package com.telecure.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.getValue
import com.telecure.app.ui.components.TeleCureBottomBar
import com.telecure.app.ui.screens.appointments.MyAppointmentsScreen
import com.telecure.app.ui.screens.auth.LoginScreen
import com.telecure.app.ui.screens.auth.SignUpScreen
import com.telecure.app.ui.screens.booking.BookAppointmentScreen
import com.telecure.app.ui.screens.booking.BookingSuccessScreen
import com.telecure.app.ui.screens.booking.PaymentScreen
import com.telecure.app.ui.screens.chat.ChatDetailScreen
import com.telecure.app.ui.screens.chat.ChatListScreen
import com.telecure.app.ui.screens.content.ContentDetailScreen
import com.telecure.app.ui.screens.dependents.DependentsScreen
import com.telecure.app.ui.screens.doctors.DoctorDetailScreen
import com.telecure.app.ui.screens.doctors.DoctorListScreen
import com.telecure.app.ui.screens.health.HealthTipsScreen
import com.telecure.app.ui.screens.healthmetrics.HealthMetricsScreen
import com.telecure.app.ui.screens.home.HomeScreen
import com.telecure.app.ui.screens.labtests.LabTestsScreen
import com.telecure.app.ui.screens.loyalty.LoyaltyScreen
import com.telecure.app.ui.screens.medicalaid.MedicalAidScreen
import com.telecure.app.ui.screens.moodjournal.MoodJournalScreen
import com.telecure.app.ui.screens.notifications.NotificationsScreen
import com.telecure.app.ui.screens.onboarding.OnboardingScreen
import com.telecure.app.ui.screens.orders.OrderTrackingScreen
import com.telecure.app.ui.screens.orders.OrdersListScreen
import com.telecure.app.ui.screens.addresses.AddressesScreen
import com.telecure.app.ui.screens.legal.PrivacyTermsScreen
import com.telecure.app.ui.screens.paymentmethods.PaymentMethodsScreen
import com.telecure.app.ui.screens.pharmacistchat.PharmacistChatScreen
import com.telecure.app.ui.screens.support.HelpFaqScreen
import com.telecure.app.ui.screens.supportchat.SupportChatScreen
import com.telecure.app.ui.screens.pharmacy.CartCheckoutScreen
import com.telecure.app.ui.screens.pharmacy.PharmacyScreen
import com.telecure.app.ui.screens.pharmacy.ProductDetailScreen
import com.telecure.app.ui.screens.profile.ProfileScreen
import com.telecure.app.ui.screens.records.HealthRecordsScreen
import com.telecure.app.ui.screens.records.PrescriptionsScreen
import com.telecure.app.ui.screens.refills.RefillRequestsScreen
import com.telecure.app.ui.screens.reminders.RemindersScreen
import com.telecure.app.ui.screens.settings.SettingsScreen
import com.telecure.app.ui.screens.splash.SplashScreen
import com.telecure.app.ui.screens.symptomchecker.SymptomCheckerScreen
import com.telecure.app.ui.screens.video.VideoConsultationScreen
import com.telecure.app.ui.screens.waitlist.WaitlistScreen

private val mainTabRoutes = setOf(
    Screen.Home.route, Screen.DoctorList.route, Screen.Pharmacy.route, Screen.Prescriptions.route, Screen.Profile.route
)

@Composable
fun TeleCureNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in mainTabRoutes) TeleCureBottomBar(navController, currentRoute)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(bottom = if (currentRoute in mainTabRoutes) padding.calculateBottomPadding() else 0.dp)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onNavigate = { route -> navController.navigate(route) { popUpTo(Screen.Splash.route) { inclusive = true } } })
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onFinish = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } })
            }
            composable(Screen.Login.route) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                    onNavigateSignUp = { navController.navigate(Screen.SignUp.route) }
                )
            }
            composable(Screen.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = { navController.navigate(Screen.Home.route) { popUpTo(0) } },
                    onNavigateLogin = { navController.popBackStack() }
                )
            }
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.DoctorList.route) { DoctorListScreen(navController) }
            composable(
                Screen.DoctorDetail.route,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { entry ->
                DoctorDetailScreen(navController, entry.arguments?.getString("doctorId").orEmpty())
            }
            composable(
                Screen.BookAppointment.route,
                arguments = listOf(navArgument("doctorId") { type = NavType.StringType })
            ) { entry ->
                BookAppointmentScreen(navController, entry.arguments?.getString("doctorId").orEmpty())
            }
            composable(
                Screen.Payment.route,
                arguments = listOf(
                    navArgument("doctorId") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType },
                    navArgument("type") { type = NavType.StringType },
                    navArgument("reason") { type = NavType.StringType },
                    navArgument("bookedFor") { type = NavType.StringType }
                )
            ) { entry ->
                PaymentScreen(
                    navController,
                    doctorId = entry.arguments?.getString("doctorId").orEmpty(),
                    date = entry.arguments?.getString("date").orEmpty(),
                    time = entry.arguments?.getString("time").orEmpty(),
                    type = entry.arguments?.getString("type").orEmpty(),
                    reasonEncoded = entry.arguments?.getString("reason").orEmpty(),
                    bookedForEncoded = entry.arguments?.getString("bookedFor").orEmpty()
                )
            }
            composable(
                Screen.BookingSuccess.route,
                arguments = listOf(
                    navArgument("confirmation") { type = NavType.StringType },
                    navArgument("specialty") { type = NavType.StringType }
                )
            ) { entry ->
                BookingSuccessScreen(
                    navController,
                    entry.arguments?.getString("confirmation").orEmpty(),
                    entry.arguments?.getString("specialty").orEmpty()
                )
            }
            composable(Screen.MyAppointments.route) { MyAppointmentsScreen(navController) }
            composable(
                Screen.VideoConsultation.route,
                arguments = listOf(navArgument("appointmentId") { type = NavType.StringType })
            ) { entry ->
                VideoConsultationScreen(navController, entry.arguments?.getString("appointmentId").orEmpty())
            }
            composable(Screen.ChatList.route) { ChatListScreen(navController) }
            composable(
                Screen.ChatDetail.route,
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("doctorName") { type = NavType.StringType }
                )
            ) { entry ->
                ChatDetailScreen(
                    navController,
                    chatId = entry.arguments?.getString("chatId").orEmpty(),
                    doctorName = entry.arguments?.getString("doctorName").orEmpty()
                )
            }
            composable(Screen.Pharmacy.route) { PharmacyScreen(navController) }
            composable(
                Screen.ProductDetail.route,
                arguments = listOf(navArgument("medicineId") { type = NavType.StringType })
            ) { entry ->
                ProductDetailScreen(navController, entry.arguments?.getString("medicineId").orEmpty())
            }
            composable(Screen.Cart.route) { CartCheckoutScreen(navController) }
            composable(Screen.MyOrders.route) { OrdersListScreen(navController) }
            composable(
                Screen.OrderTracking.route,
                arguments = listOf(navArgument("orderId") { type = NavType.StringType })
            ) { entry ->
                OrderTrackingScreen(navController, entry.arguments?.getString("orderId").orEmpty())
            }
            composable(Screen.Prescriptions.route) { PrescriptionsScreen(navController) }
            composable(Screen.HealthRecords.route) { HealthRecordsScreen(navController) }
            composable(Screen.HealthTips.route) { HealthTipsScreen(navController) }
            composable(Screen.Notifications.route) { NotificationsScreen(navController) }
            composable(Screen.Profile.route) {
                ProfileScreen(navController, onSignedOut = {
                    navController.navigate(Screen.Login.route) { popUpTo(0) }
                })
            }
            composable(Screen.Settings.route) { SettingsScreen(navController) }
            composable(Screen.Dependents.route) { DependentsScreen(navController) }
            composable(Screen.Reminders.route) { RemindersScreen(navController) }
            composable(Screen.SymptomChecker.route) { SymptomCheckerScreen(navController) }
            composable(Screen.MedicalAid.route) { MedicalAidScreen(navController) }
            composable(Screen.LabTests.route) { LabTestsScreen(navController) }
            composable(Screen.HealthMetrics.route) { HealthMetricsScreen(navController) }
            composable(Screen.RefillRequests.route) { RefillRequestsScreen(navController) }
            composable(
                Screen.ContentDetail.route,
                arguments = listOf(navArgument("tipId") { type = NavType.StringType })
            ) { entry ->
                ContentDetailScreen(navController, entry.arguments?.getString("tipId").orEmpty())
            }
            composable(Screen.Loyalty.route) { LoyaltyScreen(navController) }
            composable(Screen.Waitlist.route) { WaitlistScreen(navController) }
            composable(Screen.MoodJournal.route) { MoodJournalScreen(navController) }
            composable(Screen.PharmacistChat.route) { PharmacistChatScreen(navController) }
            composable(Screen.SupportChat.route) { SupportChatScreen(navController) }
            composable(Screen.Addresses.route) { AddressesScreen(navController) }
            composable(Screen.PaymentMethods.route) { PaymentMethodsScreen(navController) }
            composable(Screen.HelpFaq.route) { HelpFaqScreen(navController) }
            composable(Screen.PrivacyTerms.route) { PrivacyTermsScreen(navController) }
        }
    }
}
