package org.koitharu.kotatsu.desktop.core.manga

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.koitharu.kotatsu.parsers.MangaLoaderContext
import org.koitharu.kotatsu.parsers.MangaParser
import org.koitharu.kotatsu.parsers.config.ConfigKey
import org.koitharu.kotatsu.parsers.config.MangaSourceConfig
import org.koitharu.kotatsu.parsers.model.*
import java.util.Locale

/**
 * Manga repository for fetching manga from real online sources
 */
class MangaRepository(
    private val okHttpClient: OkHttpClient
) {
    
    private val loaderContext = object : MangaLoaderContext() {
        override val cookieJar = okHttpClient.cookieJar
        override val httpClient = okHttpClient
        
        override fun encodeBase64(data: ByteArray): String {
            return java.util.Base64.getEncoder().encodeToString(data)
        }
        
        override fun decodeBase64(data: String): ByteArray {
            return java.util.Base64.getDecoder().decode(data)
        }
        
        override fun getPreferredLocales(): List<Locale> {
            return listOf(Locale.getDefault())
        }
        
        override fun getDefaultUserAgent(): String {
            return "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        }
        
        override fun getConfig(source: MangaSource): MangaSourceConfig {
            return object : MangaSourceConfig {
                override fun <T> get(key: ConfigKey<T>): T {
                    @Suppress("UNCHECKED_CAST")
                    return key.defaultValue as T
                }
            }
        }
        
        override suspend fun evaluateJs(script: String): String? {
            // JS evaluation not supported in desktop version
            return null
        }
        
        override suspend fun evaluateJs(baseUrl: String, script: String): String? {
            // JS evaluation not supported in desktop version
            return null
        }
        
        override fun redrawImageResponse(
            response: okhttp3.Response,
            redraw: (image: org.koitharu.kotatsu.parsers.bitmap.Bitmap) -> org.koitharu.kotatsu.parsers.bitmap.Bitmap
        ): okhttp3.Response {
            // Image redrawing not supported in desktop version
            return response
        }
        
        override fun createBitmap(width: Int, height: Int): org.koitharu.kotatsu.parsers.bitmap.Bitmap {
            throw UnsupportedOperationException("Bitmap creation not supported in desktop version")
        }
    }
    
    /**
     * Get all available manga sources
     */
    fun getAvailableSources(): List<MangaParserSource> {
        return MangaParserSource.entries
            .filterNot { it.isBroken }
            .sortedBy { it.title }
    }
    
    /**
     * Get parser for a specific source using the factory method
     */
    private fun getParser(source: MangaParserSource): MangaParser {
        return loaderContext.newParserInstance(source)
    }
    
    /**
     * Get manga list from a source
     */
    suspend fun getMangaList(
        source: MangaParserSource,
        offset: Int = 0
    ): List<Manga> = withContext(Dispatchers.IO) {
        try {
            val parser = getParser(source)
            val sortOrder = parser.availableSortOrders.firstOrNull() ?: SortOrder.UPDATED
            parser.getList(offset, sortOrder, MangaListFilter.EMPTY)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Search manga in a source
     */
    suspend fun searchManga(
        source: MangaParserSource,
        query: String
    ): List<Manga> = withContext(Dispatchers.IO) {
        try {
            val parser = getParser(source)
            parser.getList(0, SortOrder.RELEVANCE, MangaListFilter(query = query))
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Get manga details
     */
    suspend fun getMangaDetails(manga: Manga): Manga = withContext(Dispatchers.IO) {
        try {
            val source = manga.source as? MangaParserSource ?: return@withContext manga
            val parser = getParser(source)
            parser.getDetails(manga)
        } catch (e: Exception) {
            e.printStackTrace()
            manga
        }
    }
    
    /**
     * Get manga chapters
     */
    suspend fun getChapters(manga: Manga): List<MangaChapter> = withContext(Dispatchers.IO) {
        try {
            val source = manga.source as? MangaParserSource ?: return@withContext emptyList()
            val parser = getParser(source)
            val details = parser.getDetails(manga)
            details.chapters ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Get chapter pages
     */
    suspend fun getPages(chapter: MangaChapter): List<MangaPage> = withContext(Dispatchers.IO) {
        try {
            val source = chapter.source as? MangaParserSource ?: return@withContext emptyList()
            val parser = getParser(source)
            parser.getPages(chapter)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
