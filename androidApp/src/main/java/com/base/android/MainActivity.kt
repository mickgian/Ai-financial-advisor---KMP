package com.base.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.base.shared.auth.AuthModule
import com.base.shared.utils.Logger
import com.base.shared.view.AppViewAndroid
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize the authentication module with Android context
        Logger.info("MainActivity: Initializing AuthModule", "BaseAppInit")
        try {
            AuthModule.initialize(this)
            Logger.info("MainActivity: AuthModule initialized successfully", "BaseAppInit")
            
            // Start the AuthManager in a coroutine
            lifecycleScope.launch {
                try {
                    AuthModule.startAuthManager()
                    Logger.info("MainActivity: AuthManager started successfully", "BaseAppInit")
                } catch (e: Exception) {
                    Logger.error("MainActivity: Failed to start AuthManager", "BaseAppInit", e)
                }
            }
        } catch (e: Exception) {
            Logger.error("MainActivity: Failed to initialize AuthModule", "BaseAppInit", e)
        }
        
        setContent {
            enableEdgeToEdge()
            AppViewAndroid()
        }
    }
}
