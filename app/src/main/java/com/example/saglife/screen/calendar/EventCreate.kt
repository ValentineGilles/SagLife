package com.example.saglife.screen.calendar

import Notification
import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.saglife.models.EventItem
import com.example.saglife.screen.forum.DisplaySquareImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date
import java.util.UUID

/**
 * Ecran pour créer un événement.
 *
 * @param navController Navigation controller.
 */

private val auth: FirebaseAuth = com.google.firebase.ktx.Firebase.auth

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MutableCollectionMutableState")
@Composable
fun EventCreate(navController: NavHostController) {
    // État des champs du formulaire
    var name by remember { mutableStateOf(TextFieldValue()) }
    var description by remember { mutableStateOf(TextFieldValue()) }
    var photoPath by remember { mutableStateOf(TextFieldValue()) }

    // Heures de début et de fin
    var time_start by remember { mutableStateOf("10h00") }
    var time_stop by remember { mutableStateOf("12h00") }

    // Etat de chargement des données
    var postLoaded by remember { mutableStateOf(false) }

    // Filtre sélectionné et liste des filtres disponibles
    var selectedFilter by remember { mutableStateOf("") }
    var filterList by remember { mutableStateOf(mutableListOf<String>()) }

    //Contexte
    val context = LocalContext.current

    // États pour les composants de sélection de date et d'heure
    val startTimePickerState =
        remember { TimePickerState(is24Hour = true, initialHour = 0, initialMinute = 0) }
    val stopTimePickerState =
        remember { TimePickerState(is24Hour = true, initialHour = 0, initialMinute = 0) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Date().time
    )

    var selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri.value = it
            }
        }


    val db = Firebase.firestore

    // Uri de l'image sélectionnée
    var uri by remember { mutableStateOf<Uri?>(null) }
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            uri = it
        }
    )
    // Chargement initial des filtres depuis Firebase
    LaunchedEffect(postLoaded) {
        if (!postLoaded) {

            db.collection("filter_event").get().addOnSuccessListener { result ->
                val filters = mutableListOf<String>()
                for (document in result) {
                    val name = document.getString("Name")!!
                    filters.add(name)
                }
                filterList = filters
                selectedFilter = filters[0]
            }
                .addOnFailureListener { e ->
                    println("Erreur lors de la récupération des données des filtres : $e")
                }
        }
    }

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Nouvel événement",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                )

// Champ de saisie du nom de l'événement
                OutlinedTextField(
                    shape = RoundedCornerShape(8.dp),
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nom de l'événement") },

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
                // Sélecteur de date
                DatePicker(state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background,
                    )
                )

                // Calcul de la date sélectionnée
                val selectedDate = datePickerState.selectedDateMillis?.let {
                    Date(it)
                }
                // Sélecteurs d'heure de début et de fin
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Début ",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp, top = 16.dp)
                    )
                    time_start =
                        TimePickerDialog(state = startTimePickerState, time = time_start)
                    Text(" - Fin ")
                    time_stop = TimePickerDialog(state = stopTimePickerState, time = time_stop)

                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 32.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    if (selectedImageUri.value == null) {
                        IconButton(
                            onClick = {
                                imageLauncher.launch("image/*")
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp)),
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

                Text(
                    text = "Ajouter une image de couverture",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp, top = 16.dp),
                    textAlign = TextAlign.Start
                )

                // Champ de saisie de la description
                OutlinedTextField(
                    shape = RoundedCornerShape(8.dp),
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
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
                // Liste déroulante des filtres
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filterList) { filter ->
                        androidx.compose.material3.FilterChip(
                            onClick = {
                                selectedFilter = filter
                            },
                            label = {
                                Text(filter)
                            },
                            selected = selectedFilter == filter,
                            leadingIcon = if (selectedFilter == filter) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = "Done icon",
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else {
                                null
                            },
                        )
                    }
                }


                // Bouton pour enregistrer l'événement
                Button(
                    onClick = {
                        if (name.text.isNotBlank() && description.text.isNotBlank() && selectedDate != null && selectedImageUri.value != null) {
                            val imageUri = selectedImageUri.value!!

                            // Construction des objets de date pour le début et la fin
                            val dateStart = Date(selectedDate.time)
                            dateStart.hours = startTimePickerState.hour
                            dateStart.minutes = startTimePickerState.minute
                            val dateStop = Date(selectedDate.time)
                            dateStop.hours = stopTimePickerState.hour
                            dateStop.minutes = stopTimePickerState.minute

                            // Enregistrer l'image dans Firebase Storage
                            val storageRef = Firebase.storage.reference
                            val imageRef = storageRef.child("images/events/${UUID.randomUUID()}")

                            val uploadTask = imageRef.putFile(imageUri)

                            uploadTask.addOnSuccessListener { taskSnapshot ->
                                // L'image a été téléchargée avec succès, obtenez l'URL de téléchargement
                                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                                    val imageUrl = downloadUri.toString()

                                    // Création de l'objet EventItem
                                    val event = auth.currentUser?.uid?.let {
                                        EventItem(
                                            author_id = it,
                                            id = UUID.randomUUID().toString(),
                                            name = name.text,
                                            dateStart = dateStart,
                                            dateEnd = dateStop,
                                            filter = selectedFilter,
                                            description = description.text,
                                            photoPath = imageUrl
                                        )
                                    }

                                    // Enregistrement de l'événement dans Firebase
                                    event?.toFirebase()

                                    //Envoie de la notification
                                    Notification("Saglife",name.text,"notif").send(context)

                                    // Retour à l'écran précédent
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    time: String,
    toggle: @Composable () -> Unit = {},
    state: TimePickerState,
): String {

    val openDialog = remember { mutableStateOf(false) }

    ElevatedButton(
        modifier = Modifier,
        shape = RoundedCornerShape(24),
        onClick = { openDialog.value = true },
    ) {
        Text(time)
    }
    if (openDialog.value)
        androidx.compose.ui.window.Dialog(
            onDismissRequest = {
                openDialog.value = false
                println("Confirmation registered")
            },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            ),
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .height(IntrinsicSize.Min)
                    .background(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surface
                    ),
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        text = title,
                        style = MaterialTheme.typography.labelMedium
                    )
                    TimePicker(state = state)
                    Row(
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                    ) {
                        toggle()
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = { openDialog.value = false }
                        ) { Text("Cancel") }
                        TextButton(
                            onClick = {
                                openDialog.value = false

                            }
                        ) { Text("OK") }
                    }
                }
            }
        }
    return state.hour.toString() + ":" + state.minute.toString()
}


@Composable
fun DisplayImage(uri: Uri, onDeleteClick: () -> Unit) {
    println("Uri de l'image : $uri")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(shape = RoundedCornerShape(8.dp))
            .background(Color.Gray)
    ) {
        Image(
            painter = rememberImagePainter(data = uri),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        IconButton(
            onClick = onDeleteClick, // Appel de la fonction de suppression
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Supprimer l'image",
                tint = Color.White
            )
        }
    }
}


