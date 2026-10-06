package com.telecure.app.ui.components

private val generalTips = listOf(
    "Bring your ID and medical aid card (if applicable)",
    "Have a list of your current medications ready",
    "Write down your symptoms and when they started"
)

private val specialtyTips: Map<String, List<String>> = mapOf(
    "General Physician" to listOf("Fast for 8 hours if blood tests might be needed") + generalTips,
    "Cardiologist" to listOf("Bring any previous ECG or heart scan results", "Avoid caffeine for 2 hours before your appointment") + generalTips,
    "Dermatologist" to listOf("Avoid makeup or lotion on the affected area on the day", "Take clear photos of the issue over time if it changes") + generalTips,
    "Pediatrician" to listOf("Bring your child's vaccination record", "Note your child's height, weight, and recent behavior changes") + generalTips,
    "Psychiatrist" to listOf("Write down how you've been feeling over the past few weeks", "List any medications or supplements you're currently taking") + generalTips,
    "Dentist" to listOf("Avoid eating right before the appointment if a procedure is planned", "Note any tooth sensitivity or pain location") + generalTips
)

fun prepTipsFor(specialty: String): List<String> = specialtyTips[specialty] ?: generalTips
