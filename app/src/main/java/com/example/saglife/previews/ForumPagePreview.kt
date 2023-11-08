package com.example.saglife.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.example.saglife.component.forum.ForumPage

@Preview
@Composable
fun ForumPagePreview() {
    ForumPage(
        navController = rememberNavController(),
        id = "example_id"
    )
}
