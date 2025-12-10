package dev.dongeeo.timerfitpro.domain.model

data class DailySummary(
    val date: String,
    val sessionCount: Int,
    val totalDurationMillis: Long
)


