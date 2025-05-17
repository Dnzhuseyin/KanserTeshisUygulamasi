package com.example.fonksiyonel.ui.screens.reports

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.CancerType
import com.example.fonksiyonel.model.DiagnosisResult
import com.example.fonksiyonel.model.Report
import com.example.fonksiyonel.model.RiskLevel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportHistoryScreen(
    onNavigateBack: () -> Unit,
    onNavigateToReportDetail: (String) -> Unit
) {
    // In a real app, this would come from a ViewModel
    val reports = remember {
        listOf(
            Report(
                id = "report1",
                userId = "user123",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.BENIGN,
                    confidencePercentage = 0.92f,
                    riskLevel = RiskLevel.LOW
                ),
                createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
            ),
            Report(
                id = "report2",
                userId = "user123",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.MELANOMA,
                    confidencePercentage = 0.78f,
                    riskLevel = RiskLevel.HIGH
                ),
                createdAt = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000
            ),
            Report(
                id = "report3",
                userId = "user123",
                imageUrl = "",
                diagnosisResult = DiagnosisResult(
                    cancerType = CancerType.BASAL_CELL_CARCINOMA,
                    confidencePercentage = 0.85f,
                    riskLevel = RiskLevel.MEDIUM
                ),
                createdAt = System.currentTimeMillis() - 25 * 24 * 60 * 60 * 1000
            )
        )
    }
    
    var filteredReports by remember { mutableStateOf(reports) }
    var selectedRiskLevel by remember { mutableStateOf<RiskLevel?>(null) }
    var sortByNewest by remember { mutableStateOf(true) }
    
    // Filter and sort reports
    LaunchedEffect(selectedRiskLevel, sortByNewest) {
        filteredReports = reports
            .let { list ->
                if (selectedRiskLevel != null) {
                    list.filter { it.diagnosisResult?.riskLevel == selectedRiskLevel }
                } else {
                    list
                }
            }
            .let { list ->
                if (sortByNewest) {
                    list.sortedByDescending { it.createdAt }
                } else {
                    list.sortedBy { it.createdAt }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rapor Geçmişi") },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Filter and Sort Options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Risk Level Filter
                Box {
                    var expanded by remember { mutableStateOf(false) }
                    
                    OutlinedButton(
                        onClick = { expanded = true },
                        modifier = Modifier.width(160.dp)
                    ) {
                        Text(
                            text = when (selectedRiskLevel) {
                                RiskLevel.LOW -> "Düşük Risk"
                                RiskLevel.MEDIUM -> "Orta Risk"
                                RiskLevel.HIGH -> "Yüksek Risk"
                                RiskLevel.VERY_HIGH -> "Çok Yüksek Risk"
                                null -> "Tüm Riskler"
                            }
                        )
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Tüm Riskler") },
                            onClick = {
                                selectedRiskLevel = null
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Düşük Risk") },
                            onClick = {
                                selectedRiskLevel = RiskLevel.LOW
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Orta Risk") },
                            onClick = {
                                selectedRiskLevel = RiskLevel.MEDIUM
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Yüksek Risk") },
                            onClick = {
                                selectedRiskLevel = RiskLevel.HIGH
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Çok Yüksek Risk") },
                            onClick = {
                                selectedRiskLevel = RiskLevel.VERY_HIGH
                                expanded = false
                            }
                        )
                    }
                }
                
                // Sort Option
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Sırala: ")
                    TextButton(
                        onClick = { sortByNewest = !sortByNewest }
                    ) {
                        Text(if (sortByNewest) "En Yeni" else "En Eski")
                    }
                }
            }
            
            // Reports List
            if (filteredReports.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Henüz rapor bulunmamaktadır",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredReports) { report ->
                        ReportItem(
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
fun ReportItem(
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
            
            // Report Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                // Date
                Text(
                    text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        .format(Date(report.createdAt)),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Cancer Type
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
                
                // Confidence
                Text(
                    text = "Güven: ${(report.diagnosisResult?.confidencePercentage?.times(100))?.toInt()}%",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Risk Level
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 36.dp)
                    .clip(RoundedCornerShape(18.dp))
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
                Text(
                    text = when (report.diagnosisResult?.riskLevel) {
                        RiskLevel.LOW -> "Düşük Risk"
                        RiskLevel.MEDIUM -> "Orta Risk"
                        RiskLevel.HIGH -> "Yüksek Risk"
                        RiskLevel.VERY_HIGH -> "Çok Yüksek"
                        else -> "Bilinmiyor"
                    },
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
