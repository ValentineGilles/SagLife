package com.example.saglife.screen
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.sharp.Star
import androidx.compose.material.icons.twotone.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.saglife.R
import com.example.saglife.models.EventItem
import com.example.saglife.models.MapItem
import com.example.saglife.ui.theme.Purple40
import com.example.saglife.ui.theme.Purple80
import com.example.saglife.ui.theme.PurpleGrey40
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.util.Date


@Composable
fun MapInfoScreen(navController: NavHostController, id : String?) {

    var map by remember {
        mutableStateOf(
            MapItem(
                "id",
                "Nom de l'établissement",
                "Adresse",
                "Catégorie",
                "Description...",
                "event.jpg"
            )
        )
    }


    val db = Firebase.firestore
    if (id != null) {
        db.collection("map").document(id).get().addOnSuccessListener { document ->
            val name = document.getString("Name")!!
            val adresse = document.getString("Adresse")!!
            val filter = document.getString("Filter")!!
            val description = document.getString("Description")!!
            val photoPath = document.getString("Photo")!!
            map = MapItem(document.id, name, adresse, filter, description, photoPath)
        }
    }

    var storage = Firebase.storage
    var storageReference = storage.getReference("images/").child(map.photoPath)
    var urlImage : Uri? by remember { mutableStateOf(null) }
    storageReference.downloadUrl.addOnSuccessListener { url-> urlImage = url}



    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ) {
        if(urlImage==null) Image(
            painter = painterResource(id = R.drawable.event),
            contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth(),

            ) else
            AsyncImage(
                model = urlImage,contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth(),
            )
        Surface(modifier = Modifier.padding(16.dp).fillMaxWidth().height(72.dp), color = Color.Transparent) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.padding(8.dp)
            ) {
                Column() {
                    Text(text = map.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically,) {
                        Text(text = "4,7 \nsur 5", fontSize = 12.sp, textAlign = TextAlign.Center)
                        Spacer(
                            Modifier
                                .width(8.dp)
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()  //fill the max height
                                .width(1.dp)
                                .padding(top = 8.dp, bottom = 8.dp)

                        )
                        Spacer(
                            Modifier
                                .width(8.dp)
                        )
                        Text(text = "1,2km", fontSize = 12.sp, textAlign = TextAlign.Center)
                        Spacer(
                            Modifier
                                .width(8.dp)
                        )
                        Divider(
                            modifier = Modifier
                                .fillMaxHeight()  //fill the max height
                                .width(1.dp)
                                .padding(top = 8.dp, bottom = 8.dp)

                        )
                        Spacer(
                            Modifier
                                .width(8.dp)
                        )
                        Text(text = map.filter, fontSize = 12.sp, textAlign = TextAlign.Center)

                    }
                }
                FilledTonalButton(
                    onClick = { },
                    contentPadding = PaddingValues(16.dp, 8.dp),
                    modifier = Modifier
                        .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                    shape = RoundedCornerShape(24),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp
                    )
                ) {
                    Text("Itinéraire")
                }
            }
        }
        Divider(
            modifier = Modifier
                .fillMaxWidth()  //fill the max width
                .height(1.dp).padding(start = 16.dp, end = 16.dp)
        )
        Text(
            text = map.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Justify
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth()  //fill the max width
                .height(1.dp).padding(start = 16.dp, end = 16.dp)
        )
    }
}
@Preview
@Composable
fun PreviewMap() {
    val star = 3
    Surface (color = PurpleGrey40,shape = RoundedCornerShape(8)){ Column (modifier = Modifier.padding(8.dp)){ Row (modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.SpaceBetween){ Text(text = "Author", fontWeight = FontWeight.Bold)
        Text(text = "18 janvier 2022", color = Purple80)}
        Row { for (i in 1..star)Icon(imageVector = Icons.Default.Star, contentDescription = null)
            for (i in star+1..5)Icon(imageVector = Icons.TwoTone.Star, contentDescription = null) }
        Spacer(
            Modifier
                .height(8.dp)
        )
        Text("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam aliquam quis turpis eu volutpat. Proin nisi risus, accumsan sit amet mauris a, cursus mattis sem. Nunc ut mi lorem. Curabitur at arcu ullamcorper, elementum nibh at, facilisis est.",textAlign = TextAlign.Justify)
    } }
    /*
    Column (
        modifier = Modifier.fillMaxSize(),
    ){ Image(
        painter = painterResource(id = R.drawable.event),
        contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth()
    )
        Surface(modifier = Modifier.padding(16.dp).fillMaxWidth().height(72.dp)){
        Row (verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,modifier = Modifier.padding(8.dp)) {
            Column (){
                Text(text = "map.name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment= Alignment.CenterVertically, ){
                    Text(text = "4,7 \nsur 5", fontSize = 12.sp, textAlign= TextAlign.Center)
                    Spacer(
                        Modifier
                            .width(8.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()  //fill the max height
                            .width(1.dp)
                            .padding(top = 8.dp, bottom = 8.dp)

                    )
                    Spacer(
                        Modifier
                            .width(8.dp)
                    )
                    Text(text = "1,2km", fontSize = 12.sp, textAlign= TextAlign.Center)
                    Spacer(
                        Modifier
                            .width(8.dp)
                    )
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()  //fill the max height
                            .width(1.dp)
                            .padding(top = 8.dp, bottom = 8.dp)

                    )
                    Spacer(
                        Modifier
                            .width(8.dp)
                    )
                    Text(text = "map.categorie", fontSize = 12.sp, textAlign= TextAlign.Center)

                }
            }
            FilledTonalButton(onClick = {  }, contentPadding = PaddingValues(16.dp,8.dp),modifier = Modifier
                .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp), shape = RoundedCornerShape(24), elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp
            )
            ) {
                Text("Itinéraire")
            }
        }
        }
        Divider(modifier = Modifier
            .fillMaxWidth()  //fill the max width
            .height(1.dp).padding(start = 16.dp, end= 16.dp))
        Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla suscipit ex erat. Maecenas tempus nulla neque, vehicula posuere libero porta in. Donec fermentum enim vitae dui semper luctus. Nullam rhoncus ligula at tellus molestie, sed facilisis dui volutpat. Nunc fringilla massa id purus porttitor maximus. Aenean diam ante, consectetur euismod tincidunt nec, elementum in nisl. Sed bibendum tempus lorem, sed accumsan massa pellentesque at. Vivamus ullamcorper at mauris in aliquam. Vivamus fermentum libero quis purus tempor, non consequat dui rutrum. Nam blandit nibh nec odio ornare iaculis. Nulla id pharetra diam.\n" +
                "\n" +
                "Mauris placerat sed elit in ullamcorper. Vivamus viverra eu lorem nec blandit. Morbi id scelerisque augue. Vivamus a ante nibh. Nam cursus purus nec sapien ultrices, eu vulputate libero vestibulum. Phasellus eget velit malesuada, porttitor nunc ut, semper ante. Nunc hendrerit viverra lacus, eu venenatis diam maximus et. Nulla dignissim tristique nulla non feugiat.\n" +
                "\n" +
                "Morbi vitae semper risus. Donec mollis lacinia dictum. In justo elit, faucibus non libero ut, pharetra blandit ligula. Pellentesque non egestas arcu, vel ornare magna. Nam molestie nunc vel purus hendrerit, id volutpat urna congue. Phasellus faucibus tincidunt risus, at iaculis ex mollis quis. Cras non porta nisi. Aenean a felis arcu. Quisque pulvinar at arcu vel condimentum. Nulla nec fringilla est. Cras maximus, elit sed elementum semper, elit velit tincidunt nunc, eu congue augue quam quis lacus. Duis fermentum ligula et faucibus faucibus. Duis felis tortor, sagittis non imperdiet sit amet, fermentum nec purus. Vivamus elementum est odio, sit amet vulputate odio pharetra vitae.",fontSize = 16.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Justify)
        Divider(modifier = Modifier
            .fillMaxWidth()  //fill the max width
            .height(1.dp).padding(start = 16.dp, end= 16.dp))
    }*/

}
