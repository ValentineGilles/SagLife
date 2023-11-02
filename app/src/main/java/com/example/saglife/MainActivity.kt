
    package com.example.saglife
    import com.google.firebase.FirebaseApp
    import com.example.saglife.screen.CalendarScreen
    import com.example.saglife.screen.ForumScreen
    import com.example.saglife.screen.HomeScreen
    import com.example.saglife.screen.MapScreen
    import android.os.Bundle
    import androidx.activity.compose.setContent
    import androidx.activity.ComponentActivity
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
    import com.example.saglife.models.EventItem
    import com.example.saglife.screen.LoginScreen
    import com.google.firebase.Firebase
    import com.google.firebase.firestore.firestore
    import java.util.Date


    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Initialize Firebase
            FirebaseApp.initializeApp(this)

            setContent {
                val navController = rememberNavController()
                var isUserLoggedIn by remember { mutableStateOf(false) }

                if (isUserLoggedIn) {
                    // L'utilisateur est connecté, affichez les écrans principaux
                    MyApp(navController)
                } else {
                    // L'utilisateur n'est pas connecté, affichez l'écran de connexion
                    LoginScreen(onLoginClick = { isUserLoggedIn = true })
                }

            }
        }

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun MyApp(navController: NavHostController) {
        var selectedItem by remember { mutableStateOf(0) }
        val eventItems = getEventsFromFirebase()

        Scaffold(
            bottomBar = {
                BottomNavigationBar(selectedItem) {
                    selectedItem = it
                    when (it) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("calendar")
                        2 -> navController.navigate("map")
                        3 -> navController.navigate("forum")
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") { HomeScreen() }
                composable("calendar") { CalendarScreen(eventItems )}
                composable("map") { MapScreen() }
                composable("forum") { ForumScreen() }
            }
        }
    }

    fun getEventsFromFirebase(): MutableList<EventItem> {
        val db = Firebase.firestore

        var eventItems = mutableListOf<EventItem>()

        db.collection("event").get().addOnSuccessListener { result ->
            for (document in result) {
                println("${document.id} => ${document.data}")
                val name = document.get("Name").toString()
                val dateStart = Date()
                val dateEnd = Date()
                val description = document.get("Description").toString()
                val photoPath = document.get("Photo").toString()
                eventItems.add(EventItem(name, dateStart, dateEnd,description, photoPath))
            }
        }
        return eventItems
    }