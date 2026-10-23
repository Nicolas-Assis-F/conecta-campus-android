package br.com.conectacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import br.com.conectacampus.ui.ConectaCampusApp
import br.com.conectacampus.ui.theme.ConectaCampusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ConectaCampusTheme {
                ConectaCampusApp()
            }
        }
    }
}

