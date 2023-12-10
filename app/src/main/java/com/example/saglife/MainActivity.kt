package com.example.saglife

import Routes
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.saglife.screen.account.ForgotPasswordScreen
import com.example.saglife.screen.account.LoginScreen
import com.example.saglife.screen.account.ProfileScreen
import com.example.saglife.screen.account.RegistrationScreen
import com.example.saglife.screen.calendar.CalendarScreen
import com.example.saglife.screen.calendar.EventCreate
import com.example.saglife.screen.calendar.EventScreen
import com.example.saglife.screen.forum.ForumCreatePost
import com.example.saglife.screen.forum.ForumModifyComment
import com.example.saglife.screen.forum.ForumModifyPost
import com.example.saglife.screen.forum.ForumPage
import com.example.saglife.screen.forum.ForumScreen
import com.example.saglife.screen.home.HomeScreen
import com.example.saglife.screen.map.MapCreate
import com.example.saglife.screen.map.MapInfoScreen
import com.example.saglife.screen.map.MapScreen
import com.example.saglife.screen.navbars.BottomNavigationBar
import com.example.saglife.screen.navbars.CustomTopAppBar
import com.example.saglife.ui.theme.SagLifeTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging

private val auth: FirebaseAuth = Firebase.auth


class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient

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

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }



    @RequiresApi(Build.VERSION_CODES.M)
    @Composable
    fun MyApp(navController: NavHostController) {

        askNotificationPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                println("Fetching FCM registration token failed")
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            print("Token "+token)
        })

        Firebase.messaging.subscribeToTopic("notif")
            .addOnCompleteListener { task ->
                var msg = "Subscribed"
                if (!task.isSuccessful) {
                    msg = "Subscribe failed"
                }
                println(msg)
            }

        var clientLocation by remember { mutableStateOf(GeoPoint(48.40496419957457, -71.0574980680532)) }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val REQUEST_LOCATION_PERMISSION_CODE = 128
            requestPermissions(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), REQUEST_LOCATION_PERMISSION_CODE)
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                // Got last known location. In some rare situations this can be null.
                if(location != null){
                    val latitude = location.latitude
                    val longitude = location.longitude
                    clientLocation = GeoPoint(latitude,longitude)
                    println("Latitude: $latitude, Longitude: $longitude")
                }

            }

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
                    HomeScreen(navController = navController, clientLocation)
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
                    MapScreen(navController = navController, clientLocation)
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
                    isTopBarVisible.value = true
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    MapInfoScreen(
                        navController = navController,
                        backStackEntry.arguments?.getString("id"),
                        clientLocation
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
                    isBottomBarVisible.value = true
                    isTopBarBack.value = true
                    EventScreen(
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


