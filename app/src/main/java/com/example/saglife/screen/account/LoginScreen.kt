package com.example.saglife.screen.account

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


private val auth: FirebaseAuth = Firebase.auth

@Composable
fun LoginScreen(navController: NavHostController) {
    // État des champs de texte pour l'e-mail et le mot de passe
    var email by remember { mutableStateOf(TextFieldValue("saglifeapp@gmail.com")) }
    var password by remember { mutableStateOf(TextFieldValue("UserTest")) }

    // État pour afficher les messages d'erreur de connexion
    var ConnectionError by remember { mutableStateOf<String?>(null) }

    // Boîte avec un fond pour contenir le texte "S'inscrire" en bas de l'écran
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        ClickableText(
            text = AnnotatedString("S'inscrire"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = {
                navController.navigate("registration")
            },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }

    // Colonne contenant les éléments de l'écran de connexion
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

        Text(text = "SagLife", style = TextStyle(fontSize = 30.sp), modifier = Modifier.padding(bottom = 16.dp))

        Spacer(modifier = Modifier.height(20.dp))

        // Champ de texte pour l'adresse e-mail
        TextField(
            label = { Text(text = "Adresse e-mail") },
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Champ de texte pour le mot de passe (avec masquage du texte)
        TextField(
            label = { Text(text = "Mot de passe") },
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Affiche le message d'erreur de connexion s'il y en a un
        ConnectionError?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 0.dp, bottom = 20.dp))
        }

        // Bouton de connexion
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = {
                    // Valide la connexion en utilisant les informations saisies
                    isValidLogin(email.text, password.text, navController) { success, errorMessage ->
                        if (!success) {
                            // Affichez le message d'erreur de connexion
                            ConnectionError =
                                errorMessage ?: "Une erreur inconnue s'est produite"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Se connecter", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Texte "Mot de passe oublié ?" avec un lien pour réinitialiser le mot de passe
        ClickableText(
            text = AnnotatedString("Mot de passe oublié ?"),
            onClick = { navController.navigate("forgotten") },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

// Fonction pour valider la connexion
fun isValidLogin(email: String, password: String, navController: NavHostController, onLoginComplete: (success: Boolean, errorMessage: String?) -> Unit) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Authentification réussie, naviguez vers l'écran d'accueil
                navController.navigate(Routes.Home.route)
                onLoginComplete(true, null)
            } else {
                val exception = task.exception
                if (exception != null) {
                    // Afficher l'origine de l'erreur
                    println("Échec lors de la connexion : ${exception.message}")
                    onLoginComplete(false, exception.message)
                } else {
                    // Si l'exception est null, affichez un message générique
                    println("Échec lors de la connexion")
                    onLoginComplete(false, "Une erreur inconnue s'est produite")
                }
            }
        }
}

