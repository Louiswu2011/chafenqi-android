package com.nltv.chafenqi.view.songlist.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nltv.chafenqi.data.Comment

@Composable
fun CommentCard(comment: Comment) {
    Card (
        modifier = Modifier.size(width = 300.dp, height = 80.dp)
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.fillMaxSize()
                .padding(8.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = comment.username, fontWeight = FontWeight.Bold)
                Text(text = comment.dateString, style = MaterialTheme.typography.bodySmall)
            }
            Text(text = comment.content, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}