package com.example.endlessstory

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import dev.langchain4j.data.image.Image
import dev.langchain4j.model.vertexai.VertexAiImageModel
import dev.langchain4j.model.output.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.function.Consumer
import org.assertj.core.api.Assertions.assertThat






class StoryViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> =
        MutableStateFlow(UiState.Initial)
    val uiState: StateFlow<UiState> =
        _uiState.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    val chat = generativeModel.startChat(
        history = listOf()
    )

    fun sendPrompt(
        bitmap: Bitmap,
        prompt: String
    ) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = chat.sendMessage(
                    content {
                        image(bitmap)
                        text(prompt)
                    }
                )

                // HERE WE SHOULD WORK WITH OUTPUT CONTENT PARSING AND CHANGING IT BY KEYWORDS
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }

    fun should_generate_three_images_with_persistence() {
        val ImagenModel: VertexAiImageModel = VertexAiImageModel.builder()
            .endpoint(BuildConfig.ENDPOINT)
            .location(BuildConfig.LOCATION)
            .project(BuildConfig.PROJECT)
            .publisher(BuildConfig.PUBLISHER)
            .modelName("imagegeneration@002")
            .withPersisting()
            .build()

        val imageListResponse: Response<List<Image>> =
            ImagenModel.generate("photo of a sunset in the forest", 3)

        assertThat(imageListResponse.content()).hasSize(3)
        imageListResponse.content().forEach(Consumer<Image> { img: Image ->
            assertThat(img.url()).isNotNull()
            assertThat(img.base64Data()).isNotNull()

        })


        _uiState.value = UiState.Images(imageListResponse.content())

    }

    fun firstPrompt(
        prompt: String
    ) {

        viewModelScope.launch(Dispatchers.IO) {
            try {


                val response = chat.sendMessage(
                    content {
                        text(prompt)
                    }
                )

                // HERE WE SHOULD WORK WITH OUTPUT CONTENT PARSING AND CHANGING IT BY KEYWORDS
                response.text?.let { outputContent ->
                    _uiState.value = UiState.Success(outputContent)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.localizedMessage ?: "")
            }
        }
    }
}


