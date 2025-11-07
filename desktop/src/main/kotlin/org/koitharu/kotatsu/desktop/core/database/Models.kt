package org.koitharu.kotatsu.desktop.core.database

import kotlinx.serialization.Serializable

/**
 * Data models for the database
 */

@Serializable
data class MangaEntity(
    val id: Long = 0,
    val mangaId: String,
    val title: String,
    val altTitle: String? = null,
    val url: String,
    val publicUrl: String,
    val rating: Float = 0f,
    val isNsfw: Boolean = false,
    val coverUrl: String,
    val largeCoverUrl: String? = null,
    val state: String? = null,
    val author: String? = null,
    val source: String,
    val createdAt: Long = System.currentTimeMillis() / 1000
)

@Serializable
data class FavouriteEntity(
    val id: Long = 0,
    val mangaId: String,
    val categoryId: Long = 0,
    val sortKey: Long = 0,
    val createdAt: Long = System.currentTimeMillis() / 1000
)

@Serializable
data class HistoryEntity(
    val mangaId: String,
    val createdAt: Long,
    val updatedAt: Long,
    val chapterId: String,
    val page: Int = 0,
    val scroll: Float = 0f,
    val percent: Float = 0f
)

@Serializable
data class TagEntity(
    val id: Long = 0,
    val tagId: String,
    val title: String,
    val key: String,
    val source: String
)
