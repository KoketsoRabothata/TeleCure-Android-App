package com.telecure.app.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.telecure.app.ui.theme.TealPrimary
import kotlinx.coroutines.launch

private data class OnboardSlide(val imageUrl: String, val title: String, val subtitle: String)

// Swap these URLs for your own hosted assets any time — they're just placeholder stock photos.
private val slides = listOf(
    OnboardSlide(
        "https://images.unsplash.com/photo-1651008376811-b90baee60c1f?w=1000&q=80",
        "Consult doctors from home",
        "Speak face-to-face with licensed doctors over secure video, wherever you are."
    ),
    OnboardSlide(
        "https://images.unsplash.com/photo-1587854692152-cbe660dbde88?w=1000&q=80",
        "Genuine medicines delivered fast",
        "Order pharmacy essentials and track your delivery in real time."
    ),
    OnboardSlide(
        "https://images.unsplash.com/photo-1631815588090-d4bfec5b1ccb?w=1000&q=80",
        "Instant digital prescriptions & MCs",
        "Get e-prescriptions and medical certificates right after your visit."
    ),
    OnboardSlide(
        "https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=1000&q=80",
        "Your health records, always with you",
        "Securely store and share your records with any doctor, any time."
    )
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize().background(Color.White)) {
        Box(Modifier.fillMaxWidth().weight(1.1f)) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                val slide = slides[page]
                Box(Modifier.fillMaxSize()) {
                    AsyncImage(
                        model = slide.imageUrl,
                        contentDescription = slide.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    )
                    Box(
                        Modifier.fillMaxSize().clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Black.copy(alpha = 0.05f), Color.Black.copy(alpha = 0.35f)),
                                startY = 0f
                            )
                        )
                    )
                }
            }
            TextButton(
                onClick = onFinish,
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 44.dp, end = 16.dp),
                colors = ButtonDefaults.textButtonColors(containerColor = Color.Black.copy(alpha = 0.25f), contentColor = Color.White)
            ) { Text("Skip") }
        }

        Column(
            Modifier.fillMaxWidth().weight(1f).padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val slide = slides[pagerState.currentPage]
            Text(slide.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Spacer(Modifier.height(10.dp))
            Text(slide.subtitle, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, color = Color.Gray)

            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(slides.size) { i ->
                    Box(
                        Modifier
                            .padding(4.dp)
                            .size(if (pagerState.currentPage == i) 22.dp else 8.dp, 8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (pagerState.currentPage == i) TealPrimary else Color(0xFFD0E5E2))
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = {
                    if (pagerState.currentPage == slides.lastIndex) onFinish()
                    else scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text(if (pagerState.currentPage == slides.lastIndex) "Get Started" else "Next")
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
