package com.example.endlessstory

import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.viewModelScope
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.Firebase
import com.google.api.client.http.HttpHeaders
import com.google.auth.oauth2.GoogleCredentials
import org.apache.http.HttpEntity
import org.assertj.core.api.Assertions

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.function.Consumer
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.cio.ChannelIOException
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.isEmpty
import io.ktor.utils.io.core.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonNull.content
import org.assertj.core.util.Strings.append
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {


    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.endlessstory", appContext.packageName)
    }


    @Test
    fun parse_json_response() {
        val pictures = InstrumentationRegistry.getInstrumentation().targetContext.assets.open("response.json")


    }


    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    val chat = generativeModel.startChat(
        history = listOf()
    )

    @Test
    fun chat_with_model() = runBlocking {
        val prompt = "Generate a story of a mountain"
        val response = chat.sendMessage(
            prompt
        )
        println(response.text)
    }


    @OptIn(ExperimentalEncodingApi::class)
    @Test
    fun should_generate_three_images_with_persistence() = runBlocking {

        val G_credentials =
            InstrumentationRegistry.getInstrumentation().targetContext.assets.open("credential.json")

        try {
            val stream: InputStream = G_credentials
            val credentials: GoogleCredentials = GoogleCredentials.fromStream(stream)
            val token: String = credentials.refreshAccessToken().getTokenValue()

            val paramString =
                "https://us-central1-aiplatform.googleapis.com/v1/projects/algebraic-spot-427705-e1/locations/us-central1/publishers/google/models/imagegeneration@002:predict"
            val jsonString =
                "{'instances': [ { 'prompt': 'Generate a photo of a mountain' } ],'parameters': { 'sampleCount': 1} }"


            val client = HttpClient()


            val file = File.createTempFile("files", "index")


            val requestString = HttpRequestBuilder(paramString).apply {
                headers {
                    append("Authorization", "Bearer $token")
                    append("Content-Type", "application/json; charset=utf-8")
                }
                setBody(jsonString)
            }


            val response: HttpResponse = client.request(paramString) {
                method = HttpMethod.Post
                headers {
                    append("Authorization", "Bearer $token")
                    append("Content-Type", "application/json; charset=utf-8")
                }
                setBody(jsonString)
            }

            val result: String = response.body()
            //process response
            val jsonObject = JSONObject(result)

            val items = jsonObject.getJSONArray("predictions")
            for (i in 0 until items.length()) {
                val string = items.getJSONObject(i)
                    .getString("bytesBase64Encoded")
                val imageBytes = Base64.decode(string, 0)

                val bitmap =
                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()

                println(string)


            }

        } catch (e: Exception) {
            println("EXCEPTION in edit$e")

        }
    }

    @Test
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
            "{'instances': [ { 'prompt': 'Generate a photo of a mountain' } ],'parameters': { 'sampleCount': 3} }"


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




}   }