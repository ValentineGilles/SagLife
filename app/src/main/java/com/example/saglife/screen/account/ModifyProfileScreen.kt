package com.example.saglife.screen.account

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.screen.calendar.DisplayImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.UUID

private val auth: FirebaseAuth = Firebase.auth
@Composable
fun ModifyProfileScreen(navController: NavHostController) {
    // États pour stocker les informations de l'utilisateur
    var profilePic by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val db = Firebase.firestore

    db.collection("users").document(auth.currentUser?.uid.toString()).get().addOnSuccessListener { document ->
        if (document != null) {
            profilePic = document.data?.get("profile_pic").toString()
            username = document.data?.get("username").toString()
            description = document.data?.get("description").toString()
        }
    }.addOnFailureListener { exception ->
        println("get failed with $exception")
    }

    val imageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri.value = it
            }
        }

    // Uri de l'image sélectionnée
    var uri by remember { mutableStateOf<Uri?>(null) }
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )
    Card(
        modifier = Modifier.padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Modifier Profil", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(16.dp))

                // Champ pour la photo de profil
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                ) {
                    if (selectedImageUri.value == null) {
                        IconButton(
                            onClick = {
                                imageLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    MaterialTheme.colorScheme.secondary,
                                    RoundedCornerShape(8.dp)
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Ajouter une image",
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    } else {
                        selectedImageUri.value?.let { uri ->
                            DisplayImage(uri) {
                                // Callback pour supprimer l'image
                                selectedImageUri.value = null
                            }
                        }

                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Champ pour le nom d'utilisateur
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Nom d'Utilisateur") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 300.dp) // Ajuster la largeur maximale
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Champ pour la description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 300.dp) // Ajuster la largeur maximale
                        .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Bouton pour soumettre les modifications
                Button(
                    onClick = {
                            auth.currentUser?.let {
                                updateUserProfile(
                                    it.uid,
                                    selectedImageUri,
                                    username,
                                    description
                                )
                            }
                        navController.navigateUp()

                    }
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}


fun updateUserProfile(userId: String, profilePic: MutableState<Uri?>, username: String, description: String) {
    val userRef = Firebase.firestore.collection("users").document(userId)

    var oldImageUrl: String? = null
    userRef.get().addOnSuccessListener { document ->
        oldImageUrl = document.getString("profile_pic")

    val imageUri = profilePic.value!!

    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("profile_pic/${UUID.randomUUID()}")

    val uploadTask = imageRef.putFile(imageUri)

    uploadTask.addOnSuccessListener { taskSnapshot ->
        // L'image a été téléchargée avec succès, obtenez l'URL de téléchargement
        imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
            val imageUrl = downloadUri.toString()
            userRef.update(
                mapOf(
                    "profile_pic" to imageUrl,
                    "username" to username,
                    "description" to description
                )
            ).addOnSuccessListener {
                println("oldImageUrl : $oldImageUrl")
                oldImageUrl?.let {
                    val oldImageRef = Firebase.storage.getReferenceFromUrl(it)
                    oldImageRef.delete().addOnSuccessListener {
                        println("Old image successfully deleted")
                    }.addOnFailureListener { e ->
                        println("Error deleting old image: $e")
                    }
                }
                println("DocumentSnapshot successfully updated!")
            }.addOnFailureListener { e ->
                println("Error updating document: $e")
            }
        }
    }
    }
}
