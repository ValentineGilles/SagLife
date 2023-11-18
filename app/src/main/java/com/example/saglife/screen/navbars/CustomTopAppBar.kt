package com.example.saglife.screen.navbars
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.saglife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    navController: NavHostController,
    title: String,
    showBackIcon: Boolean,
    showProfileIcon: Boolean,
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = @Composable {
            if (showBackIcon && navController.previousBackStackEntry != null) {
                IconButton(onClick = {
                    navController.navigateUp()
                    if (currentRoute == Routes.ForumPage.route)
                    {
                        navController.navigate(Routes.Forum.route)
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
            else
            {
                IconButton(onClick = {
                    auth.signOut()
                    navController.navigate(Routes.Login.route) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_logout),
                        contentDescription = "Log out"
                    )
                }
            }
        },
        actions = {
            if (showProfileIcon) {
                Spacer(modifier = Modifier.width(16.dp))
                val username = auth.currentUser?.displayName
                if(username != null)
                {
                    Text(text=username)
                }
                IconButton(onClick = { navController.navigate(Routes.Profile.route) }) {

                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
            }
        }
    )
}


