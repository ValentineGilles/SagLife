package com.example.saglife


import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.models.ForumPostItem

@SuppressLint("DiscouragedApi")
@Composable
fun ForumCard(data: ForumPostItem, navController: NavHostController) {
    val icon = data.icon
    val title = data.title
    val author = data.author
    val nb = data.nb
    val date = data.getDay()
    val hour = data.getTime()
    val id = data.id
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        modifier = Modifier.fillMaxWidth().shadow(4.dp, shape = RoundedCornerShape(8.dp)).clickable {
            navController.navigate("forum/$id")
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),

            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(context.resources.getIdentifier(icon, "drawable", context.packageName)),
                contentDescription = "Forum",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Box(
                modifier = Modifier
                    .height(50.dp)
                    .width(2.dp)
                    .background(Color(0xFFCCCCCC))
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall)
                Text(
                    text = author,
                    style = TextStyle(
                        fontSize = 12.sp,
                        letterSpacing = 0.sp
                    ),
                    color = Color.Gray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "$nb réponse(s)",
                        style = TextStyle(
                            fontSize = 10.sp,
                            letterSpacing = 0.sp
                        ),
                    )
                    Text(
                        text = "$date à $hour",
                        style = TextStyle(
                            fontSize = 10.sp,
                            letterSpacing = 0.sp
                        ),
                    )
                }
            }
        }
    }
}
