package com.example.endlessstory

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.test.platform.app.InstrumentationRegistry
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.auth.oauth2.GoogleCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.preparePost
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.function.Consumer
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


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

/*
    @OptIn(ExperimentalEncodingApi::class)
    fun create_asymmetric_encryption() = runBlocking {
        val G_credentials =
            InstrumentationRegistry.getInstrumentation().targetContext.assets.open("credential.json")

        val stream: InputStream = G_credentials
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
        val token: String = credentials.refreshAccessToken().getTokenValue()

        val paramString =
            "https://us-central1-aiplatform.googleapis.com/v1/projects/algebraic-spot-427705-e1/locations/us-central1/publishers/google/models/imagegeneration@002:predict"
        val jsonString =
            "{'instances': [ { 'prompt': 'Generate a photo of a mountain' } ],'parameters': { 'sampleCount': 2} }"


        val client = HttpClient()


        val file = File.createTempFile("files", "index")


        val requestString = HttpRequestBuilder().apply {
            url(paramString)
            method = HttpMethod.Post
            headers {
                append("Authorization", "Bearer $token")
                append("Content-Type", "application/json; charset=utf-8")
            }
            setBody(jsonString)
        }


        client.preparePost(builder = requestString).execute { httpResponse ->
            val channel: ByteReadChannel = httpResponse.body()
            while (!channel.isClosedForRead) {
                val packet = channel.readRemaining(DEFAULT_BUFFER_SIZE.toLong())
                while (!packet.isEmpty) {
                    val bytes = packet.readBytes()
                    file.appendBytes(bytes)
                    println("Received ${file.length()} bytes from ${httpResponse.contentLength()}")
                }

            }
        }

        val jsonObject = JSONObject(file.bufferedReader().readText())

        val items = jsonObject.getJSONArray("predictions")
        for (i in 0 until items.length()) {
            val string = items.getJSONObject(i)
                .getString("bytesBase64Encoded")
            val imageBytes = Base64.decode(string, 0)

            val bitmap =
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()


            println(string)

        }

    } */
}


