package com.example.endlessstory

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.auth.oauth2.GoogleCredentials
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.headers
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.contentLength
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.InputStream

class ImageViewModel() : ViewModel() {
    private val _imState: MutableStateFlow<ImState> =
        MutableStateFlow(ImState.Initial)
    val imState: StateFlow<ImState> =
        _imState.asStateFlow()


    fun create_asymmetric_encryption(
        request: String,
        context: Context
    ) = runBlocking {



        val G_credentials = context.assets.open("credential.json")
        val stream: InputStream = G_credentials
        val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
        val token: String = credentials.refreshAccessToken().getTokenValue()

        val paramString =
            "https://us-central1-aiplatform.googleapis.com/v1/projects/algebraic-spot-427705-e1/locations/us-central1/publishers/google/models/imagegeneration@002:predict"
        val jsonString =
            "{'instances': [ { 'prompt': " + request + " } ],'parameters': { 'sampleCount': 3} }"
        val client = HttpClient()
        val file = File.createTempFile("temp_response", "index")

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

                ////// HERE WE SHOULD WORK WITH OUTPUT CONTENT PARSING AND CHANGING IT BY KEYWORDS ////

            }
        }

    }


}