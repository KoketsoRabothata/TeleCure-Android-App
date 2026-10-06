# TeleCure — Android Studio Project

A fully functional Jetpack Compose + Firebase telemedicine app implementing all 23 screens
from the TeleCure wireframe spec: splash, onboarding, auth, home, doctor discovery/booking,
payments, video consultation, chat, pharmacy + cart + Google-Maps order tracking,
prescriptions/e-MC, health records, health tips, notifications, profile, and settings.

## Tech stack
- Kotlin + Jetpack Compose (Material 3), single-Activity + Navigation Compose
- Firebase Auth, Firestore, Storage, Cloud Messaging
- Google Maps SDK for Android + Maps Compose (live order tracking with rider/destination
  markers and route line) — see `OrderTrackingScreen.kt`
- Coil for image loading

## 1. Firebase setup
1. Create a Firebase project at https://console.firebase.google.com.
2. Add an Android app with package name `com.telecure.app`.
3. Download the real `google-services.json` and place it at `app/google-services.json`
   (a `google-services.json.SAMPLE` is included here showing the expected shape — delete
   `.SAMPLE` once you drop in your real file).
4. Enable **Authentication → Sign-in method → Email/Password**.
5. Enable **Authentication → Sign-in method → Google** (needed for the "Continue with
   Google" button on the login screen — see step 3 below for the client ID it needs).
6. Enable **Firestore Database** (production mode) and deploy `firestore.rules`
   (Firebase console → Firestore → Rules, or via the Firebase CLI: `firebase deploy --only firestore:rules`).
7. Enable **Storage** (used for medical record uploads and profile photos) and deploy
   `storage.rules` the same way, via the Storage → Rules tab.
8. (Optional) Enable **Cloud Messaging** for push notifications.

## 2. Google Maps setup (used on the Order Tracking screen)
1. In Google Cloud Console, enable **Maps SDK for Android** for the same project.
2. Create an API key restricted to Android apps (package `com.telecure.app` + your SHA-1).
3. Open `app/build.gradle` and replace `YOUR_GOOGLE_MAPS_API_KEY_HERE` in
   `manifestPlaceholders` with your key.

## 3. Google Sign-In setup (used on the login screen)
1. After enabling the Google provider in step 5 above, Firebase auto-generates a **Web
   client ID** — find it under Authentication → Sign-in method → Google → Web SDK
   configuration.
2. Open `app/src/main/res/values/strings.xml` and replace
   `YOUR_WEB_CLIENT_ID.apps.googleusercontent.com` in the `google_web_client_id` string
   with that value.
3. Make sure your debug SHA-1 (and later, your release SHA-1) is registered under
   Project Settings → Your apps → the Android app, or Google Sign-In will fail silently.

## 4. Seed sample data (doctors, medicines, health tips)
```
cd seed
npm install
# Place a Firebase service-account JSON (Project Settings → Service Accounts →
# Generate new private key) at seed/serviceAccountKey.json
node seedData.js
```

## 5. Open & run
1. Open the `TeleCureApp` folder in Android Studio (Koala or newer recommended).
2. Let Gradle sync — it will fetch Compose, Firebase, and Maps dependencies.
3. Run on an emulator with Google Play services, or a physical device.
4. Sign up for a new account on first launch (Onboarding → Create Account), then explore:
   Home → Doctors → Book Appointment → Payment → My Appointments → Video Consultation;
   Pharmacy → Product → Cart → Order Tracking (map).

## Project structure
```
app/src/main/java/com/telecure/app/
  data/model/        Firestore data classes
  data/repository/   Auth, Doctor, Appointment, Pharmacy/Order, Records,
                      Prescription, Chat, Notification, HealthTip repositories
  data/service/       FCM messaging service
  navigation/         Screen routes + NavHost wiring all 23 screens
  ui/theme/           Teal/blue brand colors, typography, Material 3 theme
  ui/components/      Shared bottom nav, empty/error/success states, top bar
  ui/viewmodel/        One ViewModel per feature area, backed by Firestore
  ui/screens/<area>/   Splash, Onboarding, Auth, Home, Doctors, Booking,
                        Appointments, Video, Chat, Pharmacy, Orders, Records,
                        Health, Notifications, Profile, Settings
firestore.rules       Per-collection security rules (owner-scoped reads/writes)
seed/                 Node script + sample data for doctors/medicines/health tips
```

## Notes
- Biometric login, in-call video streaming (WebRTC), push-notification tray display, and
  document sharing during chat are wired as UI + hooks with inline comments marking where
  to plug in `BiometricPrompt`, a WebRTC/Agora SDK, and a file picker + Storage upload,
  since those require platform SDKs/keys specific to your deployment.
- All other flows (auth, booking, payments record, appointments, pharmacy ordering, live
  order tracking on Google Maps, prescriptions, records upload, notifications, profile)
  read and write real Firestore data end-to-end.
