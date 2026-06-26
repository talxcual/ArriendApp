package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.data.LuxeRentalDatabase
import com.example.ui.LuxeRentalApp
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-edge system styling
        enableEdgeToEdge()

        // Fetch Room Database DAO for reactive architecture flows
        val database = LuxeRentalDatabase.getDatabase(this, lifecycleScope)
        val dao = database.dao()

        setContent {
            MyApplicationTheme {
                LuxeRentalApp(dao = dao)
            }
        }
    }
}
