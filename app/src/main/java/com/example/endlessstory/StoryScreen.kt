package com.example.endlessstory

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel

val images = mutableListOf(
    // Image generated using Gemini from the prompt "cupcake image"
    R.drawable.image_1,
    R.drawable.image_2,
    R.drawable.image_3

)
val imageDescriptions = arrayOf(
    R.string.image1_description,
    R.string.image2_description,
    R.string.image3_description
)



@Preview
@Composable
fun StoryScreen(
    onNextButtonClicked: () -> Unit = {},
    storyViewModel: StoryViewModel = viewModel(),
    itemWidthDp: Dp = 1024.dp, // Default width for each item
    paddingDp: Dp = 8.dp, // Default distance between items


) {
    val selectedImage = remember { mutableIntStateOf(0) }
    val lazyListState = LazyListState( firstVisibleItemIndex = 1, firstVisibleItemScrollOffset = 0)
    val itemWidthPx = with(LocalDensity.current) { (itemWidthDp + paddingDp).toPx() }
    val coroutineScope = rememberCoroutineScope()
    val initialPrompt = stringResource(R.string.prompt_initial)
    val placeholderPrompt = stringResource(R.string.prompt_placeholder)
    val placeholderResult = stringResource(R.string.results_placeholder)
    var prompt by rememberSaveable { mutableStateOf(placeholderPrompt) }
    var result by rememberSaveable { mutableStateOf(placeholderResult) }
    val uiState by storyViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showPopup by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        storyViewModel.firstPrompt(initialPrompt)
    }



    Box(
        Modifier.fillMaxSize(),


    ) {

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            state = lazyListState,
            horizontalArrangement = Arrangement.spacedBy(paddingDp, Alignment.CenterHorizontally),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp
            )
        ) {
            itemsIndexed(images) { index, image ->
                var imageModifier = Modifier
                    .requiredSize(itemWidthDp)
                    .clickable {
                        selectedImage.intValue = index
                    }
                if (index == selectedImage.intValue) {
                    imageModifier =
                        imageModifier.border(BorderStroke(12.dp, MaterialTheme.colorScheme.primary))
                }
                Image(
                    bitmap = painterResource(image),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {},
                    contentDescription = stringResource(imageDescriptions[index])
                )
            }
        }

        Column (modifier = Modifier
            .align(Alignment.Center)
            .padding(top = 400.dp, bottom = 100.dp)) {

            if (uiState is UiState.Loading) {
                CircularProgressIndicator()
            } else {
                var textColor = MaterialTheme.colorScheme.onSurface
                if (uiState is UiState.Error) {
                    textColor = MaterialTheme.colorScheme.error
                    result = (uiState as UiState.Error).errorMessage
                } else if (uiState is UiState.Success) {
                    textColor = MaterialTheme.colorScheme.onSurface
                    result = (uiState as UiState.Success).outputText
                    val new = (uiState as UiState.Images).new_images

                    for (i in images.indices) {
                        images[i] = new[i].base64Data().toInt()

                    }

                }
                val scrollState = rememberScrollState()
                Text(
                    text = result,
                    textAlign = TextAlign.Justify,
                    color = textColor,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(all = 16.dp)
                .align(Alignment.BottomStart)
        ) {
            Button(
                onClick = {
                    showPopup = true
                },
                enabled = prompt.isNotEmpty(),
                shape = CircleShape,
                modifier = Modifier.size(80.dp)

            ) {
                Text(text = stringResource(R.string.action_stop))
            }

            TextField(
                value = prompt,
                label = { Text(stringResource(R.string.label_prompt)) },
                onValueChange = { prompt = it },
                modifier = Modifier
                    .weight(0.8f)
                    .padding(start = 8.dp, end = 8.dp)
                    .padding(vertical = 8.dp)

            )

            Button(
                onClick = {
                    val bitmap = BitmapFactory.decodeResource(
                        context.resources,
                        images[selectedImage.intValue]
                    )
                    storyViewModel.sendPrompt(bitmap, prompt)
                },
                enabled = prompt.isNotEmpty(),
                shape = CircleShape,
                modifier = Modifier.size(80.dp)

            ) {
                Text(text = stringResource(R.string.action_go))
            }


        }

        if (showPopup) {
            Popup(
                alignment = Alignment.BottomCenter,
                properties = PopupProperties(
                    excludeFromSystemGesture = true,
                ),

                // to dismiss on click outside
                onDismissRequest = { showPopup = false }
            ) {
                Box(
                    modifier = Modifier
                        .width(400.dp)
                        .height(100.dp)
                        .background(Color.White)
                        .clip(shape = RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(all = 8.dp)
                            .width(400.dp)
                            .height(100.dp)
                            .background(Color.White)
                            .clip(shape = RoundedCornerShape(15.dp))

                    ) {
                        Button(
                            onClick = onNextButtonClicked,
                            enabled = prompt.isNotEmpty(),
                            shape = CircleShape,
                            modifier = Modifier.size(70.dp)
                        ) {
                            Text(text = stringResource(R.string.action_go))
                        }

                        Box(modifier = Modifier.width(240.dp)) {
                            Text(
                                text = "Are you sure you want to end the story? Or just take a step back?",
                                Modifier.padding(start = 8.dp, end = 8.dp)
                            )
                        }
                        Button(onClick = {
                            // There should be a function here to continue the story
                            showPopup = false
                        },
                            enabled = prompt.isNotEmpty(),
                            shape = CircleShape,
                            modifier = Modifier.size(70.dp)){
                            Text(text = stringResource(R.string.action_stop))
                        }
                    }
                }


        }



    }



}}

