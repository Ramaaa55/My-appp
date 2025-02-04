package com.mysticmango.idealtattooia

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.mysticmango.idealtattooia.TattooStyle
import com.mysticmango.idealtattooia.TattooStyleProvider
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import coil.compose.AsyncImage
import com.mysticmango.idealtattooia.DarkColorScheme
import com.mysticmango.idealtattooia.Typography
import com.mysticmango.idealtattooia.textPrimary
import com.mysticmango.idealtattooia.transparent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = DarkColorScheme,
                typography = Typography
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TattooGrid()
                }
            }
        }
    }
}

@Composable
fun TattooGeneratorApp() {
    val tattooDesigns = listOf(
        TattooStyle(R.drawable.estilo_japones, "Japonés"),
        TattooStyle(R.drawable.estilo_realista, "Realista"),
        TattooStyle(R.drawable.estilo_tradicional, "Tradicional"),
        TattooStyle(R.drawable.estilo_sagrado, "Sagrado"),
        TattooStyle(R.drawable.estilo_futurista, "Futurista")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Logo
            Text(
                text = "Ideal Tattoo ia",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Prompt Section
            PromptSection()

            Spacer(modifier = Modifier.height(16.dp))

            // Style Selection
            StyleSection(tattooDesigns)
        }
    }
}

@Composable
fun PromptSection() {
    val promptText = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Describe your ideal tattoo",
            style = Typography.titleSmall,
            color = textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = promptText.value,
            onValueChange = { promptText.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    shape = RoundedCornerShape(16.dp)
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(16.dp)
                ),
            textStyle = Typography.bodyMedium.copy(color = textPrimary),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = transparent,
                unfocusedContainerColor = transparent,
                disabledContainerColor = transparent,
                focusedIndicatorColor = transparent,
                unfocusedIndicatorColor = transparent
            )
        )
    }
}

@Composable
fun StyleSection(styles: List<TattooStyle>) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp)),
        color = Color(0xFF1C1C1C)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "Selecciona el estilo",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(styles) { style ->
                    StyleCard(style)
                }
            }

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
                    .clip(RoundedCornerShape(50)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF6B00)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Generar Tatuaje",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        "Ver anuncio",
                        fontSize = 14.sp,
                        color = Color.Black.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

@Composable
fun StyleCard(style: TattooStyle) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier
                .width(160.dp)
                .aspectRatio(3f/4f)
                .clip(RoundedCornerShape(16.dp)),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(style.imageResId),
                    contentDescription = "Estilo ${style.name}",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Light grey overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.1f))
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = style.name,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun TattooGrid() {
    val designs = TattooStyleProvider.defaultStyles()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(16.dp)
    ) {
        items(designs) { style ->
            TattooCard(style)
        }
    }
}

@Composable
private fun TattooCard(style: TattooStyle) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        AsyncImage(
            model = style.imageResId,
            contentDescription = style.name,
            modifier = Modifier.size(200.dp)
        )
    }
}

