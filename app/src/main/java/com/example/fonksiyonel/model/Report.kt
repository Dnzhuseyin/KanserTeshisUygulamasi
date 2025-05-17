package com.example.fonksiyonel.model

import java.util.Date

data class Report(
    val id: String = "",
    val userId: String = "",
    val imageUrl: String = "",
    val diagnosisResult: DiagnosisResult? = null,
    val createdAt: Long = Date().time,
    val sharedWithDoctors: List<String> = emptyList(),
    val doctorFeedback: String? = null
)

data class DiagnosisResult(
    val cancerType: CancerType,
    val confidencePercentage: Float,
    val riskLevel: RiskLevel
)

enum class CancerType {
    MELANOMA,
    BASAL_CELL_CARCINOMA,
    SQUAMOUS_CELL_CARCINOMA,
    BENIGN,
    UNKNOWN
}

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH,
    VERY_HIGH
}
