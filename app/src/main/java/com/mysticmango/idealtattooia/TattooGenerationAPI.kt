package com.mysticmango.idealtattooia

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Url

interface TattooGenerationAPI {
    @GET("generate")
    suspend fun generateDesign(
        @Query("prompt") prompt: String,
        @Query("style") style: String
    ): retrofit2.Response<ResponseBody>

    companion object {
        fun create(): TattooGenerationAPI {
            return Retrofit.Builder()
                .baseUrl("https://image.pollinations.ai/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
                .create(TattooGenerationAPI::class.java)
        }
    }
}

data class GenerationRequest(
    val prompt: String,
    val negativePrompt: String,
    val guidanceScale: Int,
    val steps: Int
)

data class GenerationResponse(
    val outputs: List<Output>
)

data class Output(
    val url: String
) 