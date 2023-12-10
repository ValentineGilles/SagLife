package com.example.saglife.screen.account

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.database.getDescriptionFromUid
import com.example.saglife.database.getProfilePicFromUid
import com.google.firebase.auth.FirebaseAuth

private val auth = FirebaseAuth.getInstance()

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(navController: NavHostController) {
    var profilePicture by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    getProfilePicFromUid(auth.uid.toString()) { profilePic ->
        profilePicture = profilePic
    }

    getDescriptionFromUid(auth.uid.toString()) { descriptionUser ->
        description = descriptionUser
    }

    description = "Je suis un utilisateur de SagLife."

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Photo de profil dans un cercle
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            AsyncImage(
                model = profilePicture,
                contentDescription = "Profile picture",
                modifier = Modifier.fillMaxSize()
            )
            // Icone de crayon pour modifier la photo de profil
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Modifier Photo de profil",
                modifier = Modifier
                    .size(5.dp)
                    .padding(4.dp)
            )
        }

        // Nom d'utilisateur
        Text(
            text = auth.currentUser?.displayName.toString(),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp)
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))
        //Ligne fine de séparation
        Spacer(modifier = Modifier.height(1.dp).background(MaterialTheme.colorScheme.primary))

        Spacer(modifier = Modifier.height(16.dp))

        // Paramètres avec Icone et texte
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
                .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically
            ) {

                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Icone de paramètres",
                        modifier = Modifier.size(30.dp)
                    )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Paramètres",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 16.dp, start = 16.dp, bottom = 16.dp)
                .shadow(4.dp, shape = RoundedCornerShape(8.dp)),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
            onClick = {
                auth.signOut() // Déconnexion de l'utilisateur
                navController.navigate(Routes.Login.route)}
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),

                verticalAlignment = Alignment.CenterVertically,

            ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Icone de déconnexion",
                        modifier = Modifier.size(30.dp)
                    )
                Spacer(modifier = Modifier.width(20.dp))
                Text(
                    text = "Déconnexion",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
