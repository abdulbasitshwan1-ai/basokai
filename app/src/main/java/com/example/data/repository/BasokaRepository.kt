package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.FileDocEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TaskType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BasokaRepository(context: Context) {
    private val db = AppDatabase.getDatabase(context)
    private val chatDao = db.chatMessageDao()
    private val taskDao = db.taskDao()
    private val memoryDao = db.memoryDao()
    private val fileDao = db.fileDocDao()

    private val prefs: SharedPreferences =
        context.getSharedPreferences("basoka_settings_prefs", Context.MODE_PRIVATE)

    private val _isVoiceModeEnabled = MutableStateFlow(prefs.getBoolean("voice_mode_enabled", false))
    val isVoiceModeEnabled: StateFlow<Boolean> = _isVoiceModeEnabled.asStateFlow()

    private val _isMemoryEnabled = MutableStateFlow(prefs.getBoolean("memory_enabled", true))
    val isMemoryEnabled: StateFlow<Boolean> = _isMemoryEnabled.asStateFlow()

    private val _userLanguage = MutableStateFlow(prefs.getString("app_language", "ckb") ?: "ckb")
    val userLanguage: StateFlow<String> = _userLanguage.asStateFlow()

    // Chat operations
    val allMessages: Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()

    suspend fun saveMessage(message: ChatMessageEntity): Long = chatDao.insertMessage(message)

    suspend fun clearChat() = chatDao.clearAllMessages()

    suspend fun deleteMessage(id: Long) = chatDao.deleteMessageById(id)

    // Task operations
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    fun getTasksByType(type: TaskType): Flow<List<TaskEntity>> = taskDao.getTasksByType(type)

    suspend fun saveTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun toggleTaskComplete(id: Long, completed: Boolean) = taskDao.setTaskCompleted(id, completed)

    suspend fun toggleTaskEnabled(id: Long, enabled: Boolean) = taskDao.setTaskEnabled(id, enabled)

    suspend fun updateTimerState(id: Long, remaining: Int, running: Boolean) =
        taskDao.updateTimerState(id, remaining, running)

    // Memory operations
    val allMemories: Flow<List<MemoryEntity>> = memoryDao.getAllMemories()

    suspend fun saveMemory(key: String, value: String, category: String = "گشتی"): Long =
        memoryDao.insertMemory(MemoryEntity(key = key, value = value, category = category))

    suspend fun deleteMemory(id: Long) = memoryDao.deleteMemoryById(id)

    suspend fun clearMemories() = memoryDao.clearAllMemories()

    // Documents / Files
    val allDocuments: Flow<List<FileDocEntity>> = fileDao.getAllDocuments()

    suspend fun saveDocument(title: String, content: String, summary: String? = null, translation: String? = null): Long =
        fileDao.insertDocument(
            FileDocEntity(
                title = title,
                content = content,
                summary = summary,
                translation = translation,
                wordCount = content.trim().split("\\s+".toRegex()).size
            )
        )

    suspend fun deleteDocument(id: Long) = fileDao.deleteDocumentById(id)

    // Settings
    fun setVoiceMode(enabled: Boolean) {
        prefs.edit().putBoolean("voice_mode_enabled", enabled).apply()
        _isVoiceModeEnabled.value = enabled
    }

    fun setMemoryEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("memory_enabled", enabled).apply()
        _isMemoryEnabled.value = enabled
    }
}
