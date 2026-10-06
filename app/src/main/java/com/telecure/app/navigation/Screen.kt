package com.telecure.app.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object DoctorList : Screen("doctors")
    object DoctorDetail : Screen("doctor_detail/{doctorId}") {
        fun build(id: String) = "doctor_detail/$id"
    }
    object BookAppointment : Screen("book_appointment/{doctorId}") {
        fun build(id: String) = "book_appointment/$id"
    }
    object Payment : Screen("payment/{doctorId}/{date}/{time}/{type}/{reason}/{bookedFor}") {
        fun build(id: String, date: String, time: String, type: String, reason: String, bookedFor: String = "-") =
            "payment/$id/$date/$time/$type/${reason.ifBlank { "-" }}/${bookedFor.ifBlank { "-" }}"
    }
    object BookingSuccess : Screen("booking_success/{confirmation}/{specialty}") {
        fun build(confirmation: String, specialty: String = "-") = "booking_success/$confirmation/${specialty.ifBlank { "-" }}"
    }
    object MyAppointments : Screen("my_appointments")
    object VideoConsultation : Screen("video_consultation/{appointmentId}") {
        fun build(id: String) = "video_consultation/$id"
    }
    object ChatList : Screen("chat_list")
    object ChatDetail : Screen("chat_detail/{chatId}/{doctorName}") {
        fun build(id: String, name: String) = "chat_detail/$id/$name"
    }
    object Pharmacy : Screen("pharmacy")
    object ProductDetail : Screen("product_detail/{medicineId}") {
        fun build(id: String) = "product_detail/$id"
    }
    object Cart : Screen("cart")
    object MyOrders : Screen("my_orders")
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun build(id: String) = "order_tracking/$id"
    }
    object Prescriptions : Screen("prescriptions")
    object HealthRecords : Screen("health_records")
    object HealthTips : Screen("health_tips")
    object ContentDetail : Screen("content_detail/{tipId}") {
        fun build(id: String) = "content_detail/$id"
    }
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object Dependents : Screen("dependents")
    object Reminders : Screen("reminders")
    object SymptomChecker : Screen("symptom_checker")
    object MedicalAid : Screen("medical_aid")
    object LabTests : Screen("lab_tests")
    object HealthMetrics : Screen("health_metrics")
    object RefillRequests : Screen("refill_requests")
    object Loyalty : Screen("loyalty")
    object Waitlist : Screen("waitlist")
    object MoodJournal : Screen("mood_journal")
    object PharmacistChat : Screen("pharmacist_chat")
    object SupportChat : Screen("support_chat")
    object Addresses : Screen("addresses")
    object PaymentMethods : Screen("payment_methods")
    object HelpFaq : Screen("help_faq")
    object PrivacyTerms : Screen("privacy_terms")
}
