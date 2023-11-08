package com.example.saglife.component.forum

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.saglife.R

data class ItemData(
    val icon: Int,
    val author: String,
    val date: String,
    val hour: String,
    val comment: String
)

@Composable
fun ForumPage(navController: NavHostController, id: String?) {
    val icon = R.drawable.ic_profile
    val title = "Titre 1"
    val author = "Auteur 1"
    val nb = 10
    val date = "01/11/2023"
    val hour = "12:30"
    val description =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Tempor commodo ullamcorper a lacus vestibulum sed. Leo vel orci porta non pulvinar. Aliquam ultrices sagittis orci a. Turpis egestas maecenas pharetra convallis. Euismod elementum nisi quis eleifend quam adipiscing. Aliquet sagittis id consectetur purus ut faucibus pulvinar elementum. Aliquam ut porttitor leo a diam sollicitudin tempor id eu. Donec ac odio tempor orci dapibus ultrices in iaculis nunc. Laoreet non curabitur gravida arcu ac. Pharetra sit amet aliquam id diam maecenas ultricies. Pellentesque eu tincidunt tortor aliquam. Ornare suspendisse sed nisi lacus. Quam viverra orci sagittis eu. Diam donec adipiscing tristique risus nec feugiat in fermentum. Venenatis a condimentum vitae sapien."
    var showFullDescription by remember { mutableStateOf(false) }

    val dataList = listOf(
        ItemData(R.drawable.ic_profile, "Auteur 1", "01/11/2023", "12:30", "Commentaire 1"),
        ItemData(R.drawable.ic_profile, "Auteur 2", "02/11/2023", "14:45", "Commentaire 2"),
        ItemData(R.drawable.ic_profile, "Auteur 3", "03/11/2023", "16:20", "Commentaire 3")
    )

    Column() {

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(8.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 30.dp,
                            end = 30.dp,
                            top = 30.dp
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(icon),
                        contentDescription = "Forum",
                        modifier = Modifier.size(20.dp)
                    )
                    Text(author)
                    Text(
                        text = "$date à $hour",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.End
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 30.dp,
                            end = 30.dp,
                            bottom = 30.dp
                        )
                )
                {
                    if (showFullDescription) {
                        Text(
                            text = description,
                            textAlign = TextAlign.Justify
                        )
                    } else {
                        Text(
                            text = description.substring(
                                0,
                                500
                            ), // Limite la description à 100 caractères
                            textAlign = TextAlign.Justify
                        )

                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 30.dp,
                            end = 30.dp,
                            bottom = 30.dp
                        )
                )
                {
                    if (description.length > 500) {
                        if (showFullDescription) {
                            Text(
                                text = "Voir moins",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable { showFullDescription = false }
                                    .fillMaxWidth(),
                                style = TextStyle(textDecoration = TextDecoration.Underline)
                            )
                        } else {
                            Text(
                                text = "Voir plus",
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .clickable { showFullDescription = true }
                                    .fillMaxWidth(),
                                style = TextStyle(textDecoration = TextDecoration.Underline)

                            )
                        }
                    }
                }

            }

        Column {
            dataList.forEach { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = null,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = item.author)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Date: ${item.date}, Hour: ${item.hour}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = item.comment)
                    }
                }
            }

    }}
}