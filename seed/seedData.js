/**
 * TeleCure Firestore seed script.
 *
 * Usage:
 *   1. Place your Firebase service account key JSON next to this file as
 *      serviceAccountKey.json (never commit this file).
 *   2. cd seed && npm install
 *   3. node seedData.js
 *
 * Seeds: doctors, medicines, health_tips collections with sample data
 * so the app has content to browse immediately after setup.
 */
const admin = require('firebase-admin');
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
db.settings({ preferRest: true });

const doctors = [
  {
    uid: 'doc_001', name: 'Dr. Amelia Botha', specialty: 'General Physician',
    photoUrl: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=400&q=80', rating: 4.8, reviewCount: 212,
    experienceYears: 9, languages: ['English', 'Afrikaans'], nationality: 'South African',
    about: 'Focused on preventive care and everyday family health.',
    education: 'MBChB, University of Cape Town', clinicAffiliation: 'TeleCure Family Clinic, Cape Town',
    clinicLat: -33.9249, clinicLng: 18.4241, consultationFee: 350, gender: 'Female',
    nextAvailableSlot: 'Today 2:00 PM', verified: true
  },
  {
    uid: 'doc_002', name: 'Dr. Raj Naidoo', specialty: 'Cardiologist',
    photoUrl: 'https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400&q=80', rating: 4.9, reviewCount: 340,
    experienceYears: 15, languages: ['English', 'Zulu', 'Hindi'], nationality: 'South African',
    about: 'Specialist in interventional cardiology and heart health screening.',
    education: 'MD Cardiology, University of the Witwatersrand', clinicAffiliation: 'TeleCure Heart Centre, Sandton',
    clinicLat: -26.1076, clinicLng: 28.0567, consultationFee: 750, gender: 'Male',
    nextAvailableSlot: 'Tomorrow 9:00 AM', verified: true
  },
  {
    uid: 'doc_003', name: 'Dr. Thandeka Mokoena', specialty: 'Dermatologist',
    photoUrl: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&q=80', rating: 4.7, reviewCount: 158,
    experienceYears: 7, languages: ['English', 'Zulu', 'Sotho'], nationality: 'South African',
    about: 'Treats acne, eczema and general skin health concerns.',
    education: 'MD Dermatology, University of Pretoria', clinicAffiliation: 'TeleCure Skin Clinic, Pretoria',
    clinicLat: -25.7479, clinicLng: 28.2293, consultationFee: 500, gender: 'Female',
    nextAvailableSlot: 'Today 4:30 PM', verified: true
  },
  {
    uid: 'doc_004', name: 'Dr. Marcus van der Merwe', specialty: 'Pediatrician',
    photoUrl: 'https://images.unsplash.com/photo-1622902046580-2b47f47f5471?w=400&q=80', rating: 4.9, reviewCount: 275,
    experienceYears: 12, languages: ['English', 'Afrikaans'], nationality: 'South African',
    about: 'Caring for infants, children and adolescent health.',
    education: 'MBChB, Stellenbosch University', clinicAffiliation: 'TeleCure Kids Clinic, Durban',
    clinicLat: -29.8587, clinicLng: 31.0218, consultationFee: 450, gender: 'Male',
    nextAvailableSlot: 'Tomorrow 11:00 AM', verified: true
  },
  {
    uid: 'doc_005', name: 'Dr. Fatima Adams', specialty: 'Psychiatrist',
    photoUrl: 'https://images.unsplash.com/photo-1584367369853-8b966cf0f7db?w=400&q=80', rating: 4.8, reviewCount: 190,
    experienceYears: 10, languages: ['English', 'Afrikaans', 'Xhosa'], nationality: 'South African',
    about: 'Supports anxiety, depression and stress management.',
    education: 'MD Psychiatry, University of Johannesburg', clinicAffiliation: 'TeleCure Mind Wellness, Johannesburg',
    clinicLat: -26.2041, clinicLng: 28.0473, consultationFee: 650, gender: 'Female',
    nextAvailableSlot: 'Today 6:00 PM', verified: true
  }
];

const medicines = [
  { id: 'med_001', name: 'Panado 500mg', strength: '500mg, 20 tablets', category: 'Pain Relief', manufacturer: 'Adcock Ingram', description: 'Relieves mild to moderate pain and fever.', dosage: '1-2 tablets every 4-6 hours as needed.', price: 39.90, imageUrl: 'https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=400', prescriptionRequired: false, stock: 500 },
  { id: 'med_002', name: 'Vitamin C 1000mg', strength: '1000mg, 30 tablets', category: 'Vitamins', manufacturer: 'Vitaforce', description: 'Supports immune system function.', dosage: '1 tablet daily.', price: 89.90, imageUrl: 'https://images.unsplash.com/photo-1550572017-edd951b55104?w=400', prescriptionRequired: false, stock: 300 },
  { id: 'med_003', name: 'Glucophage (Metformin) 500mg', strength: '500mg, 60 tablets', category: 'Chronic', manufacturer: 'Merck', description: 'Manages blood sugar in type 2 diabetes.', dosage: 'As prescribed by your doctor.', price: 129.90, imageUrl: 'https://images.unsplash.com/photo-1631549916768-4119b2e5f926?w=400', prescriptionRequired: true, stock: 150 },
  { id: 'med_004', name: 'Allergex (Cetirizine) 10mg', strength: '10mg, 10 tablets', category: 'Cold & Flu', manufacturer: 'Adcock Ingram', description: 'Relieves allergy symptoms and hay fever.', dosage: '1 tablet daily.', price: 54.90, imageUrl: 'https://images.unsplash.com/photo-1471864190281-a93a3070b6de?w=400', prescriptionRequired: false, stock: 400 },
  { id: 'med_005', name: 'Hydrocortisone Cream 1%', strength: '15g tube', category: 'Skin Care', manufacturer: 'Dis-Chem', description: 'Reduces itching and inflammation from skin irritation.', dosage: 'Apply thinly 1-2 times daily.', price: 69.90, imageUrl: 'https://images.unsplash.com/photo-1585435557343-3b092031a831?w=400', prescriptionRequired: false, stock: 200 },
  { id: 'med_006', name: 'Amlodipine 5mg', strength: '5mg, 30 tablets', category: 'Chronic', manufacturer: 'Cipla', description: 'Manages high blood pressure.', dosage: 'As prescribed by your doctor.', price: 99.90, imageUrl: 'https://images.unsplash.com/photo-1631549916768-4119b2e5f926?w=400', prescriptionRequired: true, stock: 180 }
];

const healthTips = [
  { id: 'tip_001', title: '5 Habits for a Healthier Heart', category: 'Chronic Care', excerpt: 'Small daily changes that add up to big cardiovascular benefits.', content: 'Regular movement, balanced meals, quality sleep, stress management, and routine checkups all support long-term heart health.', imageUrl: 'https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=600' },
  { id: 'tip_002', title: 'Eating Well on a Busy Schedule', category: 'Nutrition', excerpt: 'Practical tips for balanced meals when time is short.', content: 'Batch-cook staples, keep healthy snacks on hand, and prioritize protein and fiber at each meal.', imageUrl: 'https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=600' },
  { id: 'tip_003', title: 'Managing Stress in Daily Life', category: 'Mental Health', excerpt: 'Simple techniques to stay grounded under pressure.', content: 'Breathing exercises, short walks, and setting boundaries around work can meaningfully reduce daily stress.', imageUrl: 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=600' },
  { id: 'tip_004', title: 'Home Workouts That Actually Work', category: 'Fitness', excerpt: 'No gym, no problem — effective routines for any space.', content: 'Bodyweight circuits combining squats, push-ups, and core work can build real strength in 20-30 minutes.', imageUrl: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=600' },
  { id: 'tip_005', title: 'Newborn Sleep Basics', category: 'Parenting', excerpt: 'What to expect in the first few months.', content: 'Newborns sleep in short cycles; establishing gentle routines early can help both baby and parents rest better over time.', imageUrl: 'https://images.unsplash.com/photo-1519689680058-324335c77eba?w=600' }
];

async function seed() {
  const batch = db.batch();
  doctors.forEach(d => batch.set(db.collection('doctors').doc(d.uid), d));
  medicines.forEach(m => batch.set(db.collection('medicines').doc(m.id), m));
  healthTips.forEach(t => batch.set(db.collection('health_tips').doc(t.id), { ...t, publishedAt: admin.firestore.FieldValue.serverTimestamp() }));
  await batch.commit();
  console.log(`Seeded ${doctors.length} doctors, ${medicines.length} medicines, ${healthTips.length} health tips.`);
}

seed().then(() => process.exit(0)).catch(err => { console.error(err); process.exit(1); });
