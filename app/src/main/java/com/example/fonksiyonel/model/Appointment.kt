package com.example.fonksiyonel.model

data class Appointment(
    val id: String = "",
    val patientId: String = "",
    val doctorId: String = "",
    val date: Long = 0,
    val description: String = "",
    val status: AppointmentStatus = AppointmentStatus.PENDING
)

enum class AppointmentStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}
