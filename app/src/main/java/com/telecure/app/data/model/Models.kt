package com.telecure.app.data.model

import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class UserProfile(
    var uid: String = "",
    var fullName: String = "",
    var email: String = "",
    var phone: String = "",
    var avatarUrl: String = "",
    var idNumber: String = "",
    var isDoctor: Boolean = false,
    var biometricEnabled: Boolean = false,
    var language: String = "English",
    var fcmToken: String = "",
    var emergencyContactPhone: String = "",
    @ServerTimestamp var createdAt: Date? = null
)

data class Dependent(
    var id: String = "",
    var name: String = "",
    var relationship: String = "",
    var dateOfBirth: String = ""
)

data class Address(
    var id: String = "",
    var label: String = "Home",
    var line1: String = "",
    var city: String = "",
    var postcode: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var isDefault: Boolean = false
)

data class Doctor(
    var uid: String = "",
    var name: String = "",
    var specialty: String = "",
    var photoUrl: String = "",
    var rating: Double = 0.0,
    var reviewCount: Int = 0,
    var experienceYears: Int = 0,
    var languages: List<String> = emptyList(),
    var nationality: String = "",
    var about: String = "",
    var education: String = "",
    var clinicAffiliation: String = "",
    var clinicLat: Double = 0.0,
    var clinicLng: Double = 0.0,
    var consultationFee: Double = 0.0,
    var gender: String = "",
    var nextAvailableSlot: String = "",
    var verified: Boolean = true
)

data class TimeSlot(
    var id: String = "",
    var date: String = "",
    var time: String = "",
    var isBooked: Boolean = false
)

enum class AppointmentStatus { UPCOMING, COMPLETED, CANCELLED }
enum class ConsultationType { VIDEO, CHAT, IN_CLINIC }

data class Appointment(
    var id: String = "",
    var patientUid: String = "",
    var doctorUid: String = "",
    var doctorName: String = "",
    var doctorPhotoUrl: String = "",
    var specialty: String = "",
    var date: String = "",
    var time: String = "",
    var type: String = ConsultationType.VIDEO.name,
    var reasonForVisit: String = "",
    var reportUrls: List<String> = emptyList(),
    var fee: Double = 0.0,
    var status: String = AppointmentStatus.UPCOMING.name,
    var confirmationNumber: String = "",
    var bookedForName: String = "", // blank = booked for self; otherwise a dependent's name
    @ServerTimestamp var createdAt: Date? = null
)

data class Medicine(
    var id: String = "",
    var name: String = "",
    var strength: String = "",
    var category: String = "",
    var manufacturer: String = "",
    var description: String = "",
    var dosage: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var prescriptionRequired: Boolean = false,
    var stock: Int = 100
)

data class CartItem(
    var medicineId: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var imageUrl: String = "",
    var quantity: Int = 1,
    var prescriptionRequired: Boolean = false
)

enum class OrderStatus { CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED }

data class PharmacyOrder(
    var id: String = "",
    var patientUid: String = "",
    var items: List<CartItem> = emptyList(),
    var deliveryAddress: Address = Address(),
    var deliveryOption: String = "Standard",
    var prescriptionUrl: String = "",
    var total: Double = 0.0,
    var status: String = OrderStatus.CONFIRMED.name,
    var driverLat: Double = 0.0,
    var driverLng: Double = 0.0,
    var destinationLat: Double = 0.0,
    var destinationLng: Double = 0.0,
    @ServerTimestamp var createdAt: Date? = null
)

data class Prescription(
    var id: String = "",
    var patientUid: String = "",
    var doctorName: String = "",
    var diagnosis: String = "",
    var medicines: List<String> = emptyList(),
    var pdfUrl: String = "",
    var type: String = "Prescription", // Prescription | e-MC | Lab Report | Referral
    @ServerTimestamp var issuedAt: Date? = null
)

data class MedicalRecord(
    var id: String = "",
    var patientUid: String = "",
    var title: String = "",
    var category: String = "Lab", // Lab | Imaging | Vaccination | Other
    var fileUrl: String = "",
    var sharedWithDoctorUid: String = "",
    @ServerTimestamp var uploadedAt: Date? = null
)

data class ChatThread(
    var id: String = "",
    var patientUid: String = "",
    var doctorUid: String = "",
    var doctorName: String = "",
    var doctorPhotoUrl: String = "",
    var lastMessage: String = "",
    @ServerTimestamp var lastMessageAt: Date? = null
)

data class ChatMessage(
    var id: String = "",
    var senderUid: String = "",
    var text: String = "",
    var imageUrl: String = "",
    var documentUrl: String = "",
    @ServerTimestamp var sentAt: Date? = null
)

data class HealthTip(
    var id: String = "",
    var title: String = "",
    var category: String = "",
    var excerpt: String = "",
    var content: String = "",
    var imageUrl: String = "",
    var videoUrl: String = "",
    @ServerTimestamp var publishedAt: Date? = null
)

data class WaitlistEntry(
    var id: String = "",
    var patientUid: String = "",
    var doctorUid: String = "",
    var doctorName: String = "",
    var preferredDate: String = "",
    var notified: Boolean = false,
    @ServerTimestamp var requestedAt: Date? = null
)

data class LoyaltyEntry(
    var id: String = "",
    var patientUid: String = "",
    var points: Int = 0,
    var reason: String = "", // e.g. "Completed consultation", "Pharmacy order"
    @ServerTimestamp var earnedAt: Date? = null
)

enum class NotificationType { APPOINTMENT, PRESCRIPTION, ORDER, HEALTH_TIP, SYSTEM }

data class AppNotification(
    var id: String = "",
    var userUid: String = "",
    var title: String = "",
    var body: String = "",
    var type: String = NotificationType.SYSTEM.name,
    var isRead: Boolean = false,
    @ServerTimestamp var createdAt: Date? = null
)

data class PaymentMethod(
    var id: String = "",
    var type: String = "Card", // Card | FPX | e-Wallet
    var label: String = "",
    var isDefault: Boolean = false
)

data class DoctorReview(
    var id: String = "",
    var doctorUid: String = "",
    var patientUid: String = "",
    var patientName: String = "",
    var appointmentId: String = "",
    var rating: Int = 5,
    var comment: String = "",
    @ServerTimestamp var createdAt: Date? = null
)

data class MedicationReminder(
    var id: String = "",
    var patientUid: String = "",
    var medicineName: String = "",
    var dosage: String = "",
    var times: List<String> = emptyList(), // e.g. ["08:00", "20:00"], 24h format
    var active: Boolean = true,
    @ServerTimestamp var createdAt: Date? = null
)

data class MedicalAid(
    var patientUid: String = "",
    var provider: String = "", // Discovery, Momentum, Bonitas, GEMS, etc.
    var memberNumber: String = "",
    var planName: String = "",
    var mainMemberName: String = "",
    var cardImageUrl: String = "",
    @ServerTimestamp var updatedAt: Date? = null
)

enum class LabTestStatus { REQUESTED, SAMPLE_COLLECTED, PROCESSING, RESULTS_READY }

data class LabTest(
    var id: String = "",
    var patientUid: String = "",
    var testName: String = "", // e.g. "Full Blood Count", "HIV Test", "COVID-19 PCR"
    var status: String = LabTestStatus.REQUESTED.name,
    var resultUrl: String = "",
    var resultSummary: String = "",
    var requestedByDoctorName: String = "",
    var price: Double = 0.0,
    @ServerTimestamp var createdAt: Date? = null
)

enum class MetricType { WEIGHT, BLOOD_PRESSURE, BLOOD_SUGAR, MOOD }

data class HealthMetricEntry(
    var id: String = "",
    var patientUid: String = "",
    var type: String = MetricType.WEIGHT.name,
    var value: String = "", // "72.5" for weight/sugar, "120/80" for BP, "Good"/"Okay"/"Low" for mood
    var note: String = "",
    @ServerTimestamp var recordedAt: Date? = null
)

enum class RefillStatus { PENDING, APPROVED, DECLINED }

data class RefillRequest(
    var id: String = "",
    var patientUid: String = "",
    var doctorUid: String = "",
    var doctorName: String = "",
    var medicineName: String = "",
    var status: String = RefillStatus.PENDING.name,
    var doctorNote: String = "",
    @ServerTimestamp var requestedAt: Date? = null
)
