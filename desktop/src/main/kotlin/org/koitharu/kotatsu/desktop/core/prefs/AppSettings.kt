package org.koitharu.kotatsu.desktop.core.prefs

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.io.File

/**
 * Application settings interface
 */
interface AppSettings {
    var theme: Theme
    var downloadDirectory: String
    var readerMode: ReaderMode
    
    fun save()
    fun load()
}

enum class Theme {
    LIGHT, DARK, SYSTEM
}

enum class ReaderMode {
    STANDARD, WEBTOON
}

@Serializable
data class SettingsData(
    val theme: String = Theme.SYSTEM.name,
    val downloadDirectory: String = "",
    val readerMode: String = ReaderMode.STANDARD.name
)

class AppSettingsImpl(private val settingsFile: File) : AppSettings {
    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    
    private var data: SettingsData = SettingsData()
    
    init {
        load()
    }
    
    override var theme: Theme
        get() = Theme.valueOf(data.theme)
        set(value) {
            data = data.copy(theme = value.name)
            save()
        }
    
    override var downloadDirectory: String
        get() = data.downloadDirectory.ifEmpty { 
            "${System.getProperty("user.home")}/Downloads/Kotatsu"
        }
        set(value) {
            data = data.copy(downloadDirectory = value)
            save()
        }
    
    override var readerMode: ReaderMode
        get() = ReaderMode.valueOf(data.readerMode)
        set(value) {
            data = data.copy(readerMode = value.name)
            save()
        }
    
    override fun save() {
        try {
            settingsFile.writeText(json.encodeToString(data))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    override fun load() {
        try {
            if (settingsFile.exists()) {
                data = json.decodeFromString<SettingsData>(settingsFile.readText())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            data = SettingsData()
        }
    }
}
