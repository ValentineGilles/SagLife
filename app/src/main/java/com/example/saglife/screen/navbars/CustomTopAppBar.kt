package com.example.saglife.screen.navbars
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.database.getProfilePicFromUid
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

    var profilePicture by remember { mutableStateOf("") }
    getProfilePicFromUid(auth.uid.toString()) { profilePic ->
        if (profilePic.startsWith("https://firebasestorage.googleapis.com/"))
            profilePicture = profilePic
        else profilePicture = "https://firebasestorage.googleapis.com/v0/b/saglife-94b7c.appspot.com/o/profile_pic%2Fblank-profile-picture-image-holder-with-a-crown-vector-42411540.jpg?alt=media&token=368f0c1f-8e2a-46c2-a253-640287f74515"

    }
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
                    Text(text = username, style = MaterialTheme.typography.labelLarge) // Affiche le nom d'utilisateur s'il est disponible
                }
                Spacer(modifier = Modifier.width(10.dp)) // Espace entre l'icône de profil et le texte


                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .clickable { navController.navigate(Routes.Profile.route) }
                ) {
                    AsyncImage(
                        model = profilePicture,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp)) // Espace entre l'icône de profil et le texte
            }
        }
    )
}


