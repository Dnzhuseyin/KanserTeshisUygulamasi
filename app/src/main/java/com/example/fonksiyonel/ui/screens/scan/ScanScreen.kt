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
import com.example.fonksiyonel.model.ModelOutput
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
    var modelOutput by remember { mutableStateOf<ModelOutput?>(null) }
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
                    val rawOutput = withContext(Dispatchers.IO) {
                        skinCancerClassifier.getModelRawOutput(uri)
                    }
                    modelOutput = rawOutput
                    
                    // Eski diagnosis sonucunu da hesaplayalım ama göstermeyelim
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
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(bottom = 16.dp)
                                )
                                
                                if (isAnalyzing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .align(Alignment.CenterHorizontally)
                                            .padding(top = 8.dp, bottom = 8.dp)
                                    )
                                    Text(
                                        text = "Analiz ediliyor...",
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        fontSize = 16.sp
                                    )
                                } else if (modelOutput != null) {
                                    // Show raw model outputs
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 16.dp)
                                    ) {
                                        Text(
                                            text = "Ham Değerler:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )
                                        
                                        // Display raw values
                                        modelOutput?.rawOutputs?.forEachIndexed { index, value ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${modelOutput?.classLabels?.get(index) ?: "Sınıf $index"}: ",
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "$value",
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                        
                                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                        
                                        Text(
                                            text = "Yüzdelik Değerler:",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(bottom = 8.dp, top = 8.dp)
                                        )
                                        
                                        // Display percentage values
                                        modelOutput?.percentages?.forEachIndexed { index, value ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(bottom = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${modelOutput?.classLabels?.get(index) ?: "Sınıf $index"}: ",
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 14.sp
                                                )
                                                Text(
                                                    text = "${(value * 100).toInt()}%",
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                    }
                                    
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    
                                    Text(
                                        text = "Bu sonuçlar modelin ham çıktısını göstermektedir ve sınıflandırma yapılmamıştır.",
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = 14.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } 
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
