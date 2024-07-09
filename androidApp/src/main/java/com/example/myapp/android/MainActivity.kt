package com.example.myapp.android

import android.bluetooth.le.ScanSettings
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapp.Greeting
import com.juul.kable.Scanner

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    GreetingView(Greeting().greet())
                }
            }
        }
    }
}

val scanner = Scanner {
    scanSettings = ScanSettings.Builder()
        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .build()
}

@Composable
fun GreetingView(texts: List<String>) {
    texts?.forEach { it -> Text(text = it + "\n")}
    println(scanner)
}

@Preview
@Composable
fun DefaultPreview() {
    println(scanner)
    MaterialTheme {
        val greeting = remember { Greeting().greet() }

        Column(
            modifier = Modifier.padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            greeting.forEach { greeting ->
                Text(greeting)
                Divider()
            }
        }
    }
}
