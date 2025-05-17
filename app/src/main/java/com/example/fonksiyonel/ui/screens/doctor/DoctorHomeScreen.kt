package com.example.fonksiyonel.ui.screens.doctor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
fun DoctorHomeScreen(
    onNavigateToReportDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    // In a real app, this would come from a ViewModel
    val patientReports = remember {
        listOf(
            Report(
                id = "report1",
                userId = "user1",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.BENIGN,
                    confidencePercentage = 0.92f,
                    riskLevel = RiskLevel.LOW
                ),
                createdAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                sharedWithDoctors = listOf("doctor123"),
                doctorFeedback = null
            ),
            Report(
                id = "report2",
                userId = "user2",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.MELANOMA,
                    confidencePercentage = 0.78f,
                    riskLevel = RiskLevel.HIGH
                ),
                createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                sharedWithDoctors = listOf("doctor123"),
                doctorFeedback = null
            ),
            Report(
                id = "report3",
                userId = "user3",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.BASAL_CELL_CARCINOMA,
                    confidencePercentage = 0.85f,
                    riskLevel = RiskLevel.MEDIUM
                ),
                createdAt = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                sharedWithDoctors = listOf("doctor123"),
                doctorFeedback = "Hastanın durumu takip edilmeli. 1 ay sonra kontrol önerilir."
            )
        )
    }
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Çıkış Yap") },
            text = { Text("Hesabınızdan çıkış yapmak istediğinize emin misiniz?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text("İptal")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doktor Paneli") },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_logout),
                            contentDescription = "Çıkış Yap"
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Doctor Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Photo
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "D",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    // Doctor Info
                    Column {
                        Text(
                            text = "Dr. Mehmet Demir",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = "Dermatoloji Uzmanı",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        
                        Text(
                            text = "mehmet.demir@example.com",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
            
            // Reports Title
            Text(
                text = "Hasta Raporları",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
            
            // Reports List
            if (patientReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz paylaşılan rapor bulunmamaktadır",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(patientReports) { report ->
                        PatientReportItem(
                            report = report,
                            onClick = { onNavigateToReportDetail(report.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PatientReportItem(
    report: Report,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Patient Info
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Patient Avatar
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column {
                        Text(
                            text = "Hasta ${report.userId.substring(4)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Text(
                            text = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale.getDefault())
                                .format(Date(report.createdAt)),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                
                // Feedback Status
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (report.doctorFeedback != null)
                                MaterialTheme.colorScheme.tertiary
                            else
                                MaterialTheme.colorScheme.primary
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (report.doctorFeedback != null) "Yanıtlandı" else "Bekliyor",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Report Preview
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Image Preview
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
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
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Diagnosis Result
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = when (report.diagnosisResult?.cancerType) {
                            CancerType.BENIGN -> "İyi Huylu (Benign)"
                            CancerType.MELANOMA -> "Melanoma"
                            CancerType.BASAL_CELL_CARCINOMA -> "Bazal Hücreli Karsinom"
                            CancerType.SQUAMOUS_CELL_CARCINOMA -> "Skuamöz Hücreli Karsinom"
                            else -> "Bilinmiyor"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Güven: ${(report.diagnosisResult?.confidencePercentage?.times(100))?.toInt()}%",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Risk Level
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (report.diagnosisResult?.riskLevel) {
                                    RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                                    RiskLevel.MEDIUM -> Color(0xFFFFA000)
                                    RiskLevel.HIGH -> Color(0xFFF57C00)
                                    RiskLevel.VERY_HIGH -> MaterialTheme.colorScheme.error
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
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
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}
