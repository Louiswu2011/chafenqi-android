# REASONIX.md — Chafenqi (查分器)

## Stack
- **Kotlin 2.3.20** (JVM target 17) on **Android** (minSdk 28, targetSdk 35, compileSdk 37)
- **Jetpack Compose** with Material3 (BOM 2026.06.00), Navigation Compose
- **Ktor 3.5.0** (OkHttp engine) for HTTP + kotlinx-serialization-json 1.11.0
- **Firebase** (Crashlytics, Perf, Analytics) + OneSignal 5.9.4 for push
- **Coil 3.5.0** for image loading; **Vico 2.1.3** for charts; **ComposePreferences** for settings UI
- **CMake 3.22.1** native lib `tun2http` (C, VPN/proxy capture) built per ABI (arm64-v8a, armeabi-v7a)

## Layout
- `app/` — single Android module: `src/main/java/com/nltv/chafenqi/`
  - `view/` — Compose screens + ViewModels, one subdir per screen group (`home/`, `info/`, `songlist/`, `settings/`, `login/`, `updater/`, `premium/`)
  - `model/` — API response DTOs (`user/maimai/`, `user/chunithm/`, `team/`)
  - `data/` — leaderboard types, rank types, comments, version info
  - `storage/` — local data classes (song list, user prefs, log data, persistent loader)
  - `networking/` — API client classes (CFQServer, FishServer, CFQTeamServer)
  - `updater/` — VPN proxy service (`ChafenqiProxy`) + utils
  - `tile/` — Quick Settings tile service
  - `extension/` + `util/` — shared helpers
  - `ui/theme/` — Compose theme (Color, Type, Theme)
- `app/src/main/cpp/` — tun2http native sources (tcp, udp, dns, http, tls, dhcp, icmp, ip)
- `app/src/test/` — JUnit 4 unit tests (currently only `ExampleUnitTest.kt`)
- `app/src/androidTest/` — AndroidX Test instrumented tests (currently only `ExampleInstrumentedTest.kt`)

## Commands
```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (minified + proguard)
./gradlew assembleRelease

# Unit tests (JVM)
./gradlew test

# Instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Clean
./gradlew clean
```
Build output lands in `app/build/outputs/apk/`. Version auto-increments `VERSION_BUILD` on each Gradle sync/build.

## Conventions
- **MVVM pattern**: each Compose `*Page.kt` screen has a corresponding `*ViewModel.kt` in the same package
- **Package-by-feature** under `view/`: `home/`, `info/maimai/`, `info/chunithm/`, `songlist/`, `settings/`, etc.
- **Game model split**: user data, song list, and storage classes are duplicated for Chunithm and Maimai DX (prefix `Chunithm*` / `Maimai*`)
- **Named constructor args** used on data classes per kotlinx-serialization convention (all model classes use `@Serializable`)
- **Kotlin code style** = `official` (set in `gradle.properties`)
- **APK split by ABI** (`armeabi-v7a`, `arm64-v8a`), no universal APK

## Watch out for
- **Version file**: `app/version.properties` is auto-written on every sync; `VERSION_BUILD` increments each time — don't commit it mid-session expecting a stable number
- **ktlint is DISABLED** in the IDE plugin config (`.idea/ktlint-plugin.xml`) — the JAR exists but isn't active; don't assume it enforces style
- **Lint baseline**: `app/lint-baseline.xml` suppresses existing warnings — new lint issues beyond the baseline will still fail the build
- **Cleartext traffic**: the app allows cleartext HTTP (`usesCleartextTraffic="true"`, `network_security_config.xml`) — the server address is a plain HTTP IP
- **Native build paths**: CMake intermediate files under `app/.cxx/` are generated — don't edit by hand
- **Debug build includes hardcoded test credentials** in `resValue` — never ship a debug APK publicly
