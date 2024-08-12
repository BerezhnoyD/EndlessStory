package com.example.endlessstory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp



@Preview
@Composable
fun InstructionsScreen(onNextButtonClicked: () -> Unit = {}
)



{
    Box(
        Modifier.fillMaxSize(),


        ) {
        Column (modifier = Modifier.align(Alignment.Center).padding(top = 10.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

            val scrollState = rememberScrollState()

            FilledTonalButton(
                onClick = {

                },
                shape = CircleShape


            ) {
                Text(text = stringResource(R.string.action1_instructions))
            }



            Text(
                text = stringResource(R.string.text_instructions),
                textAlign = TextAlign.Justify,
                color = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            )


            FilledTonalButton(
                onClick = onNextButtonClicked,
                shape = CircleShape


            ) {
                Text(text = stringResource(R.string.action2_instructions))
            }

        }


    }

}