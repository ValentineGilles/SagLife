package com.example.saglife
import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(navController: NavHostController, title: String, showBackIcon: Boolean) {
    val colors = MaterialTheme.colorScheme
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = @Composable {
            IconButton(onClick = { navController.navigate(Routes.Login.route) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Back"
                )
            }
        }
    )
}
