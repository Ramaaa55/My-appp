package com.mysticmango.idealtattooia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.core.content.ContextCompat
import android.widget.Button
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.mysticmango.idealtattooia.databinding.ActivityMainBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import java.net.URLEncoder
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.target.Target
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.app.AlertDialog
import com.google.android.material.button.MaterialButton
import com.github.ybq.android.spinkit.SpinKitView
import com.github.ybq.android.spinkit.style.Circle
import android.view.LayoutInflater
import retrofit2.Retrofit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random
import com.mysticmango.idealtattooia.TattooGenerationAPI
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import okhttp3.ResponseBody
import android.graphics.BitmapFactory
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.scalars.ScalarsConverterFactory
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import android.widget.Spinner
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import kotlinx.coroutines.Job
import android.widget.ProgressBar
import com.bumptech.glide.request.RequestOptions
import android.util.Log
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.util.UUID
import java.net.MalformedURLException
import com.bumptech.glide.load.Key
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.animation.AnimationUtils
import android.view.Gravity
import kotlinx.coroutines.delay
import com.mysticmango.idealtattooia.TattooStyle
import com.mysticmango.idealtattooia.TattooStyleProvider
import com.bumptech.glide.Priority
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.LinearLayout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import android.annotation.SuppressLint
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import android.provider.MediaStore
import android.graphics.drawable.BitmapDrawable
import android.graphics.PorterDuff

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var styleRecyclerView: RecyclerView
    private lateinit var promptEditText: TextInputEditText
    private lateinit var btnSuggest: MaterialButton
    private lateinit var btnGenerate: MaterialButton
    private lateinit var generatedTattoo: ImageView
    private var currentImageJob: Job? = null
    private var selectedStyle: TattooStyle? = null
    private val tattooAdapter = TattooAdapter(
        styles = TattooStyleProvider.defaultStyles(),
        onStyleSelected = { selectedStyle: TattooStyle ->
            this.selectedStyle = selectedStyle
            Toast.makeText(this, "Estilo: ${selectedStyle.name}", Toast.LENGTH_SHORT).show()
        }
    )

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://image.pollinations.ai/")
        .client(okHttpClient)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    private val apiService: TattooGenerationAPI by lazy {
        retrofit.create(TattooGenerationAPI::class.java)
    }

    private val surprisePrompts = arrayOf(
        "Geometric wolf with tribal patterns",
        "Watercolor phoenix rising from flames",
        "Minimalist mountain range with moon phases",
        "Cyberpunk robot arm with neon circuits",
        "Japanese koi fish with cherry blossoms"
    )

    private var retryCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create overlay for loading
        val rootView = findViewById<View>(android.R.id.content)
        val loadingOverlay = LayoutInflater.from(this).inflate(R.layout.overlay_loading, null)
        val overlayParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        loadingOverlay.layoutParams = overlayParams
        loadingOverlay.visibility = View.GONE
        (rootView as FrameLayout).addView(loadingOverlay)

        // Initialize views
        styleRecyclerView = binding.styleRecycler
        promptEditText = binding.promptInput
        btnSuggest = binding.surpriseButton
        btnGenerate = binding.generateButton

        // Hide any progress indicators initially
        hideLoading()

        // Setup RecyclerView
        binding.styleRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tattooAdapter
        }

        // Setup buttons
        binding.surpriseButton.setOnClickListener {
            val randomPrompt = surprisePrompts.random()
            promptEditText.setText(randomPrompt)
            Toast.makeText(this, "¡Prompt sugerido!", Toast.LENGTH_SHORT).show()
        }

        binding.generateButton.setOnClickListener {
            val prompt = promptEditText.text.toString().trim()
            val selectedStyle = tattooAdapter.getSelectedStyle()

            if (prompt.isNotEmpty() && selectedStyle != null) {
                showLoading()
                generateTattooDesign(prompt, selectedStyle.name)
            } else {
                if (prompt.isEmpty()) {
                    Toast.makeText(this, "Ingresa un prompt primero", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Selecciona un estilo", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLoading() {
        val loadingOverlay = findViewById<View>(R.id.loading_overlay)
        loadingOverlay?.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        val loadingOverlay = findViewById<View>(R.id.loading_overlay)
        loadingOverlay?.visibility = View.GONE
    }

    private fun generateTattooDesign(prompt: String, style: String) {
        showLoading()
        currentImageJob?.cancel()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Modify the prompt to ensure black and white professional tattoo design
        val enhancedPrompt = "$prompt in $style style, professional black and white tattoo design, high contrast, clean lines, detailed tattoo art, white background, 8k resolution"
        val encodedPrompt = URLEncoder.encode(enhancedPrompt, "UTF-8")
        val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=512&height=512&nologo=true&seed=${System.currentTimeMillis()}"

        Log.d("ImageDebug", "URL: $imageUrl")

        val request = Request.Builder()
            .url(imageUrl)
            .header("Cache-Control", "no-cache")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    hideLoading()
                    showError("Tiempo de espera agotado. Verifica tu conexión y vuelve a intentar")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            hideLoading()
                            showError("API Error: ${response.code}")
                        }
                        return
                    }

                    val inputStream = response.body?.byteStream()
                    val bytes = inputStream?.readBytes()

                    runOnUiThread {
                        if (bytes != null && bytes.isNotEmpty()) {
                            showResultDialog(bytes)
                        } else {
                            hideLoading()
                            showError("Empty response")
                        }
                    }
                }
            }
        })
    }

    private fun showResultDialog(imageBytes: ByteArray) {
        hideLoading()

        // Create and show result dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_result, null)
        val resultImage = dialogView.findViewById<ImageView>(R.id.result_image)
        val closeButton = dialogView.findViewById<Button>(R.id.close_button)
        val saveButton = dialogView.findViewById<Button>(R.id.save_button)

        // Apply black and white filter
        val blackAndWhiteMatrix = ColorMatrix().apply {
            setSaturation(0f) // Remove all color (make it grayscale)
            // Increase contrast slightly
            val contrast = 1.2f
            val scale = contrast
            val translate = (-.5f * scale + .5f) * 255f
            set(floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
        }

        // Load image into the dialog with black and white filter
        Glide.with(this)
            .load(imageBytes)
            .apply(RequestOptions()
                .override(1080, 1080)
                .transform(CenterCrop(), RoundedCorners(32))
                .placeholder(R.drawable.loading_animation)
            )
            .into(resultImage)

        // Apply black and white filter after loading
        resultImage.colorFilter = ColorMatrixColorFilter(blackAndWhiteMatrix)

        val dialog = AlertDialog.Builder(this, R.style.Theme_Dialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Setup buttons
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            // Save image to gallery
            val drawable = resultImage.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "Tattoo_${System.currentTimeMillis()}",
                    "Generated tattoo design"
                )
                Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun showError(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Error")
            .setMessage(message)
            .setPositiveButton("Reintentar") { _, _ ->
                if (retryCount < 3) {
                    retryCount++
                    val prompt = promptEditText.text.toString()
                    val style = selectedStyle?.name ?: "Tradicional"
                    generateTattooDesign(prompt, style)
                } else {
                    Toast.makeText(this, "Límite de reintentos alcanzado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}