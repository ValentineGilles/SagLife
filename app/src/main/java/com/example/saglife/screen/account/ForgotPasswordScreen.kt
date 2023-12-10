package com.example.saglife.screen.account

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    // Crée une boîte (Box) qui remplit toute la taille de l'écran
    Box(modifier = Modifier.fillMaxSize()) {
        // Affiche le Scaffold avec la barre supérieure personnalisée pour la réinitialisation de mot de passe
        ScaffoldWithTopBarForgotPass(navController)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScaffoldWithTopBarForgotPass(navController: NavHostController) {
    // Crée un Scaffold avec une barre supérieure et un contenu personnalisé
    Scaffold(
        content = {
            var email by remember { mutableStateOf(TextFieldValue()) }

            // Colonne contenant les éléments de l'écran de réinitialisation de mot de passe
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp)
                    .verticalScroll(rememberScrollState()), // Permet le défilement vertical si nécessaire
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Affiche le logo de l'application
                Image(
                    painter = painterResource(id = R.drawable.saglife_logo_purple),
                    contentDescription = "Logo de l'application",
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Champ de texte pour l'adresse e-mail
                OutlinedTextField(
                    label = { Text(text = "Adresse e-mail") },
                    value = email,
                    onValueChange = { email = it },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bouton de réinitialisation de mot de passe
                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            // Récupère l'adresse e-mail de l'utilisateur depuis le champ TextField
                            val userEmail = email.text

                            if (userEmail.isNotEmpty()) {
                                // Utilise la fonction sendPasswordResetEmail pour envoyer un e-mail de réinitialisation
                                auth.sendPasswordResetEmail(userEmail)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            // E-mail de réinitialisation envoyé avec succès
                                            println("E-mail de réinitialisation envoyé avec succès")
                                        } else {
                                            // Erreur lors de l'envoi de l'e-mail de réinitialisation
                                            println("Erreur lors de l'envoi de l'e-mail de réinitialisation : ${task.exception?.message}")
                                        }
                                    }
                            } else {
                                // L'utilisateur n'a pas saisi d'adresse e-mail
                                println("Veuillez saisir une adresse e-mail")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "Réinitialiser", color = Color.White)
                    }
                }
            }
        }
    )
}
