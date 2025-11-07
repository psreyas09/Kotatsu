package org.koitharu.kotatsu.desktop.core.manga

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import org.koitharu.kotatsu.parsers.model.*

/**
 * Manga repository for fetching manga from online sources
 * Simplified version that provides mock data for now
 */
class MangaRepository(
    private val okHttpClient: OkHttpClient
) {
    
    /**
     * Get all available manga sources
     */
    fun getAvailableSources(): List<MangaSourceInfo> {
        // Return a list of popular manga sources
        return listOf(
            MangaSourceInfo("MangaDex", "mangadex"),
            MangaSourceInfo("MangaReader", "mangareader"),
            MangaSourceInfo("MangaKakalot", "mangakakalot"),
            MangaSourceInfo("MangaNelo", "manganelo"),
            MangaSourceInfo("MangaPark", "mangapark"),
            MangaSourceInfo("MangaHere", "mangahere"),
            MangaSourceInfo("MangaFox", "mangafox"),
            MangaSourceInfo("Mangasee", "mangasee"),
            MangaSourceInfo("KissManga", "kissmanga"),
            MangaSourceInfo("ReadManga", "readmanga"),
        )
    }
    
    /**
     * Get manga list from a source
     */
    suspend fun getMangaList(
        source: MangaSourceInfo,
        offset: Int = 0
    ): List<MangaInfo> = withContext(Dispatchers.IO) {
        // Return mock data for now
        listOf(
            MangaInfo(
                id = "1",
                title = "One Piece",
                coverUrl = "",
                rating = 4.8f,
                source = source.id
            ),
            MangaInfo(
                id = "2",
                title = "Naruto",
                coverUrl = "",
                rating = 4.7f,
                source = source.id
            ),
            MangaInfo(
                id = "3",
                title = "Bleach",
                coverUrl = "",
                rating = 4.5f,
                source = source.id
            ),
            MangaInfo(
                id = "4",
                title = "Attack on Titan",
                coverUrl = "",
                rating = 4.9f,
                source = source.id
            ),
            MangaInfo(
                id = "5",
                title = "My Hero Academia",
                coverUrl = "",
                rating = 4.6f,
                source = source.id
            ),
        )
    }
    
    /**
     * Search manga in a source
     */
    suspend fun searchManga(
        source: MangaSourceInfo,
        query: String
    ): List<MangaInfo> = withContext(Dispatchers.IO) {
        // Filter mock data by query
        getMangaList(source).filter {
            it.title.contains(query, ignoreCase = true)
        }
    }
}

data class MangaSourceInfo(
    val name: String,
    val id: String
)

data class MangaInfo(
    val id: String,
    val title: String,
    val coverUrl: String,
    val rating: Float,
    val source: String
)
