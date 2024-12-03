package com.example.jetpackcomposeauthui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.jetpackcomposeauthui.ui.theme.AlegreyaSansFontFamily

@Composable
fun ForgotPasswordRow(
    onForgotPasswordTap: () -> Unit = {},
) {
    Row(
        modifier = Modifier.padding(top=19.dp, bottom = 45.dp)
    ){
        Text("Forgot Password? ",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = AlegreyaSansFontFamily,
                color = Color.White
            )
        )

        Text("Reset Password",
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = AlegreyaSansFontFamily,
                fontWeight = FontWeight(700),
                color = Color.White
            ),
            modifier = Modifier.clickable {
                onForgotPasswordTap()
            }
        )


    }
}