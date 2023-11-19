package com.example.saglife.screen.calendar

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.saglife.models.EventItem
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.firestore
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import java.time.Instant
import java.time.ZoneOffset
import java.util.Calendar
import java.util.Date

/**
 * Ecran pour créer un événement.
 *
 * @param navController Navigation controller.
 */
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

    // États pour les composants de sélection de date et d'heure
    val startTimePickerState =
        remember { TimePickerState(is24Hour = true, initialHour = 0, initialMinute = 0) }
    val stopTimePickerState =
        remember { TimePickerState(is24Hour = true, initialHour = 0, initialMinute = 0) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Date().time
    )


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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Ajouter un nouvel événement",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp, top = 32.dp)
        )

// Champ de saisie du nom de l'événement
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = name,
            onValueChange = { name = it },
            label = { Text("Nom de l'événement") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 300.dp) // Ajuster la largeur maximale
                .padding(bottom = 16.dp)
        )
        // Sélecteur de date
        DatePicker(state = datePickerState)
        // Calcul de la date sélectionnée
        val selectedDate = datePickerState.selectedDateMillis?.let {
            Date(it)
        }
        // Sélecteurs d'heure de début et de fin
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Horaires")
            Row {
                time_start = TimePickerDialog(state = startTimePickerState, time = time_start)
                Text("-")
                time_stop = TimePickerDialog(state = stopTimePickerState, time = time_stop)
            }
        }
        // Bouton pour choisir une image
        Button(onClick = {
            singlePhotoPicker.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )

        }) {
            Text("Pick Single Image")
        }
        // Champ de saisie de la description
        TextField(
            shape = RoundedCornerShape(8.dp),
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            colors = TextFieldDefaults.textFieldColors(
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(200.dp)
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
        ElevatedButton(
            onClick = {
                if (name.text != "" && description.text != "" && selectedDate != null) {
                    // Construction des objets de date pour le début et la fin
                    val dateStart = Date(selectedDate.time)
                    dateStart.hours = startTimePickerState.hour
                    dateStart.minutes = startTimePickerState.minute
                    val dateStop = Date(selectedDate.time)
                    dateStop.hours = stopTimePickerState.hour
                    dateStop.minutes = stopTimePickerState.minute

                    // Création de l'objet EventItem
                    val event = EventItem(
                        id = "",
                        name = name.text,
                        dateStart = dateStart,
                        dateEnd = dateStop,
                        filter = selectedFilter,
                        description = description.text,
                        photoPath = "event.jpg"
                    )
                    // Enregistrement de l'événement dans Firebase
                    event.toFirebase()

                    // Retour à l'écran précédent
                    navController.popBackStack()
                }


            },
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text("Enregistrer")
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





