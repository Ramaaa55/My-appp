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
import android.graphics.Bitmap
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem

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

    // AdMob variables
    private var rewardedAd: RewardedAd? = null
    private var interstitialAd: InterstitialAd? = null
    private var pendingImageBytes: ByteArray? = null
    private var pendingStyle: String? = null
    private var isLoadingFinished = false

    // Ad unit IDs
    private val rewardedAdUnitId = "ca-app-pub-4766140355071916/2782442591"
    private val interstitialAdUnitId = "ca-app-pub-4766140355071916/3702521413"
    private val bannerAdUnitId = "ca-app-pub-4766140355071916/3535000457"

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
        "Lobo aullando a la luna llena",
        "Fénix emergiendo de las llamas",
        "Dragón enrollado alrededor de una espada",
        "Ojo que todo lo ve con símbolos alquímicos",
        "Serpiente entrelazada con rosas",
        "Árbol de la vida con raíces profundas",
        "Calavera mexicana con decoraciones florales",
        "León coronado con constelaciones estelares",
        "Olas oceánicas rompiendo contra acantilados",
        "Búho posado sobre libros antiguos",
        "Mandala con geometría sagrada",
        "Tigre en movimiento entre bambúes",
        "Mano sosteniendo un corazón llameante",
        "Ballena jorobada con sistema planetario",
        "Colibrí libando flor tropical",
        "Águila desplumando sus alas",
        "Esqueleto de dinosaurio en actitud feroz",
        "Escorpión con cola en espiral",
        "Tortuga marina navegando corrientes",
        "Llave antigua con engranajes visibles",
        "Cadenas rotas convertidas en plumas",
        "Círculo de piedras celtas con runas",
        "Venado con astas formando ramas",
        "Abejas rodeando panal hexagonal",
        "Ancla marina con sirenas mitológicas",
        "Reloj de arena con estrellas fugaces",
        "Mariposa monarca con mapa migratorio",
        "Puño cerrado rompiendo cadenas",
        "Golondrinas en formación de vuelo",
        "Líneas de Nazca estilizadas",
        "Quetzalcoatl en pleno movimiento",
        "Huellas de animales en espiral",
        "Círculo zodiacal con símbolos ocultos",
        "Herramientas de artesano medievales",
        "Daga atravesando nube de humo",
        "Manos entrelazadas formando raíces",
        "Máscara veneciana con detalles barrocos",
        "Símbolos de elementos naturales (tierra, aire, fuego, agua)",
        "Barcos vikingos navegando tormentas",
        "Espiral de galaxia en un ojo humano",
        "Huellas digitales convertidas en paisaje",
        "Símbolos de resistencia indígena",
        "Círculo de hongos mágicos en bosque",
        "Plumas de aves exóticas entrelazadas",
        "Esqueleto de pez con arrecife coralino",
        "Instrumentos musicales ancestrales",
        "Neuronas formando constelaciones",
        "Huracán conteniendo animales mitológicos",
        "Escalera al cielo con peldaños rotos",
        "Símbolo infinito con fractales matemáticos"
    )

    private var retryCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize AdMob
        MobileAds.initialize(this) { initializationStatus ->
            Log.d("AdMob", "Initialization complete: $initializationStatus")
            // Load ads after initialization
            loadRewardedAd()
            loadInterstitialAd()
        }

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

    private fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(this, rewardedAdUnitId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdMob", "Rewarded ad failed to load: ${adError.message}")
                rewardedAd = null
            }

            override fun onAdLoaded(ad: RewardedAd) {
                Log.d("AdMob", "Rewarded ad loaded successfully")
                rewardedAd = ad
                setupRewardedAdCallbacks()
            }
        })
    }

    private fun setupRewardedAdCallbacks() {
        rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdMob", "Rewarded ad was dismissed")
                // Load a new rewarded ad for next time
                loadRewardedAd()

                // Show the tattoo result after ad is dismissed
                pendingImageBytes?.let { bytes ->
                    pendingStyle?.let { style ->
                        showResultDialog(bytes, style)
                        pendingImageBytes = null
                        pendingStyle = null
                    }
                }
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("AdMob", "Rewarded ad failed to show: ${adError.message}")
                // Show the tattoo result anyway if ad fails to show
                pendingImageBytes?.let { bytes ->
                    pendingStyle?.let { style ->
                        showResultDialog(bytes, style)
                        pendingImageBytes = null
                        pendingStyle = null
                    }
                }
                loadRewardedAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdMob", "Rewarded ad showed full screen content")
                rewardedAd = null
            }
        }
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, interstitialAdUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("AdMob", "Interstitial ad failed to load: ${adError.message}")
                interstitialAd = null
            }

            override fun onAdLoaded(ad: InterstitialAd) {
                Log.d("AdMob", "Interstitial ad loaded successfully")
                interstitialAd = ad
                setupInterstitialAdCallbacks()
            }
        })
    }

    private fun setupInterstitialAdCallbacks() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d("AdMob", "Interstitial ad was dismissed")
                // Load a new interstitial ad for next time
                loadInterstitialAd()

                // Download the image after ad is dismissed
                downloadCurrentImage()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.d("AdMob", "Interstitial ad failed to show: ${adError.message}")
                // Download the image anyway if ad fails to show
                downloadCurrentImage()
                loadInterstitialAd()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d("AdMob", "Interstitial ad showed full screen content")
                interstitialAd = null
            }
        }
    }

    private fun showRewardedAd() {
        if (rewardedAd != null) {
            rewardedAd?.show(this, OnUserEarnedRewardListener { rewardItem ->
                Log.d("AdMob", "User earned reward: ${rewardItem.amount} ${rewardItem.type}")
                // The reward callback is handled in fullScreenContentCallback
            })
        } else {
            Log.d("AdMob", "Rewarded ad wasn't loaded yet, showing result directly")
            // Show the tattoo result anyway if ad isn't loaded
            pendingImageBytes?.let { bytes ->
                pendingStyle?.let { style ->
                    showResultDialog(bytes, style)
                    pendingImageBytes = null
                    pendingStyle = null
                }
            }
            // Try to load a new ad for next time
            loadRewardedAd()
        }
    }

    private fun showInterstitialAd() {
        if (interstitialAd != null) {
            interstitialAd?.show(this)
        } else {
            Log.d("AdMob", "Interstitial ad wasn't loaded yet, downloading directly")
            // Download the image anyway if ad isn't loaded
            downloadCurrentImage()
            // Try to load a new ad for next time
            loadInterstitialAd()
        }
    }

    private fun downloadCurrentImage() {
        val drawable = generatedTattoo.drawable
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

    private fun showLoading() {
        val loadingOverlay = findViewById<View>(R.id.loading_overlay)
        loadingOverlay?.visibility = View.VISIBLE
        isLoadingFinished = false

        // Add fade-in animation
        loadingOverlay?.alpha = 0f
        loadingOverlay?.animate()?.alpha(1f)?.setDuration(300)?.start()
    }

    private fun hideLoading() {
        val loadingOverlay = findViewById<View>(R.id.loading_overlay)

        // Add fade-out animation
        loadingOverlay?.animate()?.alpha(0f)?.setDuration(300)?.withEndAction {
            loadingOverlay.visibility = View.GONE
            isLoadingFinished = true
        }?.start()
    }

    private fun generateTattooDesign(prompt: String, style: String) {
        showLoading()
        currentImageJob?.cancel()

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Enhanced prompt construction with better style-specific details
        val stylePrompt = when (style) {
            "Japonés" -> "Japanese irezumi style tattoo with clean lines and traditional motifs"
            "Realista" -> "Hyper-realistic tattoo with fine details, shading and depth"
            "Tradicional" -> "American traditional/old school tattoo with bold outlines and limited color palette"
            "Sagrado" -> "Sacred geometry tattoo with precise geometric patterns and symmetrical design"
            "Futurista" -> "Futuristic cyberpunk tattoo with neon elements and technological themes"
            else -> style
        }

        // Create a more specific prompt that ensures the tattoo design follows the style
        val enhancedPrompt = "Professional tattoo design of $prompt in $stylePrompt style. High contrast black and white tattoo art with clean lines on white background. Detailed tattoo flash art, monochrome ink drawing, 8k resolution."

        val encodedPrompt = URLEncoder.encode(enhancedPrompt, "UTF-8")
        val imageUrl = "https://image.pollinations.ai/prompt/$encodedPrompt?width=512&height=512&nologo=true&seed=${System.currentTimeMillis()}"

        Log.d("ImageDebug", "URL: $imageUrl")
        Log.d("PromptDebug", "Enhanced Prompt: $enhancedPrompt")

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
                            // Store the image bytes and style for later use after the ad
                            pendingImageBytes = bytes
                            pendingStyle = style

                            // Simulate a delay to show the loading spinner for a bit longer
                            // then show the rewarded ad just before revealing the result
                            Handler(Looper.getMainLooper()).postDelayed({
                                // Show rewarded ad before showing the result
                                showRewardedAd()
                                hideLoading()
                            }, 1500) // Show ad after 1.5 seconds
                        } else {
                            hideLoading()
                            showError("Empty response")
                        }
                    }
                }
            }
        })
    }

    private fun showResultDialog(imageBytes: ByteArray, style: String) {
        // Create and show result dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_result, null)
        val resultImage = dialogView.findViewById<ImageView>(R.id.result_image)
        val closeButton = dialogView.findViewById<Button>(R.id.close_button)
        val saveButton = dialogView.findViewById<Button>(R.id.save_button)
        val adContainer = dialogView.findViewById<FrameLayout>(R.id.ad_container)

        // Apply style-specific filters
        val contrastValue = when (style) {
            "Realista" -> 1.4f  // Higher contrast for realism
            "Tradicional" -> 1.5f  // Very high contrast for traditional
            "Japonés" -> 1.3f  // Medium-high contrast for Japanese
            else -> 1.3f  // Default contrast
        }

        val blackAndWhiteMatrix = ColorMatrix().apply {
            setSaturation(0f) // Remove all color (make it grayscale)
            // Increase contrast based on style
            val scale = contrastValue
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
            .asBitmap()
            .load(imageBytes)
            .apply(RequestOptions()
                .override(1080, 1080)
                .transform(CenterCrop(), RoundedCorners(32))
                .placeholder(R.drawable.loading_animation)
            )
            .into(object : com.bumptech.glide.request.target.CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    // Apply black and white filter to bitmap
                    val paint = android.graphics.Paint().apply {
                        colorFilter = ColorMatrixColorFilter(blackAndWhiteMatrix)
                    }

                    val filteredBitmap = Bitmap.createBitmap(resource.width, resource.height, Bitmap.Config.ARGB_8888)
                    val canvas = android.graphics.Canvas(filteredBitmap)
                    canvas.drawBitmap(resource, 0f, 0f, paint)

                    // Set the filtered bitmap to the ImageView
                    resultImage.setImageBitmap(filteredBitmap)
                    generatedTattoo = resultImage
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    resultImage.setImageDrawable(placeholder)
                }
            })

        // Add banner ad to the dialog
        val adView = AdView(this)
        adView.adUnitId = bannerAdUnitId
        adView.setAdSize(AdSize.BANNER)

        // Set up the layout parameters for the AdView
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.spacing_md)
        adView.layoutParams = layoutParams

        // Load the banner ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        // Add the AdView to the layout
        adContainer.addView(adView)

        // Set up ad listener
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                Log.d("AdMob", "Banner ad loaded successfully")
                adContainer.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                Log.d("AdMob", "Banner ad failed to load: ${error.message}")
                adContainer.visibility = View.GONE
            }
        }

        val dialog = AlertDialog.Builder(this, R.style.Theme_Dialog)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        // Setup buttons
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        saveButton.setOnClickListener {
            // Show interstitial ad before downloading the image
            showInterstitialAd()
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