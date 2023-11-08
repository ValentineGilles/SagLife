package com.example.saglife.screen.account

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

private val auth: FirebaseAuth = Firebase.auth

@Composable
fun ForgotPasswordScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithTopBarForgotPass(navController)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScaffoldWithTopBarForgotPass(navController: NavHostController) {
    Scaffold(
           content = {
            var email by remember { mutableStateOf(TextFieldValue()) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(60.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.saglife_logo_purple),
                    contentDescription = "Logo de l'application",
                    modifier = Modifier
                        .width(200.dp)
                        .height(200.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                //Text(text = "Réinitialiser le mot de passe", style = TextStyle(fontSize = 20.sp), modifier = Modifier.padding(bottom = 16.dp))

                Spacer(modifier = Modifier.height(20.dp))
                TextField(
                    label = { Text(text = "Adresse e-mail") },
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

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
        })
}