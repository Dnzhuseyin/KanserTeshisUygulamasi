package com.example.fonksiyonel.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.example.fonksiyonel.model.Badge
import com.example.fonksiyonel.model.DiagnosisResult
import com.example.fonksiyonel.model.Report
import com.example.fonksiyonel.model.User
import com.example.fonksiyonel.ui.components.MenuOption
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToReportHistory: () -> Unit,
    onNavigateToScan: () -> Unit,
    onNavigateToAppointment: () -> Unit,
    onNavigateToBadges: () -> Unit,
    onNavigateToReportDetail: (String) -> Unit,
    onLogout: () -> Unit
) {
    // In a real app, this would come from a ViewModel
    val currentUser = remember {
        User(
            id = "user123",
            name = "Ahmet Yılmaz",
            email = "ahmet@example.com",
            profilePhotoUrl = null,
            points = 120,
            badges = listOf(
                Badge(
                    id = "badge1",
                    title = "İlk Tarama",
                    description = "İlk taramanızı gerçekleştirdiniz",
                    earnedDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
                )
            ),
            lastAnalysisDate = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
        )
    }
    
    val latestReport = remember {
        Report(
            id = "report123",
            userId = currentUser.id,
            imageUrl = "",
            diagnosisResult = DiagnosisResult(
                cancerType = com.example.fonksiyonel.model.CancerType.BENIGN,
                confidencePercentage = 0.92f,
                riskLevel = com.example.fonksiyonel.model.RiskLevel.LOW
            ),
            createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
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
                title = { Text("Anasayfa") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // User Profile Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentUser.profilePhotoUrl != null) {
                            Image(
                                painter = rememberAsyncImagePainter(currentUser.profilePhotoUrl),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = currentUser.name.first().toString(),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    
                    // User Name
                    Text(
                        text = currentUser.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    
                    // User Type
                    Text(
                        text = if (currentUser.userType.toString() == "PATIENT") "Hasta" else "Doktor",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    
                    // User Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Points
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = currentUser.points.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Puan",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        // Badges
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onNavigateToBadges() }
                        ) {
                            Text(
                                text = currentUser.badges.size.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Rozet",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                        
                        // Last Analysis
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = if (currentUser.lastAnalysisDate != null) {
                                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                        .format(Date(currentUser.lastAnalysisDate))
                                } else {
                                    "Yok"
                                },
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Son Tarama",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            
            // Latest Diagnosis Result
            if (latestReport.diagnosisResult != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .clickable { onNavigateToReportDetail(latestReport.id) },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Son Teşhis Sonucu",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Result Icon
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (latestReport.diagnosisResult.riskLevel) {
                                            com.example.fonksiyonel.model.RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                                            com.example.fonksiyonel.model.RiskLevel.MEDIUM -> Color(0xFFFFA000)
                                            com.example.fonksiyonel.model.RiskLevel.HIGH -> Color(0xFFF57C00)
                                            com.example.fonksiyonel.model.RiskLevel.VERY_HIGH -> MaterialTheme.colorScheme.error
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = when (latestReport.diagnosisResult.cancerType) {
                                            com.example.fonksiyonel.model.CancerType.BENIGN -> R.drawable.ic_check_circle
                                            else -> R.drawable.ic_warning
                                        }
                                    ),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Result Details
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = when (latestReport.diagnosisResult.cancerType) {
                                        com.example.fonksiyonel.model.CancerType.BENIGN -> "İyi Huylu (Benign)"
                                        com.example.fonksiyonel.model.CancerType.MELANOMA -> "Melanoma"
                                        com.example.fonksiyonel.model.CancerType.BASAL_CELL_CARCINOMA -> "Bazal Hücreli Karsinom"
                                        com.example.fonksiyonel.model.CancerType.SQUAMOUS_CELL_CARCINOMA -> "Skuamöz Hücreli Karsinom"
                                        else -> "Bilinmiyor"
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                
                                Text(
                                    text = "Güven: ${(latestReport.diagnosisResult.confidencePercentage * 100).toInt()}%",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                                
                                Text(
                                    text = "Risk: ${
                                        when (latestReport.diagnosisResult.riskLevel) {
                                            com.example.fonksiyonel.model.RiskLevel.LOW -> "Düşük"
                                            com.example.fonksiyonel.model.RiskLevel.MEDIUM -> "Orta"
                                            com.example.fonksiyonel.model.RiskLevel.HIGH -> "Yüksek"
                                            com.example.fonksiyonel.model.RiskLevel.VERY_HIGH -> "Çok Yüksek"
                                        }
                                    }",
                                    fontSize = 14.sp,
                                    color = when (latestReport.diagnosisResult.riskLevel) {
                                        com.example.fonksiyonel.model.RiskLevel.LOW -> MaterialTheme.colorScheme.tertiary
                                        com.example.fonksiyonel.model.RiskLevel.MEDIUM -> Color(0xFFFFA000)
                                        com.example.fonksiyonel.model.RiskLevel.HIGH -> Color(0xFFF57C00)
                                        com.example.fonksiyonel.model.RiskLevel.VERY_HIGH -> MaterialTheme.colorScheme.error
                                    }
                                )
                            }
                            
                            // Date
                            Text(
                                text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                                    .format(Date(latestReport.createdAt)),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
            
            // Menu Options
            Text(
                text = "Menü",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuOption(
                    icon = R.drawable.ic_scan,
                    title = "Yeni Tarama",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToScan
                )
                
                MenuOption(
                    icon = R.drawable.ic_history,
                    title = "Raporlarım",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToReportHistory
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuOption(
                    icon = R.drawable.ic_share,
                    title = "Doktor ile Paylaş",
                    modifier = Modifier.weight(1f),
                    onClick = { 
                        if (latestReport.id.isNotEmpty()) {
                            onNavigateToReportDetail(latestReport.id)
                        }
                    }
                )
                
                MenuOption(
                    icon = R.drawable.ic_appointment,
                    title = "Randevu Al",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAppointment
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MenuOption(
                    icon = R.drawable.ic_badge,
                    title = "Rozetlerim",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBadges
                )
                
                MenuOption(
                    icon = R.drawable.ic_settings,
                    title = "Ayarlar",
                    modifier = Modifier.weight(1f),
                    onClick = { /* Navigate to settings */ }
                )
            }
        }
    }
}
