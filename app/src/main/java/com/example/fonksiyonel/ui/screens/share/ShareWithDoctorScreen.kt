package com.example.fonksiyonel.ui.screens.share

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.Report

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareWithDoctorScreen(
    reportId: String,
    onNavigateBack: () -> Unit,
    onShareComplete: () -> Unit
) {
    // In a real app, this would come from a ViewModel
    val report = remember {
        Report(
            id = reportId,
            userId = "user123",
            imageUrl = "",
            createdAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000
        )
    }
    
    var doctorEmail by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    
    // Saved doctors (in a real app, this would come from a database)
    val savedDoctors = remember {
        listOf(
            "dr.ahmet@example.com",
            "dr.ayse@example.com",
            "dr.mehmet@example.com"
        )
    }
    
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onShareComplete()
            },
            title = { Text("Başarılı") },
            text = { Text("Rapor doktor ile başarıyla paylaşıldı.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onShareComplete()
                    }
                ) {
                    Text("Tamam")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doktor ile Paylaş") },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Raporu Doktor ile Paylaş",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            // Error Message
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            
            // Doctor Email
            OutlinedTextField(
                value = doctorEmail,
                onValueChange = { doctorEmail = it },
                label = { Text("Doktor E-posta Adresi") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Saved Doctors
            Text(
                text = "Kayıtlı Doktorlar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start
            )
            
            // Doctor Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                savedDoctors.forEach { email ->
                    SuggestionChip(
                        onClick = { doctorEmail = email },
                        label = { Text(email) }
                    )
                }
            }
            
            // Note
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Not (İsteğe Bağlı)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp)
            )
            
            // Share Button
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    
                    // Validate email
                    if (doctorEmail.isBlank()) {
                        errorMessage = "Lütfen doktor e-posta adresini girin"
                        isLoading = false
                        return@Button
                    }
                    
                    // Simulate sharing (replace with actual sharing logic)
                    // In a real app, this would be a call to your backend service
                    showSuccessDialog = true
                    isLoading = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Raporu Paylaş")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
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
                        text = "Raporunuz, belirttiğiniz e-posta adresine PDF formatında gönderilecektir. Doktorunuz, raporunuzu inceleyip size geri bildirim sağlayabilecektir.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}
