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

        // Configurar visibilidad inicial
        binding.scrimView.visibility = View.GONE
        binding.loadingContainer.visibility = View.GONE
        binding.resultContainer.visibility = View.GONE

        styleRecyclerView = binding.styleRecycler
        promptEditText = binding.promptInput
        btnSuggest = binding.surpriseButton
        btnGenerate = binding.generateButton
        generatedTattoo = binding.generatedTattoo

        setupLoadingDialog()
        setupButtons()
        setupStyleSelector()
        setupLoadingSpinner()

        supportActionBar?.show()

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading, null)
        val spinKitView = dialogView.findViewById<SpinKitView>(R.id.spin_kit)
        spinKitView.setIndeterminateDrawable(Circle())

        binding.styleRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = tattooAdapter
        }

        binding.generateButton.setOnClickListener {
            val prompt = binding.promptInput.text.toString().trim()
            val selectedStyle = (binding.styleRecycler.adapter as TattooAdapter).getSelectedStyle()

            if (prompt.isNotEmpty() && selectedStyle != null) {
                generateTattooDesign(prompt, selectedStyle.name)
            } else {
                Toast.makeText(this, "Ingresa un prompt y selecciona un estilo", Toast.LENGTH_SHORT).show()
            }
        }

        binding.downloadButton.setOnClickListener { saveToGallery() }
        binding.closeButton.setOnClickListener {
            binding.scrimView.visibility = View.GONE
            binding.resultContainer.visibility = View.GONE
            binding.surpriseButton.visibility = View.VISIBLE
        }
    }

    private fun setupStyleSelector() {
        binding.styleRecycler.apply {
            layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = tattooAdapter
        }
    }

    private fun setupButtons() {
        btnSuggest.setOnClickListener {
            val randomPrompt = surprisePrompts.random()
            promptEditText.setText(randomPrompt)
            Toast.makeText(this, "¡Prompt sugerido!", Toast.LENGTH_SHORT).show()
        }

        btnGenerate.setOnClickListener {
            val prompt = promptEditText.text.toString().trim()
            if (prompt.isNotEmpty()) {
                currentImageJob?.cancel()
                currentImageJob = lifecycleScope.launch {
                    generateTattooDesign(prompt, selectedStyle?.name ?: "DefaultStyle")
                }
            } else {
                Toast.makeText(this, "Ingresa una descripción primero", Toast.LENGTH_SHORT).show()
            }
        }

        // Mantener visibilidad del botón
        binding.surpriseButton.visibility = View.VISIBLE
    }

    private fun getSelectedStyle(): TattooStyle? {
        return selectedStyle
    }

    private fun showInputError() {
        val errorMessage = when {
            promptEditText.text.isNullOrEmpty() -> "¡Escribe una descripción!"
            selectedStyle == null -> "¡Selecciona un estilo!"
            else -> "Datos inválidos"
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }

    private fun setupLoadingDialog() {
    }

    private fun setupLoadingSpinner() {
    }

    private fun generateTattooDesign(prompt: String, style: String) {
        showLoading(true)
        currentImageJob?.cancel()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val encodedPrompt = URLEncoder.encode("$prompt in $style style, 8k resolution", "UTF-8")
        val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=512&height=512&nologo=true&seed=${System.currentTimeMillis()}"

        Log.d("ImageDebug", "URL: $imageUrl")

        val request = Request.Builder()
            .url(imageUrl)
            .header("Cache-Control", "no-cache")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    showLoading(false)
                    showError("Tiempo de espera agotado. Verifica tu conexión y vuelve a intentar")
                    binding.spinKit.visibility = View.GONE
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        runOnUiThread {
                            showLoading(false)
                            showError("API Error: ${response.code}")
                        }
                        return
                    }

                    val inputStream = response.body?.byteStream()
                    val bytes = inputStream?.readBytes()

                    runOnUiThread {
                        if (bytes != null && bytes.isNotEmpty()) {
                            Glide.with(this@MainActivity)
                                .load(bytes)
                                .apply(RequestOptions()
                                    .override(1080, 1080)
                                    .transform(CenterCrop(), RoundedCorners(32.dpToPx()))
                                )
                                .listener(object : RequestListener<Drawable> {
                                    override fun onResourceReady(
                                        resource: Drawable,
                                        model: Any,
                                        target: Target<Drawable>,
                                        dataSource: DataSource,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        binding.run {
                                            loadingContainer.visibility = View.GONE
                                            scrimView.visibility = View.GONE
                                            resultContainer.visibility = View.VISIBLE
                                            generatedTattoo.alpha = 1.0f
                                            surpriseButton.visibility = View.VISIBLE
                                            spinKit.visibility = View.GONE
                                        }
                                        return false
                                    }

                                    override fun onLoadFailed(
                                        e: GlideException?,
                                        model: Any?,
                                        target: Target<Drawable>,
                                        isFirstResource: Boolean
                                    ): Boolean {
                                        showLoading(false)
                                        showError("Error: ${e?.message ?: "Unknown error"}")
                                        binding.spinKit.visibility = View.GONE
                                        return true
                                    }
                                })
                                .into(binding.generatedTattoo)
                        } else {
                            showLoading(false)
                            showError("Empty response")
                        }
                    }
                }
            }
        })
    }

    private fun showLoading(show: Boolean) {
        binding.apply {
            if (show) {
                scrimView.visibility = View.VISIBLE
                surpriseButton.visibility = View.GONE
                loadingContainer.visibility = View.VISIBLE
            } else {
                scrimView.visibility = View.GONE
                surpriseButton.visibility = View.VISIBLE
                loadingContainer.visibility = View.GONE
            }
        }
    }

    private fun showResult(designUrl: String) {
        binding.apply {
            surpriseButton.visibility = View.VISIBLE

            generatedTattoo.apply {
                alpha = 1.0f
                scaleType = ImageView.ScaleType.CENTER_INSIDE
            }

            scrimView.visibility = View.VISIBLE
            loadingContainer.visibility = View.GONE
            resultContainer.visibility = View.VISIBLE
        }
    }

    private fun applyTattooStyle(imageView: ImageView) {
        imageView.apply {
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setBackgroundResource(R.drawable.rounded_white_background)
        }
    }

    private fun setupDownloadButton() {
        binding.downloadButton.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                saveToGallery()
            }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showError(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.loadingContainer.visibility = View.GONE
            AlertDialog.Builder(this@MainActivity)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("Reintentar") { _, _ ->
                    if (retryCount < 3) {
                        retryCount++
                        generateTattooDesign(
                            binding.promptInput.text.toString(),
                            selectedStyle?.name ?: "Tradicional"
                        )
                    } else {
                        Toast.makeText(this@MainActivity, "Límite de reintentos alcanzado", Toast.LENGTH_SHORT).show()
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }
    }

    fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun saveToGallery() {
        try {
            val drawable = binding.generatedTattoo.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "Tattoo_HD_${System.currentTimeMillis()}",
                    "Generated tattoo design"
                )
                Toast.makeText(this, "Imagen guardada en la galería", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            showError("Error al guardar: ${e.message}")
        }
    }
}


