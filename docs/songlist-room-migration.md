# Songlist DataStore → Room 迁移方案

> 状态：待执行 | 日期：2026-01-20 | 目标：将歌曲列表持久化从 DataStore JSON 迁移到 Room 数据库，同时在导入阶段建立索引和预计算，最大化 Room 架构带来的性能收益。

---

## 一、背景

当前歌曲列表（Chunithm / Maimai DX 曲目、Maimai 分类、Maimai 版本）通过 `CFQPersistentLoader` 以 JSON 字符串形式存储在 `DataStore<Preferences>` 中，解析后的列表保存在 `CFQPersistentData` 全局单例的内存变量中供全应用访问。

**现有痛点：**
- JSON 缓存无结构查询能力，所有操作依赖内存遍历（`filter`、`firstOrNull`、`indexOf`）
- `addAuxiliaryData()` 中 `associatedMusicEntry()` 和 `rating()` 对每一条 score 做 O(n) 线性搜索，用户如果有 800 首 best 则执行 ~3000 次搜索
- 无类型安全的数据访问
- 版本校验逻辑与缓存逻辑耦合在通用加载器中
- 全局可变单例缺乏响应式观察能力

---

## 二、架构对比

```
当前 (DataStore)                          目标 (Room)
─────────────────────                     ─────────────────────
CFQServer (API)                           CFQServer (API)
      │                                         │
      ▼                                         ▼
CFQPersistentLoader                     SongListRepository
  ├─ 版本校验 (statResourceVersion)        ├─ 版本校验 (statResourceVersion)
  ├─ DataStore JSON 缓存                   ├─ Room 持久化
  └─ JSON.decode → List<T>                 ├─ 内存 StateFlow<List<T>>
      │                                    ├─ 内存 StateFlow<Map<Int, T>>  ← 新增 O(1) 索引
      ▼                                    └─ loadFilterResources()
CFQPersistentData (全局单例)                    │
  Maimai.musicList: var                       ▼
  Maimai.genreList: var                 Compose ViewModels
  Maimai.versionList: var                   └─ collectAsState() / .value
  Chunithm.musicList: var
      │
      ▼
10+ 消费者直接读全局变量
  associatedMusicEntry() → O(n) 线性搜索
  rating() → 又一次 O(n) 搜索
```

**核心设计原则：Room 是持久化层，内存 StateFlow + Map 索引是查询层。** 消费者永远从内存取值，Room 只在启动和刷新时读写磁盘。

---

## 三、数据模型映射

旧模型为 `@Serializable` data class，新 Entity 保留相同字段但改为 Room 注解。嵌套对象通过 TypeConverter 序列化为 JSON 列。

### 3.1 ChunithmMusicEntryEntity

| 列名 | 类型 | 说明 |
|---|---|---|
| `musicId` | Int (PK) | 曲目 ID |
| `title` | String | 曲名 |
| `artist` | String | 艺人 |
| `genre` | String | 分类 |
| `bpm` | Int | BPM |
| `from` | String | 所属版本 |
| `charts` | String (JSON) | ChunithmMusicCharts 序列化 |

### 3.2 MaimaiMusicEntryEntity

| 列名 | 类型 | 说明 |
|---|---|---|
| `musicId` | Int (PK) | 曲目 ID |
| `title` | String | 曲名 |
| `type` | String | 类型 (SD/DX) |
| `constants` | String (JSON) | `List<Double>` |
| `level` | String (JSON) | `List<String>` |
| `charts` | String (JSON) | `List<MaimaiChartEntry>` |
| `coverId` | Int | 封面 ID |
| `basicInfo` | String (JSON) | MaimaiBasicInfoEntry |

### 3.3 MaimaiGenreEntryEntity / MaimaiVersionEntryEntity

扁平结构，无需 TypeConverter。

### 3.4 Model 层新增 `cachedConstant` 字段

五类 score entry 目前有 `@Transient var associatedMusicEntry`，在 `addAuxiliaryData()` 中通过线性搜索填充。新增一个 `@Transient var cachedConstant: Double` 字段，在填充 `associatedMusicEntry` 的同时一次性预计算好 rating 常量，让 `rating()` 变成纯计算。

```kotlin
// UserMaimaiBestScoreEntry 示例 — 其余四个同理
@Transient var associatedMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry()
@Transient var cachedConstant: Double = 0.0  // 新增，由 addAuxiliaryData 填充
```

涉及文件：
- `model/user/maimai/UserMaimaiBestScoreEntry.kt`
- `model/user/maimai/UserMaimaiRecentScoreEntry.kt`
- `model/user/chunithm/UserChunithmBestScoreEntry.kt`
- `model/user/chunithm/UserChunithmRecentScoreEntry.kt`
- `model/user/chunithm/UserChunithmRatingListEntry.kt`

---

## 四、文件变更清单

### 新建文件 (7)

| # | 文件路径 | 说明 |
|---|---|---|
| 1 | `storage/db/ChafenqiDatabase.kt` | Room `@Database` 类 + DAO 接口，单例模式 |
| 2 | `storage/db/TypeConverters.kt` | 嵌套对象 ↔ JSON 转换器 |
| 3 | `storage/db/entity/ChunithmMusicEntryEntity.kt` | Chunithm 曲目 Entity |
| 4 | `storage/db/entity/MaimaiMusicEntryEntity.kt` | Maimai 曲目 Entity |
| 5 | `storage/db/entity/MaimaiGenreEntryEntity.kt` | Maimai 分类 Entity |
| 6 | `storage/db/entity/MaimaiVersionEntryEntity.kt` | Maimai 版本 Entity |
| 7 | `storage/repository/SongListRepository.kt` | 仓库层：DAO 封装 + 网络拉取 + 版本校验 + Map 索引 + 批量填充 |

### 修改文件 (16)

| # | 文件 | 变更内容 |
|---|---|---|
| 1 | `app/build.gradle.kts` | 添加 Room / KSP 依赖 |
| 2 | `ChafenqiApplication.kt` | 初始化 Room 数据库单例 |
| 3 | `LoginPageViewModel.kt` | 替换 `loadPersistentStorage()` 实现 |
| 4 | `SettingsAdvancedPage.kt` | 替换歌曲列表重载逻辑 |
| 5 | `SongListPageViewModel.kt` | 改用 repository StateFlow |
| 6 | `HomeTeamCourseSettingViewModel.kt` | 默认状态改用空列表 |
| 7-11 | 五个 score entry model | 新增 `@Transient var cachedConstant` 字段 |
| 12 | `ClassExtensions.kt` | 删除 `associatedMusicEntry()` 系列；`rating()` 改为读 `cachedConstant`；拆出纯工具函数 |
| 13 | `EntryNavigator.kt` | 函数签名增加列表参数 |
| 14 | `CFQUser.kt` | `addAuxiliaryData()` 改为接收 `SongListRepository`，一次遍历填充两个字段 |
| 15 | `HomeTeamPageViewModel.kt` | 改用 repository 查找 |
| 16 | `HomePageViewModel.kt` / `HomeRatingPageViewModel.kt` / `RecentDetailPageViewModel.kt` / `SongStatsPageViewModel.kt` | 改用 repository 查找 |

### 删除文件 (2)

| # | 文件 | 说明 |
|---|---|---|
| 1 | `storage/persistent/CFQPersistentLoader.kt` | 被 SongListRepository 取代 |
| 2 | `storage/persistent/CFQPersistentData.kt` | 被 SongListRepository 取代 |

---

## 五、执行步骤

### step-1：添加 Room 依赖

**文件：** `app/build.gradle.kts`

在 `dependencies` 块添加：

```kotlin
val roomVersion = "2.7.1"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")
ksp("androidx.room:room-compiler:$roomVersion")
```

同时在 `proguard-rules.pro` 中添加 Room 标准 keep 规则。

**验证：** `grep room app/build.gradle.kts` 可看到新增依赖行。

---

### step-2：创建 Room Entity 和 TypeConverter

**新建文件：** `storage/db/entity/*.kt` + `storage/db/TypeConverters.kt`

- 每个 Entity 的嵌套对象列用 `@ColumnInfo` 标注，TypeConverter 做 `String` ↔ 嵌套类型互转
- TypeConverter 复用项目已有的 `Json { ignoreUnknownKeys = true }` 实例
- 保留 `@Keep` 注解避免 R8 误删
- `ChunithmMusicChartObjectInfo`、所有 chart 子类及 `MaimaiChartEntry` 本身无需成为 Entity——仅作为 TypeConverter 的反序列化目标，保持普通 data class

---

### step-3：创建 DAO 和 Database 类

**新建文件：** `storage/db/ChafenqiDatabase.kt`（包含 DAO 接口）

```kotlin
@Dao
interface SongListDao {
    @Query("SELECT * FROM maimai_music")
    fun getAllMaimaiMusic(): Flow<List<MaimaiMusicEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMaimaiMusic(entries: List<MaimaiMusicEntryEntity>)

    @Query("DELETE FROM maimai_music")
    suspend fun deleteAllMaimaiMusic()

    @Query("SELECT COUNT(*) FROM maimai_music")
    suspend fun getMaimaiMusicCount(): Int
    // ... 其余三个实体同理
}

@Database(entities = [...], version = 1, exportSchema = false)
@TypeConverters(TypeConverters::class)
abstract class ChafenqiDatabase : RoomDatabase() {
    abstract fun songListDao(): SongListDao

    companion object {
        @Volatile private var INSTANCE: ChafenqiDatabase? = null
        fun getInstance(context: Context): ChafenqiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(context, ChafenqiDatabase::class.java, "chafenqi.db")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
```

---

### step-4：创建 SongListRepository

**新建文件：** `storage/repository/SongListRepository.kt`

这是迁移的核心类，承担以下职责：

#### 4.1 暴露 Flow（列表 + Map 索引双通道）

```kotlin
class SongListRepository(private val dao: SongListDao, private val context: Context) {

    // 列表形式 — 给过滤、遍历、Compose LazyColumn 用
    private val _maimaiMusicList = MutableStateFlow<List<MaimaiMusicEntry>>(emptyList())
    val maimaiMusicList: StateFlow<List<MaimaiMusicEntry>> = _maimaiMusicList.asStateFlow()

    // Map 索引 — 给按 musicId 查找用（O(1)）
    private val _maimaiMusicMap = MutableStateFlow<Map<Int, MaimaiMusicEntry>>(emptyMap())
    val maimaiMusicMap: StateFlow<Map<Int, MaimaiMusicEntry>> = _maimaiMusicMap.asStateFlow()

    // Maimai 分类/版本
    private val _maimaiGenreList = MutableStateFlow<List<MaimaiGenreEntry>>(emptyList())
    val maimaiGenreList: StateFlow<List<MaimaiGenreEntry>> = _maimaiGenreList.asStateFlow()

    private val _maimaiVersionList = MutableStateFlow<List<MaimaiVersionEntry>>(emptyList())
    val maimaiVersionList: StateFlow<List<MaimaiVersionEntry>> = _maimaiVersionList.asStateFlow()

    // Chunithm 同理
    private val _chunithmMusicList = MutableStateFlow<List<ChunithmMusicEntry>>(emptyList())
    val chunithmMusicList: StateFlow<List<ChunithmMusicEntry>> = _chunithmMusicList.asStateFlow()

    private val _chunithmMusicMap = MutableStateFlow<Map<Int, ChunithmMusicEntry>>(emptyMap())
    val chunithmMusicMap: StateFlow<Map<Int, ChunithmMusicEntry>> = _chunithmMusicMap.asStateFlow()
}
```

#### 4.2 refreshFromServer()

```
refreshFromServer(shouldValidate: Boolean)
  ├─ [可选] 远程版本校验 (statResourceVersion)
  ├─ 调用 CFQServer.api*() 拉取 JSON
  ├─ Json.decodeFromString() → 得到现有 model 对象
  ├─ 转换为 Entity → Room DAO.insertAll (REPLACE 策略)
  ├─ 更新 StateFlow list → _maimaiMusicList.value = ...
  ├─ 构建 Map 索引 → _maimaiMusicMap.value = maiList.associateBy { it.musicId }
  └─ loadFilterResources() — 填充全局分类/版本字符串常量
```

**Map 索引是本次迁移的最大性能收益：** `associateBy` 一次 O(n) 建表，后续所有 `associatedMusicEntry` 查找从 O(n) 线性搜索变为 O(1) HashMap 取值。

#### 4.3 批量填充方法（替代扩展函数）

```kotlin
fun attachMaimaiBestEntries(scores: List<UserMaimaiBestScoreEntry>) {
    val map = _maimaiMusicMap.value
    for (score in scores) {
        val entry = map[score.musicId] ?: MaimaiMusicEntry()
        score.associatedMusicEntry = entry
        score.cachedConstant = entry.constants.getOrElse(score.levelIndex) { 0.0 }
    }
}

fun attachChunithmBestEntries(scores: List<UserChunithmBestScoreEntry>) {
    val map = _chunithmMusicMap.value
    for (score in scores) {
        val entry = map[score.musicId] ?: ChunithmMusicEntry()
        score.associatedMusicEntry = entry
        score.cachedConstant = entry.charts.constants.getOrElse(score.levelIndex) { 0.0 }
    }
}
// ... recent / ratingList 同理
```

#### 4.4 DataStore → Room 迁移

```kotlin
private suspend fun migrateFromDataStoreIfNeeded() {
    if (dao.getMaimaiMusicCount() > 0) return  // 已迁移

    val store = context.settingsStore
    val maiJson = store.data.map {
        it[stringPreferencesKey("maimaiMusicList")] ?: ""
    }.first()
    if (maiJson.isNotEmpty()) {
        val list: List<MaimaiMusicEntry> = Json.decodeFromString(maiJson)
        dao.insertAllMaimaiMusic(list.toEntities())
    }
    // ... 其余三种数据同理
    // 失败时由 refreshFromServer() 兜底
}
```

---

### step-5：给 Model 添加 `cachedConstant` 字段

**修改 5 个 model 文件：**

在每个 score entry 的 `@Transient var associatedMusicEntry` 下方新增一行：

```kotlin
@Transient var associatedMusicEntry: MaimaiMusicEntry = MaimaiMusicEntry()
@Transient var cachedConstant: Double = 0.0      // 新增
```

目标文件：
- `model/user/maimai/UserMaimaiBestScoreEntry.kt`
- `model/user/maimai/UserMaimaiRecentScoreEntry.kt`
- `model/user/chunithm/UserChunithmBestScoreEntry.kt`
- `model/user/chunithm/UserChunithmRecentScoreEntry.kt`
- `model/user/chunithm/UserChunithmRatingListEntry.kt`

---

### step-6：接入 Application 和 LoginPageViewModel

**修改文件：**
- `ChafenqiApplication.kt` — 添加 `lateinit var repository: SongListRepository`，在 `onCreate()` 中初始化
- `LoginPageViewModel.kt` — 将 `loadPersistentStorage()` 改为调用 `repository.refreshFromServer()`
- `SettingsAdvancedPage.kt` — 将旧 `CFQPersistentData.clearData/loadData` 替换为 `repository.refreshFromServer()`

---

### step-7：重构 SongListPageViewModel 及 UI 层

- `SongListPageViewModel` — `origMaiMusicList` / `origChuMusicList` 改为从 `repository.maimaiMusicList.value` 初始化；`update()` 中的过滤逻辑改用 `.value`
- `SongListUiState` — 默认值改为 `emptyList()`
- `HomeTeamCourseSettingViewModel` — 默认状态中的旧引用替换为 `emptyList()` 或由调用方传入

---

### step-8：重构 ClassExtensions.kt — 最大化 Room 优势

这是整个迁移中收益最大的一步。`ClassExtensions.kt` 目前有 33 个函数：

| 类别 | 数量 | 典型函数 | 处置 |
|---|---|---|---|
| 直接依赖 `CFQPersistentData` | 5 | `associatedMusicEntry()` 系列 | **删除**，迁移到 Repository |
| 间接依赖（调用 A 类） | 4 | `rating()` 系列 | **改签名**，读 `cachedConstant` |
| 纯工具函数 | 24 | `toRateString()`, `sha256()` 等 | **不动** |

#### 8.1 删除 `associatedMusicEntry()`（5 个）

这些函数迁移为 `SongListRepository` 的批量填充方法（见 step-4.3）：

| 原函数 | 新位置 |
|---|---|
| `UserMaimaiBestScoreEntry.associatedMusicEntry()` | `SongListRepository.attachMaimaiBestEntries()` |
| `UserMaimaiRecentScoreEntry.associatedMusicEntry()` | 合并到上述方法 |
| `UserChunithmBestScoreEntry.associatedMusicEntry()` | `SongListRepository.attachChunithmBestEntries()` |
| `UserChunithmRecentScoreEntry.associatedMusicEntry()` | 合并到上述方法 |
| `UserChunithmRatingListEntry.associatedMusicEntry()` | 合并到上述方法 |

#### 8.2 修改 `rating()`（4 个）

从"内部调用 associatedMusicEntry 做 O(n) 搜索"改为"直接读 cachedConstant 做纯计算"：

```kotlin
// 修改前
fun UserMaimaiBestScoreEntry.rating(): Int =
    maimaiRatingOf(this.associatedMusicEntry.constants[this.levelIndex], this.achievements)
    //                        ↑ O(n) 搜索

// 修改后
fun UserMaimaiBestScoreEntry.rating(): Int =
    maimaiRatingOf(this.cachedConstant, this.achievements)
    //               ↑ 直接读字段，纯计算
```

四个 `rating()` 全部照此修改。`maimaiRatingOf()` 和 `chunithmRatingOf()` 本身是纯算法函数，不动。

#### 8.3 拆出纯工具函数

将 24 个不涉及 songlist 的函数移到 `extension/FormatExtensions.kt`（可选，降低单文件复杂度）。

---

### step-9：重构 CFQUser.addAuxiliaryData()

**修改文件：** `CFQUser.kt`

核心变更：`addAuxiliaryData(context: Context)` → `addAuxiliaryData(repo: SongListRepository)`

```kotlin
// Maimai.addAuxiliaryData 改造后
fun addAuxiliaryData(repo: SongListRepository) {
    val maiMap = repo.maimaiMusicMap.value
    val maiList = repo.maimaiMusicList.value
    if (maiMap.isEmpty()) return

    // 一次遍历，同时填充 associatedMusicEntry 和 cachedConstant
    repo.attachMaimaiBestEntries(best)
    repo.attachMaimaiRecentEntries(recent)

    // 过滤未匹配的
    best = best.filterNot { it.associatedMusicEntry == MaimaiMusicEntry() }
    recent = recent.filterNot { it.associatedMusicEntry == MaimaiMusicEntry() }

    // rating() 现在纯计算，无额外搜索开销
    val pastList = maiList.filter { !it.basicInfo.isNew }
    val (pastBest, newBest) = best.partition { bestEntry ->
        maiMap.containsKey(bestEntry.musicId) && pastList.any { it.musicId == bestEntry.musicId }
    }

    Aux.pastBest = pastBest.sortedByDescending { it.rating() }.take(35)
    Aux.newBest = newBest.sortedByDescending { it.rating() }.take(15)
    // ... levelInfo 统计不变（associatedMusicEntry 已预填充）
}

// Chunithm.addAuxiliaryData 同理
```

**收益量化：**
- 迁移前：~3000 次 O(n) 线性搜索 × ~400 次比较/次 = ~1,200,000 次比较
- 迁移后：~3000 次 O(1) HashMap.get + `associateBy` 一次建表开销
- 调用方：`LoginPageViewModel` 和 `HomePageViewModel` 两处

---

### step-10：重构其余消费者

| 文件 | 变更方式 |
|---|---|
| `EntryNavigator.kt` | 所有 `navigateTo*` 函数增加 `repo: SongListRepository` 参数；内部改为 `repo.maimaiMusicMap.value[entry.musicId]` 取值 |
| `HomeTeamPageViewModel.kt` | `getCoverPath()` / `getTitle()` / `getArtist()` 中 `CFQPersistentData.*.musicList` 替换为 `repo.maimaiMusicMap.value[id]` |
| `HomePageViewModel.kt` | `navigateToMusicEntry()` 中 `CFQPersistentData.*.musicList.indexOf()` → 改用 `repo` 传参 |
| `HomeRatingPageViewModel.kt` | 同上 |
| `RecentDetailPageViewModel.kt` | `update()` 中 `CFQPersistentData.*.musicList` → `repo.*.value` |
| `SongStatsPageViewModel.kt` | 同上 |

---

### step-11：删除旧代码

- 删除 `storage/persistent/CFQPersistentLoader.kt`
- 删除 `storage/persistent/CFQPersistentData.kt`
- 验证：`search_content "CFQPersistentData|CFQPersistentLoader"` 返回空

---

### step-12：构建验证

```bash
./gradlew assembleDebug
```

确保：无编译错误、KSP 注解处理正常、R8 在 release 构建下不误删 Entity。

---

## 六、风险分析

### 🔴 风险一：消费者重构范围大，易引入回归缺陷

**影响：** 11 个文件直接引用 `CFQPersistentData` 全局单例。

**难点：** `ClassExtensions.kt` 和 `EntryNavigator.kt` 中的函数是**静态扩展函数/顶层函数**，无法通过依赖注入获取 repository。必须修改函数签名，连锁影响所有调用方。

**缓解：**
- 分三批执行：step-7（UI 层）→ step-8（扩展函数）→ step-9/10（CFQUser + 其他消费者）
- 每改完一个消费者立即编译验证，不攒到最后
- `CFQUser.addAuxiliaryData()` 的两个调用方（`LoginPageViewModel`、`HomePageViewModel`）同步改造

---

### 🟡 风险二：DataStore → Room 数据迁移

**影响：** 已有用户升级到新版本时，如果不迁移旧缓存，将被迫远程重拉全部数据。

**方案：** `SongListRepository.init()` 中实施一次性迁移：
1. 检查 Room 表是否为空（`dao.getMaimaiMusicCount() == 0`）
2. 读取 `settingsStore` 中的旧 JSON 键
3. 用现有 `kotlinx.serialization` 反序列化 → 转为新 Entity → 写入 Room
4. 迁移失败时捕获异常，回退到 `refreshFromServer()` 远程拉取

---

### 🟡 风险三：loadFilterResources 依赖链

**影响：** 全局常量（`MAIMAI_GENRE_STRINGS` 等）由 `CFQPersistentData.loadFilterResources()` 填充。

**方案：** `SongListRepository.refreshFromServer()` 完成后调用 `loadFilterResources()`，从内存 StateFlow 重新计算。本次保持全局变量模式不变。

---

### 🟡 风险四：Model 新增 `cachedConstant` 字段

**影响：** 5 个 `@Serializable` data class 增加 `@Transient var` 字段。`@Transient` 确保不参与序列化，不影响 API 反序列化。

**注意：** 如果这些 model 在其他地方被手动构造（而非仅从 JSON 反序列化），需要确认无 `==` 比较或 `copy()` 依赖新字段。

**缓解：** `cachedConstant` 默认值为 `0.0`，`associatedMusicEntry` 默认值本来就是 `MaimaiMusicEntry()`。现有行为完全兼容——只是改为在 `addAuxiliaryData` 中也被填充，而非留空。

---

### 🟢 风险五：TypeConverter 序列化性能

**影响：** 嵌套 chart 对象每次 Room 读写需 JSON 序列化/反序列化。

**方案：** `SongListRepository` 内部维护内存级 StateFlow 缓存。Room 只在首次启动和 `refreshFromServer()` 时读写磁盘；消费者始终从内存读取。首次迁移写入约 1-2 秒，在现有 Loading 阶段内完成。

---

### 🟢 风险六：Room schema 未来迁移

**影响：** 游戏版本更新可能导致 Entity 字段变更。

**方案：** 歌曲列表属于**可从服务端重新拉取**的数据，使用 `fallbackToDestructiveMigration()`——数据库重建后自动触发 `refreshFromServer()`。

---

### 🟢 风险七：APK 体积和构建时间

- Room 库增量约 **500 KB - 1 MB**（R8 压缩后），项目已启用 minify/shrink
- KSP 增量编译约 **2-5 秒**，Gradle configuration-cache 可部分抵消

---

## 七、关键设计决策

### 为什么嵌套对象用 TypeConverter 而不是独立表？

Chunithm 一首歌有 6 张谱面，每张谱面有 6 个 `objectInfo` 字段。如果全部拆成独立表：
- `chunithm_music` 1:6 → `chunithm_chart` 1:1 → `chunithm_chart_object_info`
- 读一首歌 JOIN 3 张表，写一首歌 INSERT 3 张表

对于"全量加载 → 内存过滤"的使用模式，消费者永远需要完整数据。JSON 列保持完整性，避免过度规范化。

### 为什么保留 StateFlow 内存缓存而不是直接用 Room Flow？

Room 的 `Flow<List<T>>` 理论可行，但当前消费者模式是**命令式同步读取**（`list.firstOrNull { ... }`）。直接切换到 Flow 需大规模改造为 `collect {}` 模式。StateFlow 作为桥梁，消费者从 `.value` 读取，行为完全一致。

### 为什么新增 Map 索引而不是用 Room 的 @Query 逐条查？

如果每次 `associatedMusicEntry` 都调用 `dao.getMaimaiMusicById(id)`，800 条 best 就需要 800 次 SQLite 查询——比当前的线性搜索还慢。Map 索引在 `refreshFromServer()` 时一次建表（`associateBy`），后续全是 O(1) 内存操作，这才是正确的优化方向。

### 为什么加 `cachedConstant` 而不是让 `rating()` 接受参数？

`rating()` 改为接受 `constant: Double` 参数也是可行的，但会连锁修改所有调用方（`sortedByDescending { it.rating() }` 变成 `sortedByDescending { it.rating(constant) }`）。预填 `cachedConstant` 让 `rating()` 保持零参数签名，调用方代码完全不变。

---

## 八、ClassExtensions.kt 函数逐一处置

### 删除（迁移到 SongListRepository）

| 函数 | 新形态 |
|---|---|
| `UserMaimaiBestScoreEntry.associatedMusicEntry()` | `repo.attachMaimaiBestEntries()` |
| `UserMaimaiRecentScoreEntry.associatedMusicEntry()` | `repo.attachMaimaiRecentEntries()` |
| `UserChunithmBestScoreEntry.associatedMusicEntry()` | `repo.attachChunithmBestEntries()` |
| `UserChunithmRecentScoreEntry.associatedMusicEntry()` | `repo.attachChunithmRecentEntries()` |
| `UserChunithmRatingListEntry.associatedMusicEntry()` | `repo.attachChunithmRatingEntries()` |

### 修改签名

| 函数 | 修改 |
|---|---|
| `UserMaimaiBestScoreEntry.rating(): Int` | `maimaiRatingOf(this.cachedConstant, this.achievements)` |
| `UserChunithmBestScoreEntry.rating(): Double` | `chunithmRatingOf(this.cachedConstant, this.score)` |
| `UserChunithmRecentScoreEntry.rating(): Double` | 同上 |
| `UserChunithmRatingListEntry.rating(): Double` | 同上 |

### 保持不变（24 个）

`toRateString()`, `toMaimaiCoverPath()`, `toChunithmCoverPath()`, `sha256()`, `prepended()`, `conditional()`, `toHalfWidth()`, `toDateString()`, `toMonthDayString()`, `toYMDString()`, `toMaimaiLevelString()`, `toMaimaiCoverString()`, `maimaiRatingOf()`, `chunithmRatingOf()`, `cutForRating()`, `toLevelIndex()`, `toMaimaiTrophyType()`, `toChunithmTrophyType()`, `toGameTypeString()`, `toChunithmDifficultyString()`, 以及 `MAIMAI_RATING_FACTOR`

---

## 九、验证检查点

| 检查点 | 对应步骤 | 验证方法 |
|---|---|---|
| Room 编译正常 | step-1 ~ step-3 | `./gradlew :app:compileDebugKotlin` |
| 网络拉取和缓存 | step-4 | 断开网络后重启 App，歌曲列表仍可用 |
| Entity 字段变更 | step-5 | API 反序列化后 `cachedConstant` 为 0.0（默认值） |
| 登录加载流程 | step-6 | 登录后歌曲列表正确加载，过滤/搜索可用 |
| UI 层重构 | step-7 | 曲目列表、搜索、过滤功能不受影响 |
| 扩展函数 + addAuxiliaryData | step-8 ~ step-9 | Best Score / Recent 关联曲目信息正常显示；Rating 计算正确；推荐列表正常 |
| 其余消费者 | step-10 | 导航、队伍页、统计页功能正常 |
| 旧代码清理 | step-11 | `search_content CFQPersistentData` 返回空 |
| 完整构建 | step-12 | `./gradlew assembleDebug` 成功；release 构建通过 R8 |
