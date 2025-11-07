package org.koitharu.kotatsu.desktop.core.di

import okhttp3.OkHttpClient
import org.koitharu.kotatsu.desktop.core.database.Database
import org.koitharu.kotatsu.desktop.core.manga.MangaRepository
import org.koitharu.kotatsu.desktop.core.network.NetworkClient
import org.koitharu.kotatsu.desktop.core.prefs.AppSettings
import org.koitharu.kotatsu.desktop.core.prefs.AppSettingsImpl
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Manual dependency injection container for desktop app
 * Replaces Hilt from Android version
 */
interface AppContainer {
    val settings: AppSettings
    val networkClient: NetworkClient
    val database: Database
    val mangaRepository: MangaRepository
    val dataDirectory: File
    
    fun cleanup()
}

class AppContainerImpl : AppContainer {
    private val homeDir = System.getProperty("user.home")
    override val dataDirectory: File = File(homeDir, ".kotatsu").apply {
        if (!exists()) mkdirs()
    }
    
    override val settings: AppSettings by lazy {
        AppSettingsImpl(File(dataDirectory, "settings.json"))
    }
    
    override val networkClient: NetworkClient by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
        
        NetworkClient(okHttpClient)
    }
    
    override val database: Database by lazy {
        Database(dataDirectory)
    }
    
    override val mangaRepository: MangaRepository by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(20, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build()
        MangaRepository(okHttpClient)
    }
    
    override fun cleanup() {
        try {
            database.close()
        } catch (e: Exception) {
            // Database might not have been initialized
        }
    }
}
