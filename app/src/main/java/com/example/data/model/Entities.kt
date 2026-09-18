package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class MessageSender {
    USER,
    BASOKA,
    SYSTEM
}

enum class AgentType(val kurdishName: String, val kurdishRole: String) {
    GENERAL("زیرەکی گشتی", "یاریدەدەری گشتی و گفتوگۆ"),
    WRITING("ئەیجێنتی نووسین", "داڕشتن، ئیمەیڵ، کورتکردنەوە و دەستکاریکردن"),
    RESEARCH("ئەیجێنتی توێژینەوە", "گەڕان و شیکردنەوەی زانیاری و بەڵگە"),
    CODE("ئەیجێنتی کۆد", "پەرەپێدانی نەرمەکاڵا و چارەسەری هەڵە"),
    STUDY("ئەیجێنتی خوێندن", "پلان، تاقیکردنەوە و ڕوونکردنەوەی وانە"),
    FILE("ئەیجێنتی فایل", "شیکردنەوە و وەرگێڕانی بەڵگەنامەکان"),
    VISION("ئەیجێنتی وێنە", "خوێندنەوە و شیتاڵکردنی وێنە و دەق"),
    PLANNING("ئەیجێنتی پلان و بەرهەمهێنان", "ڕێکخستنی کات، کۆبوونەوە و ئەرکەکان"),
    AUTOMATION("ئەیجێنتی ئۆتۆماتیک", "ڕێکخستنی سیستەمی خودکار و workflow"),
    DEVICE("ئەیجێنتی مۆبایل", "کۆنترۆڵی سیستم، فلاش، ڕێکخستن و کاتژمێر")
}

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val sender: MessageSender,
    val timestamp: Long = System.currentTimeMillis(),
    val agentType: AgentType = AgentType.GENERAL,
    val attachmentType: String? = null, // "image", "file", "audio"
    val attachmentUri: String? = null,
    val attachmentName: String? = null,
    val structuredAction: String? = null, // e.g. "REMINDER_CREATED", "CONFIRM_REQUIRED"
    val actionPayload: String? = null
)

enum class TaskType(val kurdishTitle: String) {
    REMINDER("بیرخستنەوە"),
    ALARM("زەنگی بێدارکەرەوە"),
    TIMER("تایمەر"),
    CALENDAR("ڕۆژژمێر و کۆبوونەوە"),
    AUTOMATION("ئۆتۆماتیک")
}

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val taskType: TaskType,
    val targetTimeMillis: Long = 0L,
    val timeLabel: String = "",
    val recurrence: String = "تاک", // "تاک", "ڕۆژانە", "هەفتانە", "مانگانە", "ساڵانە"
    val isCompleted: Boolean = false,
    val isEnabled: Boolean = true,
    val timerDurationSeconds: Int = 0,
    val timerRemainingSeconds: Int = 0,
    val isTimerRunning: Boolean = false,
    val location: String? = null,
    val note: String? = null,
    val automationCondition: String? = null
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val key: String,
    val value: String,
    val category: String = "گشتی",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_documents")
data class FileDocEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val fileType: String = "txt",
    val summary: String? = null,
    val translation: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val wordCount: Int = 0
)
