package org.koitharu.kotatsu.desktop.core.database

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * SQLite database manager for desktop
 * Replaces Room from Android version
 */
class Database(dataDirectory: File) {
    
    private val dbFile = File(dataDirectory, "kotatsu.db")
    private val connection: Connection
    
    init {
        // Load SQLite JDBC driver
        Class.forName("org.sqlite.JDBC")
        
        // Create database connection
        connection = DriverManager.getConnection("jdbc:sqlite:${dbFile.absolutePath}")
        
        // Initialize tables
        initializeTables()
    }
    
    private fun initializeTables() {
        connection.createStatement().use { statement ->
            // Manga table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS manga (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    manga_id TEXT NOT NULL UNIQUE,
                    title TEXT NOT NULL,
                    alt_title TEXT,
                    url TEXT NOT NULL,
                    public_url TEXT NOT NULL,
                    rating REAL NOT NULL DEFAULT 0,
                    is_nsfw INTEGER NOT NULL DEFAULT 0,
                    cover_url TEXT NOT NULL,
                    large_cover_url TEXT,
                    state TEXT,
                    author TEXT,
                    source TEXT NOT NULL,
                    created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
                )
            """.trimIndent())
            
            // Favorites table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS favourites (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    manga_id TEXT NOT NULL,
                    category_id INTEGER NOT NULL DEFAULT 0,
                    sort_key INTEGER NOT NULL DEFAULT 0,
                    created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
                    FOREIGN KEY (manga_id) REFERENCES manga(manga_id) ON DELETE CASCADE
                )
            """.trimIndent())
            
            // History table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS history (
                    manga_id TEXT PRIMARY KEY,
                    created_at INTEGER NOT NULL,
                    updated_at INTEGER NOT NULL,
                    chapter_id TEXT NOT NULL,
                    page INTEGER NOT NULL DEFAULT 0,
                    scroll REAL NOT NULL DEFAULT 0,
                    percent REAL NOT NULL DEFAULT 0,
                    FOREIGN KEY (manga_id) REFERENCES manga(manga_id) ON DELETE CASCADE
                )
            """.trimIndent())
            
            // Tags table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS tags (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    tag_id TEXT NOT NULL UNIQUE,
                    title TEXT NOT NULL,
                    key TEXT NOT NULL,
                    source TEXT NOT NULL
                )
            """.trimIndent())
            
            // Manga tags relation
            statement.execute("""
                CREATE TABLE IF NOT EXISTS manga_tags (
                    manga_id TEXT NOT NULL,
                    tag_id TEXT NOT NULL,
                    PRIMARY KEY (manga_id, tag_id),
                    FOREIGN KEY (manga_id) REFERENCES manga(manga_id) ON DELETE CASCADE,
                    FOREIGN KEY (tag_id) REFERENCES tags(tag_id) ON DELETE CASCADE
                )
            """.trimIndent())
            
            // Create indexes
            statement.execute("CREATE INDEX IF NOT EXISTS idx_manga_source ON manga(source)")
            statement.execute("CREATE INDEX IF NOT EXISTS idx_favourites_manga ON favourites(manga_id)")
            statement.execute("CREATE INDEX IF NOT EXISTS idx_history_updated ON history(updated_at DESC)")
        }
    }
    
    fun getConnection(): Connection = connection
    
    fun close() {
        if (!connection.isClosed) {
            connection.close()
        }
    }
    
    /**
     * Execute a query and return results
     */
    fun query(sql: String, vararg params: Any): ResultSet {
        val statement = connection.prepareStatement(sql)
        params.forEachIndexed { index, param ->
            statement.setObject(index + 1, param)
        }
        return statement.executeQuery()
    }
    
    /**
     * Execute an update/insert/delete statement
     */
    fun execute(sql: String, vararg params: Any): Int {
        connection.prepareStatement(sql).use { statement ->
            params.forEachIndexed { index, param ->
                statement.setObject(index + 1, param)
            }
            return statement.executeUpdate()
        }
    }
    
    /**
     * Begin a transaction
     */
    fun beginTransaction() {
        connection.autoCommit = false
    }
    
    /**
     * Commit a transaction
     */
    fun commitTransaction() {
        connection.commit()
        connection.autoCommit = true
    }
    
    /**
     * Rollback a transaction
     */
    fun rollbackTransaction() {
        connection.rollback()
        connection.autoCommit = true
    }
}
