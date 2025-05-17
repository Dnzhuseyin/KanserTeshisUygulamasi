package com.example.fonksiyonel.ui.screens.reports

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.CancerType
import com.example.fonksiyonel.model.DiagnosisResult
import com.example.fonksiyonel.model.Report
import com.example.fonksiyonel.model.RiskLevel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    onNavigateToShareWithDoctor: (String) -> Unit
) {
    // In a real app, this would come from a ViewModel
    val report = remember {
        Report(
            id = reportId,
            userId = "user123",
            imageUrl = "",
            diagnosisResult = DiagnosisResult(
                cancerType = CancerType.BENIGN,
                confidencePercentage = 0.92f,
                riskLevel = RiskLevel.LOW
            ),
            createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
            doctorFeedback = "Hastanın durumu iyi görünüyor. Düzenli kontrollere devam edilmesi önerilir."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rapor Detayı") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Geri"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToShareWithDoctor(reportId) }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_share),
                            contentDescription = "Doktor ile Paylaş"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Report Date
            Text(
                text = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                    .format(Date(report.createdAt)),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Center
            )
            
            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (report.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(report.imageUrl),
                        contentDescription = "Report Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_image),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Diagnosis Result
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
                        text = "Teşhis Sonucu",
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
                                when (report.diagnosisResult?.riskLevel) {
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
                                id = when (report.diagnosisResult?.cancerType) {
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
                        text = when (report.diagnosisResult?.cancerType) {
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
                        text = "Güven Oranı: ${(report.diagnosisResult?.confidencePercentage?.times(100))?.toInt()}%",
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
                                when (report.diagnosisResult?.riskLevel) {
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
                            text = when (report.diagnosisResult?.riskLevel) {
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
            
            // Doctor Feedback
            if (report.doctorFeedback != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Doktor Geri Bildirimi",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        Text(
                            text = report.doctorFeedback,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
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
                    onClick = { onNavigateToShareWithDoctor(reportId) },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(end = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Doktor ile Paylaş")
                }
                
                Button(
                    onClick = { /* Navigate to appointment */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_appointment),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Randevu Al")
                }
            }
        }
    }
}
