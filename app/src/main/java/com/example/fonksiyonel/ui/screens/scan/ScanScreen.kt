package com.example.fonksiyonel.ui.screens.scan

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.CancerType
import com.example.fonksiyonel.model.DiagnosisResult
import com.example.fonksiyonel.model.RiskLevel
import com.example.fonksiyonel.model.SkinCancerClassifier
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    onScanComplete: (String) -> Unit
) {
    val context = LocalContext.current
    
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isAnalyzing by remember { mutableStateOf(false) }
    var analysisResult by remember { mutableStateOf<DiagnosisResult?>(null) }
    val coroutineScope = rememberCoroutineScope()
    
    // Initialize the skin cancer classifier
    val skinCancerClassifier = remember { SkinCancerClassifier(context) }
    
    // Clean up resources when the composable is disposed
    DisposableEffect(Unit) {
        onDispose {
            skinCancerClassifier.close()
        }
    }
    
    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
        }
    }
    
    // Function to analyze the image using the TensorFlow Lite model
    val analyzeImage: () -> Unit = {
        imageUri?.let { uri ->
            isAnalyzing = true
            
            coroutineScope.launch {
                try {
                    // Use withContext to perform the model inference on IO dispatcher
                    val result = withContext(Dispatchers.IO) {
                        skinCancerClassifier.classifyImage(uri)
                    }
                    analysisResult = result
                } catch (e: Exception) {
                    // Handle any errors that might occur during classification
                    e.printStackTrace()
                    // Provide a fallback result or show an error message
                } finally {
                    isAnalyzing = false
                }
            }
        } ?: run {
            // No image selected
            isAnalyzing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Yapay Zeka Taraması") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Geri"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (imageUri != null) {
                // Image Preview and Analysis
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = rememberAsyncImagePainter(imageUri),
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (isAnalyzing) {
                        // Loading Indicator
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "Görsel analiz ediliyor...",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else if (analysisResult != null) {
                        // Analysis Result
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Analiz Sonucu",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                // Result Icon
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            when (analysisResult?.riskLevel) {
                                                RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                                                RiskLevel.MEDIUM -> Color(0xFFFFA000)
                                                RiskLevel.HIGH -> Color(0xFFF57C00)
                                                RiskLevel.VERY_HIGH -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = when (analysisResult?.cancerType) {
                                                CancerType.BENIGN -> R.drawable.ic_check_circle
                                                else -> R.drawable.ic_warning
                                            }
                                        ),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                // Cancer Type
                                Text(
                                    text = when (analysisResult?.cancerType) {
                                        CancerType.BENIGN -> "İyi Huylu (Benign)"
                                        CancerType.MELANOMA -> "Melanoma"
                                        CancerType.BASAL_CELL_CARCINOMA -> "Bazal Hücreli Karsinom"
                                        CancerType.SQUAMOUS_CELL_CARCINOMA -> "Skuamöz Hücreli Karsinom"
                                        else -> "Bilinmiyor"
                                    },
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Confidence
                                Text(
                                    text = "Güven Oranı: ${(analysisResult?.confidencePercentage?.times(100))?.toInt()}%",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Risk Level
                                Box(
                                    modifier = Modifier
                                        .padding(vertical = 8.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            when (analysisResult?.riskLevel) {
                                                RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                                                RiskLevel.MEDIUM -> Color(0xFFFFA000)
                                                RiskLevel.HIGH -> Color(0xFFF57C00)
                                                RiskLevel.VERY_HIGH -> MaterialTheme.colorScheme.error
                                                else -> MaterialTheme.colorScheme.primary
                                            }
                                        )
                                        .padding(horizontal = 24.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = when (analysisResult?.riskLevel) {
                                            RiskLevel.LOW -> "Düşük Risk"
                                            RiskLevel.MEDIUM -> "Orta Risk"
                                            RiskLevel.HIGH -> "Yüksek Risk"
                                            RiskLevel.VERY_HIGH -> "Çok Yüksek Risk"
                                            else -> "Bilinmiyor"
                                        },
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                                
                                // Warning
                                Text(
                                    text = "Bu sonuç sadece ön teşhistir. Mutlaka doktorunuza danışın.",
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    imageUri = null
                                    analysisResult = null
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(end = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Yeni Tarama")
                            }
                            
                            Button(
                                onClick = { onScanComplete("report123") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .padding(start = 8.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Raporu Kaydet")
                            }
                        }
                    } else {
                        // Analyze Button
                        Button(
                            onClick = { analyzeImage() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Analiz Et")
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Change Image Button
                        OutlinedButton(
                            onClick = { imageUri = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Görseli Değiştir")
                        }
                    }
                }
            } else {
                // Initial Screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // App Logo
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(bottom = 24.dp)
                    )
                    
                    // Title
                    Text(
                        text = "Yapay Zeka ile Cilt Taraması",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Description
                    Text(
                        text = "Şüpheli bir lekenin fotoğrafını galeriden yükleyerek analiz edebilirsiniz.",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Gallery Button
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Galeriden Yükle")
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Information
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Bilgilendirme",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            
                            Text(
                                text = "Bu uygulama sadece ön teşhis amaçlıdır. Kesin teşhis için mutlaka bir dermatoloğa başvurunuz.",
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
