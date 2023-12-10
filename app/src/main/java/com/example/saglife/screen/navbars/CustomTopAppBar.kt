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
    // Récupération de la route actuelle de la navigation
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // Composant TopAppBar qui représente la barre d'application en haut de l'écran
    TopAppBar(
        title = {
            Text(text = title) // Affiche le titre spécifié
        },
        navigationIcon = @Composable {
            // Icône de navigation à gauche (flèche arrière) ou bouton de déconnexion en fonction de la configuration
            if (showBackIcon && navController.previousBackStackEntry != null) {
                IconButton(onClick = {
                    navController.navigateUp() // Retour en arrière dans la navigation
                }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Actions à droite de la barre d'application (icône de profil et nom d'utilisateur)
            if (showProfileIcon) {
                Spacer(modifier = Modifier.width(16.dp)) // Espace entre l'icône de profil et le nom d'utilisateur
                val username = auth.currentUser?.displayName // Récupération du nom d'utilisateur actuel
                if (username != null) {
                    Text(text = username) // Affiche le nom d'utilisateur s'il est disponible
                }
                IconButton(onClick = { navController.navigate(Routes.Profile.route) }) {
                    // Icône de profil cliquable pour accéder au profil de l'utilisateur
                    Icon(
                        painter = painterResource(id = R.drawable.ic_profile), // Icône de profil personnalisée
                        contentDescription = "Profil",
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape) // Clip l'icône pour la rendre ronde
                    )
                    Spacer(modifier = Modifier.width(10.dp)) // Espace entre l'icône de profil et le texte
                }
            }
        }
    )
}


