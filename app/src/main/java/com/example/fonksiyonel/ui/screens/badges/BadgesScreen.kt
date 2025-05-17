package com.example.fonksiyonel.ui.screens.badges

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fonksiyonel.R
import com.example.fonksiyonel.model.Badge
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgesScreen(
    onNavigateBack: () -> Unit
) {
    // In a real app, this would come from a ViewModel
    val badges = remember {
        listOf(
            Badge(
                id = "badge1",
                title = "İlk Tarama",
                description = "İlk taramanızı gerçekleştirdiniz",
                earnedDate = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000
            ),
            Badge(
                id = "badge2",
                title = "5 Tarama",
                description = "5 tarama gerçekleştirdiniz",
                earnedDate = System.currentTimeMillis() - 15 * 24 * 60 * 60 * 1000
            ),
            Badge(
                id = "badge3",
                title = "Düzenli Kullanıcı",
                description = "Uygulamayı 30 gün boyunca düzenli olarak kullandınız",
                earnedDate = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000
            ),
            Badge(
                id = "badge4",
                title = "Doktor Paylaşımı",
                description = "İlk kez bir raporu doktor ile paylaştınız",
                earnedDate = System.currentTimeMillis() - 10 * 24 * 60 * 60 * 1000
            ),
            Badge(
                id = "badge5",
                title = "Randevu Tamamlama",
                description = "İlk randevunuzu başarıyla tamamladınız",
                earnedDate = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000
            )
        )
    }
    
    val lockedBadges = remember {
        listOf(
            Badge(
                id = "badge6",
                title = "10 Tarama",
                description = "10 tarama gerçekleştirin",
                earnedDate = 0
            ),
            Badge(
                id = "badge7",
                title = "Uzman Kullanıcı",
                description = "Uygulamayı 90 gün boyunca düzenli olarak kullanın",
                earnedDate = 0
            ),
            Badge(
                id = "badge8",
                title = "Sağlık Elçisi",
                description = "5 farklı doktor ile rapor paylaşın",
                earnedDate = 0
            )
        )
    }
    
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }
    
    if (selectedBadge != null) {
        AlertDialog(
            onDismissRequest = { selectedBadge = null },
            title = { Text(selectedBadge!!.title) },
            text = {
                Column {
                    Text(selectedBadge!!.description)
                    if (selectedBadge!!.earnedDate > 0) {
                        Text(
                            text = "Kazanıldı: ${
                                SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                                    .format(Date(selectedBadge!!.earnedDate))
                            }",
                            modifier = Modifier.padding(top = 8.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedBadge = null }) {
                    Text("Tamam")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rozetlerim") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Points and Progress
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
                    Text(
                        text = "Toplam Puanınız",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    Text(
                        text = "120",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    
                    LinearProgressIndicator(
                        progress = { 0.6f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    )
                    
                    Text(
                        text = "Bir sonraki seviyeye 80 puan kaldı",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            // Earned Badges
            Text(
                text = "Kazanılan Rozetler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(badges) { badge ->
                    BadgeItem(
                        badge = badge,
                        isLocked = false,
                        onClick = { selectedBadge = badge }
                    )
                }
            }
            
            // Locked Badges
            Text(
                text = "Kilitli Rozetler",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(lockedBadges) { badge ->
                    BadgeItem(
                        badge = badge,
                        isLocked = true,
                        onClick = { selectedBadge = badge }
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: Badge,
    isLocked: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Badge Icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLocked)
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        else
                            MaterialTheme.colorScheme.primary
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isLocked) R.drawable.ic_lock else R.drawable.ic_badge
                    ),
                    contentDescription = null,
                    tint = if (isLocked)
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    else
                        Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Badge Title
            Text(
                text = badge.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isLocked)
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }
    }
}
