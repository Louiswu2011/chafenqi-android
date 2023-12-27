package com.nltv.chafenqi.view.songlist

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nltv.chafenqi.extension.CHUNITHM_GENRE_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_LEVEL_STRINGS
import com.nltv.chafenqi.extension.CHUNITHM_VERSION_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_GENRE_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_LEVEL_STRINGS
import com.nltv.chafenqi.extension.MAIMAI_VERSION_STRINGS
import com.nltv.chafenqi.extension.toLevelIndex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListLevelFilterDialog() {
    val model: SongListPageViewModel = viewModel()
    val levelStrings = if (model.user.mode == 0) CHUNITHM_LEVEL_STRINGS else MAIMAI_LEVEL_STRINGS
    val filterLevelList = if (model.user.mode == 0) model.filterChunithmLevelList else model.filterMaimaiLevelList
    val gridState = rememberLazyGridState()

    if (model.showFilterLevelDialog) {
        AlertDialog(onDismissRequest = { model.showFilterLevelDialog = false }) {
            Surface (
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "筛选等级", style = MaterialTheme.typography.titleLarge, color = AlertDialogDefaults.textContentColor)
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(80.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        state = gridState
                    ) {
                        items(levelStrings) { level ->
                            Row (
                                Modifier
                                    .toggleable(
                                        value = filterLevelList[level.toLevelIndex(model.user.mode)],
                                        onValueChange = {
                                            filterLevelList[level.toLevelIndex(model.user.mode)] =
                                                it
                                        },
                                        role = Role.Checkbox
                                    )
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = filterLevelList[level.toLevelIndex(model.user.mode)],
                                    onCheckedChange = null
                                )
                                Text(text = level)
                            }
                        }
                    }
                    TextButton(onClick = {
                        repeat(levelStrings.size) {
                            filterLevelList[it] = false
                        }
                    }) {
                        Text(text = "重置")
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            repeat(levelStrings.size) {
                                filterLevelList[it] = false
                            }
                            model.showFilterLevelDialog = false
                        }) {
                            Text(text = "取消")
                        }
                        Button(onClick = {
                            model.filterLevel = true
                            model.update()
                            model.showFilterLevelDialog = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListGenreFilterDialog() {
    val model: SongListPageViewModel = viewModel()
    val genreStrings = if (model.user.mode == 0) CHUNITHM_GENRE_STRINGS else MAIMAI_GENRE_STRINGS
    val filterGenreList = if (model.user.mode == 0) model.filterChunithmGenreList else model.filterMaimaiGenreList
    val listState = rememberLazyListState()

    if (model.showFilterGenreDialog) {
        AlertDialog(onDismissRequest = { model.showFilterGenreDialog = false }) {
            Surface (
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "筛选分类", style = MaterialTheme.typography.titleLarge, color = AlertDialogDefaults.textContentColor)
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    LazyColumn (
                        state = listState,
                        userScrollEnabled = true
                    ) {
                        items(genreStrings) { genre ->
                            Row (
                                Modifier
                                    .toggleable(
                                        value = filterGenreList[genreStrings.indexOf(genre)],
                                        onValueChange = {
                                            filterGenreList[genreStrings.indexOf(genre)] =
                                                it
                                        },
                                        role = Role.Checkbox
                                    )
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = filterGenreList[genreStrings.indexOf(genre)],
                                    onCheckedChange = null
                                )
                                Text(text = genre)
                            }
                        }
                    }
                    TextButton(onClick = {
                        repeat(genreStrings.size) {
                            filterGenreList[it] = false
                        }
                    }) {
                        Text(text = "重置")
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            repeat(genreStrings.size) {
                                filterGenreList[it] = false
                            }
                            model.showFilterGenreDialog = false
                        }) {
                            Text(text = "取消")
                        }
                        Button(onClick = {
                            model.filterGenre = true
                            model.update()
                            model.showFilterGenreDialog = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongListVersionFilterDialog() {
    val model: SongListPageViewModel = viewModel()
    val versionStrings = if (model.user.mode == 0) CHUNITHM_VERSION_STRINGS else MAIMAI_VERSION_STRINGS
    val filterVersionList = if (model.user.mode == 0) model.filterChunithmVersionList else model.filterMaimaiVersionList
    val listState = rememberLazyListState()

    if (model.showFilterVersionDialog) {
        AlertDialog(onDismissRequest = { model.showFilterVersionDialog = false }) {
            Surface (
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "筛选分类", style = MaterialTheme.typography.titleLarge, color = AlertDialogDefaults.textContentColor)
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    LazyColumn (
                        state = listState,
                        userScrollEnabled = true
                    ) {
                        items(versionStrings) { version ->
                            Row (
                                Modifier
                                    .toggleable(
                                        value = filterVersionList[versionStrings.indexOf(version)],
                                        onValueChange = {
                                            filterVersionList[versionStrings.indexOf(version)] =
                                                it
                                        },
                                        role = Role.Checkbox
                                    )
                                    .padding(5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Checkbox(
                                    checked = filterVersionList[versionStrings.indexOf(version)],
                                    onCheckedChange = null
                                )
                                Text(text = version)
                            }
                        }
                    }
                    TextButton(onClick = {
                        repeat(versionStrings.size) {
                            filterVersionList[it] = false
                        }
                    }) {
                        Text(text = "重置")
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            repeat(versionStrings.size) {
                                filterVersionList[it] = false
                            }
                            model.showFilterVersionDialog = false
                        }) {
                            Text(text = "取消")
                        }
                        Button(onClick = {
                            model.filterVersion = true
                            model.update()
                            model.showFilterVersionDialog = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                }
            }
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun SongListConstantFilterDialog() {
    val model: SongListPageViewModel = viewModel()
    var constant by rememberSaveable {
        mutableStateOf("")
    }
    var upperbound by rememberSaveable {
        mutableStateOf("")
    }
    var lowerbound by rememberSaveable {
        mutableStateOf("")
    }
    var singleConstant by rememberSaveable {
        mutableStateOf(false)
    }

    if (model.showFilterConstantDialog) {
        AlertDialog(onDismissRequest = { model.showFilterConstantDialog = false }) {
            Surface (
                shape = AlertDialogDefaults.shape,
                color = AlertDialogDefaults.containerColor,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "筛选定数", style = MaterialTheme.typography.titleLarge, color = AlertDialogDefaults.textContentColor)
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Column (
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.animateContentSize()
                    ) {
                        when (singleConstant) {
                            true -> {
                                TextField(
                                    value = constant,
                                    onValueChange = { constant = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    label = { Text(text = "定数") }
                                )
                            }
                            false -> {
                                TextField(
                                    value = lowerbound,
                                    onValueChange = { lowerbound = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    label = { Text(text = "下限") }
                                )
                                TextField(
                                    value = upperbound,
                                    onValueChange = { upperbound = it },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    label = { Text(text = "上限") }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Row (
                        Modifier.toggleable(value = singleConstant, onValueChange = { singleConstant = it }, role = Role.Checkbox),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Checkbox(checked = singleConstant, onCheckedChange = null)
                        Text(text = "单一定数")
                    }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = { model.showFilterConstantDialog = false }) {
                            Text(text = "取消")
                        }
                        Button(onClick = {
                            val numericUpperbound = upperbound.toFloatOrNull()
                            val numericLowerbound = lowerbound.toFloatOrNull()
                            val numericConstant = constant.toFloatOrNull()
                            if (singleConstant && numericConstant != null) {
                                model.filterConstantLowerBound = numericConstant
                                model.filterConstantUpperBound = numericConstant
                            } else if (numericLowerbound != null && numericUpperbound != null) {
                                model.filterConstantLowerBound = numericLowerbound
                                model.filterConstantUpperBound = numericUpperbound
                            } else {
                                return@Button
                            }

                            model.filterConstant = true
                            model.update()
                            model.showFilterConstantDialog = false
                        }) {
                            Text(text = "确定")
                        }
                    }
                }
            }
        }
    }
}