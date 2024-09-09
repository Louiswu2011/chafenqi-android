package com.nltv.chafenqi.view.songlist.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nltv.chafenqi.data.Comment
import com.nltv.chafenqi.networking.CFQServer
import com.nltv.chafenqi.storage.persistent.CFQPersistentData
import com.nltv.chafenqi.storage.user.CFQUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CommentPageUiState(
    val loading: Boolean = false,
    val comments: List<Comment> = listOf()
)

class CommentPageViewModel: ViewModel() {
    val user = CFQUser
    private val _uiState = MutableStateFlow(CommentPageUiState())
    val uiState = _uiState.asStateFlow()

    var mode: Int = 0
    var index: Int = 0

    var replyComment: Comment? = null
    var showCommentSheet by mutableStateOf(false)

    fun update(gameType: Int, index: Int) {
        setLoading(true)
        val musicId: Int = when (gameType) {
            0 -> CFQPersistentData.Chunithm.musicList.getOrNull(index)?.musicID ?: -1
            1 -> CFQPersistentData.Maimai.musicList.getOrNull(index)?.musicID?.toInt() ?: -1
            else -> -1
        }
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    comments = CFQServer.apiFetchComment(gameType = gameType, musicId = musicId).sortedByDescending { comment -> comment.timestamp }
                )
            }
            setLoading(false)
        }
        this.mode = gameType
        this.index = index
    }

    private fun setLoading(state: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loading = state
                )
            }
        }
    }

    fun submitComment(replyId: Int = -1, content: String) {
        try {
            val musicId: Int = when (mode) {
                0 -> CFQPersistentData.Chunithm.musicList.getOrNull(index)?.musicID ?: -1
                1 -> CFQPersistentData.Maimai.musicList.getOrNull(index)?.musicID?.toInt() ?: -1
                else -> -1
            }
            viewModelScope.launch {
                CFQServer.apiPostComment(
                    authToken = user.token,
                    gameType = mode,
                    musicId = musicId,
                    replyId = replyId,
                    content = content
                )
                update(mode, index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteComment(commentId: Int) {
        try {
            viewModelScope.launch {
                CFQServer.apiDeleteComment(user.token, commentId)
                update(mode, index)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}