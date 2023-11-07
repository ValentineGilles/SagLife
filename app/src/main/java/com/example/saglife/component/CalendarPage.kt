package com.example.saglife.component
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.saglife.R
import com.example.saglife.models.EventItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Date


@Composable
fun EventScreen(navController: NavHostController, id : String?) {

    var event by remember { mutableStateOf(EventItem("id","Evénement", Date(), Date(),"Description...","event.jpg")) }


    val db = Firebase.firestore
    if(id!=null) {
        db.collection("event").document(id).get().addOnSuccessListener { document ->
            println(document.data.toString())
            val name = document.get("Name").toString()
            val dateStart : Date = document.getDate("Date_start")!!
            val dateEnd = document.getDate("Date_stop")!!
            val description = document.get("Description").toString()
            val photoPath = document.get("Photo").toString()
            event = EventItem(document.id,name, dateStart, dateEnd,description, photoPath)
        }
    }



    Column (
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
    ){ Image(
        painter = painterResource(id = R.drawable.event),
        contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth()
    )
        ElevatedCard (modifier = Modifier.fillMaxWidth().height(80.dp).padding(8.dp)) {
            Row(verticalAlignment= Alignment.CenterVertically){
                Box(modifier = Modifier.width(80.dp)){
                    Text(text = " 17 Nov.", fontSize = 24.sp, modifier = Modifier.align(
                        Alignment.Center), textAlign= TextAlign.Center)}
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
                Column { Text(text = event.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "17h - 22h")}
            }

        }
        Text(text = event.description,fontSize = 16.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Justify)

}
}

@Preview
@Composable
fun Preview() {
    Column (
        modifier = Modifier.fillMaxSize(),
    ){ Image(
        painter = painterResource(id = R.drawable.event),
        contentDescription = "null", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth()
    )
        ElevatedCard (modifier = Modifier.fillMaxWidth().height(80.dp).padding(8.dp)) {
            Row(verticalAlignment= Alignment.CenterVertically){
                Box(modifier = Modifier.width(80.dp)){
                    Text(text = " 17 Nov.", fontSize = 24.sp, modifier = Modifier.align(
                        Alignment.Center), textAlign= TextAlign.Center)}
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
                Column { Text(text = "Barbie", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text(text = "17h - 22h")}
            }

        }
        Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla suscipit ex erat. Maecenas tempus nulla neque, vehicula posuere libero porta in. Donec fermentum enim vitae dui semper luctus. Nullam rhoncus ligula at tellus molestie, sed facilisis dui volutpat. Nunc fringilla massa id purus porttitor maximus. Aenean diam ante, consectetur euismod tincidunt nec, elementum in nisl. Sed bibendum tempus lorem, sed accumsan massa pellentesque at. Vivamus ullamcorper at mauris in aliquam. Vivamus fermentum libero quis purus tempor, non consequat dui rutrum. Nam blandit nibh nec odio ornare iaculis. Nulla id pharetra diam.\n" +
                "\n" +
                "Mauris placerat sed elit in ullamcorper. Vivamus viverra eu lorem nec blandit. Morbi id scelerisque augue. Vivamus a ante nibh. Nam cursus purus nec sapien ultrices, eu vulputate libero vestibulum. Phasellus eget velit malesuada, porttitor nunc ut, semper ante. Nunc hendrerit viverra lacus, eu venenatis diam maximus et. Nulla dignissim tristique nulla non feugiat.\n" +
                "\n" +
                "Morbi vitae semper risus. Donec mollis lacinia dictum. In justo elit, faucibus non libero ut, pharetra blandit ligula. Pellentesque non egestas arcu, vel ornare magna. Nam molestie nunc vel purus hendrerit, id volutpat urna congue. Phasellus faucibus tincidunt risus, at iaculis ex mollis quis. Cras non porta nisi. Aenean a felis arcu. Quisque pulvinar at arcu vel condimentum. Nulla nec fringilla est. Cras maximus, elit sed elementum semper, elit velit tincidunt nunc, eu congue augue quam quis lacus. Duis fermentum ligula et faucibus faucibus. Duis felis tortor, sagittis non imperdiet sit amet, fermentum nec purus. Vivamus elementum est odio, sit amet vulputate odio pharetra vitae.",fontSize = 16.sp, modifier = Modifier.padding(16.dp), textAlign = TextAlign.Justify)

    }
}
