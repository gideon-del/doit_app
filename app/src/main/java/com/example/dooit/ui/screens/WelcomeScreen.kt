package com.example.dooit.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.dooit.R
import com.example.dooit.ui.theme.DooitTheme

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier, changeStatus:() -> Unit) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF000000)
            ).padding(bottom =20.dp),
        verticalArrangement = Arrangement.SpaceAround,

    ) {
        Column(modifier=Modifier.fillMaxHeight(0.9f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Image(
                    painter = painterResource(id = R.drawable.welcome_img),
                    contentDescription = "dooit",
                    modifier = Modifier.size(67.dp)

                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.app_name),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displayLarge,
                color = Color(0xFFFFFFFF)
            )
            Spacer(modifier = Modifier.height(30.dp))
            Box(modifier = Modifier.fillMaxWidth(1 / 2f)) {
                Text(
                    text = stringResource(id = R.string.welcome_desc),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFFC4C4C4)

                )
            }
        }
        Box(
            modifier=Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FilledTonalButton(
                onClick = changeStatus,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF)),
                modifier = Modifier
                    .fillMaxWidth(1 / 2f)
                    .padding(horizontal = 20.dp)
                    .clip(
                        shape = RoundedCornerShape(10.dp)
                    )
            ) {
                Text(
                    text = "Continue",
                    color = Color(0xFF000000),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }


    }
}

@Preview
@Composable
fun WelcomeScreenPreview() {
    DooitTheme {
        WelcomeScreen(changeStatus = {})
    }
}