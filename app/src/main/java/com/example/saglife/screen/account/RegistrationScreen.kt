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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


private val auth: FirebaseAuth = Firebase.auth

@Composable
fun RegistrationScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithTopBar(navController)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ScaffoldWithTopBar(navController: NavHostController) {
    Scaffold(
        content = {
            var username by remember { mutableStateOf(TextFieldValue()) }
            var password by remember { mutableStateOf(TextFieldValue()) }
            var password2 by remember { mutableStateOf(TextFieldValue()) }
            var email by remember { mutableStateOf(TextFieldValue()) }

            var ConnectionError by remember { mutableStateOf<String?>(null) }

            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)

                ) {
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    ,
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.saglife_logo_purple),
                    contentDescription = "Logo de l'application",
                    modifier = Modifier
                        .width(120.dp)
                        .height(120.dp)
                )

                Text(text = "Inscription", style = TextStyle(fontSize = 20.sp), modifier = Modifier.padding(bottom = 16.dp))

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    shape = RoundedCornerShape(8.dp),
                    label = { Text(text = "Nom d'utilisateur") },
                    value = username,
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent),
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextField(
                    shape = RoundedCornerShape(8.dp),
                    label = { Text(text = "Adresse E-mail") },
                    value = email,
                    onValueChange = { email = it },
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    label = { Text(text = "Mot de passe") },
                    value = password,
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    shape = RoundedCornerShape(8.dp),
                    label = { Text(text = "Confirmer vot mot de passe") },
                    value = password2,
                    colors = TextFieldDefaults.textFieldColors(
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { password2 = it },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                ConnectionError?.let {
                    Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 0.dp, start = 60.dp, end = 60.dp, bottom = 20.dp))
                }

                Box(modifier = Modifier.padding(40.dp, 8.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            if (password.text.isNotEmpty() && email.text.isNotEmpty() && username.text.isNotEmpty()) {
                                if (password2 == password) {
                                    signUpWithFirebase(
                                        email.text,
                                        password.text,
                                        username.text,
                                        navController
                                    ) { success, errorMessage ->
                                        if (!success) {
                                            // Affichez le message d'erreur sur password2Error
                                            ConnectionError =
                                                errorMessage ?: "Une erreur inconnue s'est produite"
                                        }
                                    }
                                } else {
                                    // Affichez un message d'erreur si les mots de passe ne correspondent pas
                                    ConnectionError = "Les deux mots de passe ne correspondent pas."
                                }
                            }
                            else
                            {
                                ConnectionError = "Veuillez remplir tous les champs."
                            }
                                  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "S'inscrire", color = Color.White)
                    }

            } }

        })
}



// Fonction pour créer un utilisateur avec Firebase
fun signUpWithFirebase(
    email: String,
    password: String,
    username: String,
    navController: NavHostController,
    onSignUpComplete: (success: Boolean, errorMessage: String?) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // L'utilisateur a été créé avec succès
                val user = auth.currentUser
                val userProfileChangeRequest = UserProfileChangeRequest.Builder()
                    .setDisplayName(username)
                    .build()
                user?.updateProfile(userProfileChangeRequest)
                    ?.addOnCompleteListener { profileUpdateTask ->
                        if (profileUpdateTask.isSuccessful) {
                            addUserToDatabase(user.uid, username)
                            navController.navigate("login")
                            onSignUpComplete(true, null) // Succès de l'inscription
                        } else {
                            // La mise à jour du pseudo a échoué
                            val errorMessage = profileUpdateTask.exception?.message
                            println("Echec de l'ajout du pseudo: $errorMessage")
                            onSignUpComplete(false, errorMessage)
                        }
                    }
            } else {
                // La création de l'utilisateur a échoué
                val errorMessage = task.exception?.message
                println("Echec de l'ajout de l'utilisateur: $errorMessage")
                onSignUpComplete(false, errorMessage)
            }
        }
}


fun addUserToDatabase(uid: String, username: String) {
    val profileImage = "R.drawable.ic_profile"
    val userMap = mapOf(
        "username" to username,
        "profile_   pic" to profileImage
    )

    // Ajouter l'utilisateur à la table "users" avec son UID comme clé
    val db = Firebase.firestore
    db.collection("users").document(uid)
        .set(userMap)
        .addOnSuccessListener {
            println("Utilisateur ajouté à la base de données avec succès")
        }
        .addOnFailureListener { e ->
            println("Erreur lors de l'ajout de l'utilisateur à la base de données: $e")
        }
}