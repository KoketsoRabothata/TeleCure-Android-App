package com.telecure.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.telecure.app.navigation.TeleCureNavGraph
import com.telecure.app.ui.theme.TeleCureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeleCureTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TeleCureNavGraph()
                }
            }
        }
    }
}
