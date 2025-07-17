package com.example.travelapp_kmp.network


import io.ktor.client.*

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object HttpClientProvider {
    // Ktor multiplatform can pick proper engine in expect/actual fashion; the
    // sample keeps it simple by selecting platform-specific engines in each
    // source-set if needed.
    val client = HttpClient {          // ← no explicit engine!
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            logger = Logger.SIMPLE   // ← prints to StdOut / Logcat
            level  = LogLevel.ALL    // headers + body
        }

    }
}
