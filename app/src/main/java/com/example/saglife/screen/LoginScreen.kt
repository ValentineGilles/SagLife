package com.example.saglife.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.graphics.Color


@Composable
fun LoginScreen(onLoginClick: () -> Unit) {
    var username by remember { mutableStateOf(TextFieldValue()) }
    var password by remember { mutableStateOf(TextFieldValue()) }
    var isUserLoggedIn by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Login", modifier = Modifier.padding(bottom = 16.dp))
        BasicTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        BasicTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                // Vérifiez les identifiants (par exemple, username et password)
                // Si la connexion réussit, passez isUserLoggedIn à true
                if (isValidLogin(username.text, password.text)) {
                    isUserLoggedIn = true
                    onLoginClick()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Login", color = Color.White)
        }
    }
}


fun isValidLogin(username: String, password: String): Boolean {
    //return username == "test" && password =="test"
    return true
}
