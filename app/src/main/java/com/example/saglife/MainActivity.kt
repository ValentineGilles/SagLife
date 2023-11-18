package com.example.saglife

import Routes
import android.os.Build

import com.example.saglife.screen.calendar.CalendarScreen
import com.example.saglife.screen.forum.ForumScreen
import com.example.saglife.screen.home.HomeScreen
import com.example.saglife.screen.map.MapScreen
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saglife.screen.navbars.BottomNavigationBar
import com.example.saglife.screen.forum.ForumPage
import com.example.saglife.screen.calendar.EventScreen
import com.example.saglife.screen.navbars.CustomTopAppBar
import com.example.saglife.screen.map.MapInfoScreen
import com.example.saglife.screen.account.ForgotPasswordScreen
import com.example.saglife.screen.account.LoginScreen
import com.example.saglife.screen.account.ProfileScreen
import com.example.saglife.screen.account.RegistrationScreen
import com.example.saglife.screen.calendar.EventCreate
import com.example.saglife.screen.forum.ForumCreatePost
import com.example.saglife.screen.map.MapCreate
import com.example.saglife.screen.forum.ForumModifyComment
import com.example.saglife.screen.forum.ForumModifyPost
import com.example.saglife.ui.theme.SagLifeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide();
        setContent {
            SagLifeTheme {
                val navController = rememberNavController()
                MyApp(navController)
            }

        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyApp(navController: NavHostController) {
        val currentUser = auth.currentUser
        var startpage = ""
        println("current user : $currentUser")
        if (currentUser != null) {
            startpage = Routes.Home.route
        } else {
            startpage = Routes.Login.route
        }
        var selectedItem by remember { mutableStateOf(0) }
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        // Définir une variable pour indiquer si les bars doivent être affichées
        val isTopBarVisible = remember { mutableStateOf(true) }
        val isTopBarBack = remember { mutableStateOf(true) }
        val isBottomBarVisible = remember { mutableStateOf(true) }

        Scaffold(
            topBar = {
                if (isTopBarVisible.value) {
                    if (isTopBarBack.value) {
                        CustomTopAppBar(navController, "", true, false)
                    } else {
                        CustomTopAppBar(navController, "", false, true)
                    }
                }
            },
            bottomBar = {
                if (isBottomBarVisible.value) {
                    BottomNavigationBar(selectedItem) {
                        selectedItem = it
                        when (it) {
                            0 -> navController.navigate(Routes.Home.route)
                            1 -> navController.navigate(Routes.Calendar.route)
                            2 -> navController.navigate(Routes.Map.route)
                            3 -> navController.navigate(Routes.Forum.route)
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = startpage,
                modifier = Modifier.padding(innerPadding)

            ) {
                composable(Routes.Home.route) {
                    // Affiche la TopBar et la BottomBar sur "Home"
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = false
                    HomeScreen(navController = navController)
                }
                composable(Routes.Calendar.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = false
                    CalendarScreen(navController = navController)
                }
                composable(Routes.Map.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = false
                    MapScreen(navController = navController)
                }

                composable(Routes.Forum.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = false
                    ForumScreen(navController = navController)
                }

                composable(Routes.Profile.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    ProfileScreen(navController = navController)
                }

                composable(Routes.Login.route) {
                    // Masque la TopBar et la BottomBar sur "Login"
                    isTopBarVisible.value = false
                    isBottomBarVisible.value = false
                    isTopBarBack.value = false
                    LoginScreen(
                        navController = navController
                    )

                }


                composable(Routes.MapInfo.route) { backStackEntry ->
                    isTopBarVisible.value = false
                    isBottomBarVisible.value = false
                    MapInfoScreen(
                        navController = navController,
                        backStackEntry.arguments?.getString("id")
                    )
                }
                composable(Routes.Registration.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = false
                    isTopBarBack.value = true
                    RegistrationScreen(navController = navController)
                }

                composable(Routes.Forgotten.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = false
                    isTopBarBack.value = true
                    ForgotPasswordScreen(navController = navController)
                }
                composable(Routes.Event.route) { backStackEntry ->
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = false
                    isTopBarBack.value = true
                    EventScreen(
                        navController = navController,
                        backStackEntry.arguments?.getString("id")
                    )
                }

                composable(Routes.ForumPage.route) { backStackEntry ->
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    ForumPage(
                        navController = navController,
                        backStackEntry.arguments?.getString("id")
                    )
                }

                composable(Routes.ForumCreatePost.route) { backStackEntry ->
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    ForumCreatePost(navController = navController)
                }
                composable(Routes.MapCreate.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    MapCreate(navController = navController)
                }
                composable(Routes.EventCreate.route) {
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    EventCreate(navController = navController)
                }

                    composable(Routes.ForumModifyPost.route) { backStackEntry ->
                        isTopBarVisible.value = true
                        isBottomBarVisible.value = true
                        isTopBarBack.value = true
                        ForumModifyPost(
                            navController = navController,
                            backStackEntry.arguments?.getString("id")
                        )
                    }

                    composable(Routes.ForumModifyComment.route) { backStackEntry ->
                        isTopBarVisible.value = true
                        isBottomBarVisible.value = true
                        isTopBarBack.value = true
                        ForumModifyComment(
                            navController = navController,
                            backStackEntry.arguments?.getString("post_id"),
                            backStackEntry.arguments?.getString("comment_id")
                        )
                    }
                }
            }
        }
    }


