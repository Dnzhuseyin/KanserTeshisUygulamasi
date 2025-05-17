package com.example.fonksiyonel.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val userType: UserType = UserType.PATIENT,
    val profilePhotoUrl: String? = null,
    val points: Int = 0,
    val badges: List<Badge> = emptyList(),
    val lastAnalysisDate: Long? = null
)

enum class UserType {
    PATIENT, DOCTOR
}

data class Badge(
    val id: String,
    val title: String,
    val description: String,
    val earnedDate: Long
)
