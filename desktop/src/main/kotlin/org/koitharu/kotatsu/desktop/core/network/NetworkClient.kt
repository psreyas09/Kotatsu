package org.koitharu.kotatsu.desktop.core.network

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Network client wrapper for OkHttp
 */
class NetworkClient(private val okHttpClient: OkHttpClient) {
    
    suspend fun get(url: String): Response {
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        
        return okHttpClient.newCall(request).execute()
    }
    
    suspend fun getHtml(url: String): String {
        return get(url).use { response ->
            if (!response.isSuccessful) {
                throw Exception("HTTP ${response.code}: ${response.message}")
            }
            response.body?.string() ?: ""
        }
    }
}
