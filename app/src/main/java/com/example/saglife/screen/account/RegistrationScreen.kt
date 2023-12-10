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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


// Initialisation de Firebase Authentication
private val auth: FirebaseAuth = Firebase.auth

// Composable pour l'écran d'inscription
@Composable
fun RegistrationScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithTopBar(navController)
    }
}

// Composable pour l'écran d'inscription avec la barre de navigation en haut
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
            var showPassword by remember { mutableStateOf(false) }

            var ConnectionError by remember { mutableStateOf<String?>(null) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
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

                OutlinedTextField(
                    label = { Text(text = "Nom d'utilisateur") },
                    value = username,
                    shape = RoundedCornerShape(16.dp),
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    label = { Text(text = "Adresse E-mail") },
                    value = email,
                    shape = RoundedCornerShape(16.dp),
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    label = { Text(text = "Mot de passe") },
                    value = password,
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        if (showPassword) {
                            IconButton(onClick = { showPassword = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = 60.dp, end = 60.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    shape = RoundedCornerShape(16.dp),
                    label = { Text(text = "Confirmer votre mot de passe") },
                    value = password2,
                    visualTransformation = if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        if (showPassword) {
                            IconButton(onClick = { showPassword = false }) {
                                Icon(
                                    imageVector = Icons.Filled.Visibility,
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        } else {
                            IconButton(
                                onClick = { showPassword = true }) {
                                Icon(
                                    imageVector = Icons.Filled.VisibilityOff,
                                    contentDescription = "hide_password",
                                    tint = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    },
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
                                            // Affichez le message d'erreur sur ConnectionError
                                            ConnectionError =
                                                errorMessage ?: "Une erreur inconnue s'est produite"
                                        }
                                    }
                                } else {
                                    // Affichez un message d'erreur si les mots de passe ne correspondent pas
                                    ConnectionError = "Les deux mots de passe ne correspondent pas."
                                }
                            } else {
                                ConnectionError = "Veuillez remplir tous les champs."
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Text(text = "S'inscrire", color = Color.White)
                    }
                }
            }
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
    val trim_email = email.trim()
    auth.createUserWithEmailAndPassword(trim_email, password)
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

// Fonction pour ajouter l'utilisateur à la base de données
fun addUserToDatabase(uid: String, username: String) {
    val profileImage = "R.drawable.ic_profile"
    val userMap = mapOf(
        "username" to username,
        "profile_pic" to profileImage
    )

// Ajouter l'utilisateur à la table "users
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
