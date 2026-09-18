package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.ActionType
import com.example.ai.DeviceController
import com.example.ai.GeminiService
import com.example.ai.KurdishNlpEngine
import com.example.ai.ParsedIntent
import com.example.data.model.AgentType
import com.example.data.model.ChatMessageEntity
import com.example.data.model.FileDocEntity
import com.example.data.model.MemoryEntity
import com.example.data.model.MessageSender
import com.example.data.model.TaskEntity
import com.example.data.model.TaskType
import com.example.data.repository.BasokaRepository
import com.example.ui.components.KurdishTtsManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class ConfirmationDialogState(
    val isVisible: Boolean = false,
    val prompt: String = "",
    val onConfirm: () -> Unit = {},
    val onDismiss: () -> Unit = {}
)

class BasokaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BasokaRepository(application)
    private val deviceController = DeviceController(application)
    private val geminiService = GeminiService()
    val ttsManager = KurdishTtsManager(application)
    val isSpeaking: StateFlow<Boolean> = ttsManager.isSpeaking

    // 1. Navigation tab index (0: چات, 1: تواناکان, 2: کارەکان, 3: فایلەکان, 4: ڕێکخستنەکان)
    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    // 2. Chat state
    val messages: StateFlow<List<ChatMessageEntity>> = repository.allMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _thinkingStatusText = MutableStateFlow("BASOKA خەریکی بیرکردنەوەیە...")
    val thinkingStatusText: StateFlow<String> = _thinkingStatusText.asStateFlow()

    private val _currentAgent = MutableStateFlow(AgentType.GENERAL)
    val currentAgent: StateFlow<AgentType> = _currentAgent.asStateFlow()

    // 3. Confirmation dialog state
    private val _confirmationState = MutableStateFlow(ConfirmationDialogState())
    val confirmationState: StateFlow<ConfirmationDialogState> = _confirmationState.asStateFlow()

    // 4. Tasks & Alarms
    val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 5. Memory
    val allMemories: StateFlow<List<MemoryEntity>> = repository.allMemories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 6. Documents / Files
    val allDocuments: StateFlow<List<FileDocEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 7. Settings
    val isVoiceModeEnabled: StateFlow<Boolean> = repository.isVoiceModeEnabled
    val isMemoryEnabled: StateFlow<Boolean> = repository.isMemoryEnabled

    // 8. Pending Clarification Intent
    private var pendingClarificationContext: String? = null

    // 9. Timer worker job
    private var timerJob: Job? = null

    init {
        startTimerLoop()
    }

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun onPromptSuggestionClicked(suggestion: String) {
        _inputText.value = suggestion
        sendMessage(suggestion)
    }

    fun sendMessage(
        customText: String? = null,
        attachmentType: String? = null,
        attachmentUri: String? = null,
        attachmentName: String? = null
    ) {
        val textToSend = (customText ?: _inputText.value).trim()
        if (textToSend.isEmpty() && attachmentUri == null) return

        _inputText.value = ""

        viewModelScope.launch {
            // Save user message
            repository.saveMessage(
                ChatMessageEntity(
                    text = textToSend,
                    sender = MessageSender.USER,
                    attachmentType = attachmentType,
                    attachmentUri = attachmentUri,
                    attachmentName = attachmentName
                )
            )

            processAssistantResponse(textToSend, attachmentType, attachmentUri)
        }
    }

    private suspend fun processAssistantResponse(
        userText: String,
        attachmentType: String?,
        attachmentUri: String?
    ) {
        _isThinking.value = true

        // Set contextual loading message in Kurdish
        _thinkingStatusText.value = when {
            (userText.contains("دروست") || userText.contains("بکێشە") || userText.contains("بەرهەم")) &&
                    (userText.contains("وێنە") || userText.contains("تابلۆ") || userText.contains("image")) ->
                "خەریکی دروستکردن و نەخشاندنی وێنەکەیە بە ژیری دەستکرد..."
            attachmentType == "file" || userText.contains("فایل") -> "خەریکی شیکردنەوەی فایلەکەیە..."
            attachmentType == "image" || userText.contains("وێنە") -> "خەریکی شیکردنەوەی وێنەکەیە..."
            userText.contains("کۆد") -> "خەریکی تاوتوێکردنی کۆدەکەیە..."
            else -> "BASOKA خەریکی بیرکردنەوەیە..."
        }

        delay(400) // Small organic pause for natural feel

        val tasksList = allTasks.value
        val hasActiveTimer = tasksList.any { it.taskType == TaskType.TIMER && it.isTimerRunning }

        // Combined text if answering a clarification
        val fullContextText = if (pendingClarificationContext != null) {
            val combined = "$pendingClarificationContext $userText"
            pendingClarificationContext = null
            combined
        } else {
            userText
        }

        val parsedIntent = KurdishNlpEngine.parseUserInput(fullContextText, hasActiveTimer)
        _currentAgent.value = parsedIntent.agentType

        // Check if sensitive confirmation is required
        if (parsedIntent.needsConfirmation) {
            _isThinking.value = false
            _confirmationState.value = ConfirmationDialogState(
                isVisible = true,
                prompt = parsedIntent.confirmationPrompt,
                onConfirm = {
                    _confirmationState.value = ConfirmationDialogState()
                    executeAction(parsedIntent)
                },
                onDismiss = {
                    _confirmationState.value = ConfirmationDialogState()
                    viewModelScope.launch {
                        repository.saveMessage(
                            ChatMessageEntity(
                                text = "کردارەکە هەڵوەشێنرایەوە لەسەر داوای خۆت.",
                                sender = MessageSender.BASOKA,
                                agentType = parsedIntent.agentType
                            )
                        )
                    }
                }
            )
            return
        }

        // Check if clarification is needed
        if (parsedIntent.needsClarification) {
            pendingClarificationContext = userText
            _isThinking.value = false
            repository.saveMessage(
                ChatMessageEntity(
                    text = parsedIntent.clarificationQuestion,
                    sender = MessageSender.BASOKA,
                    agentType = parsedIntent.agentType
                )
            )
            return
        }

        // Execute valid intent action
        executeAction(parsedIntent)
    }

    private fun executeAction(intent: ParsedIntent) {
        viewModelScope.launch {
            when (intent.actionType) {
                ActionType.START_TIMER -> {
                    val task = TaskEntity(
                        title = intent.title,
                        taskType = TaskType.TIMER,
                        timerDurationSeconds = intent.timerDurationSeconds,
                        timerRemainingSeconds = intent.timerDurationSeconds,
                        isTimerRunning = true
                    )
                    val id = repository.saveTask(task)
                    respondFromBasoka(
                        "${intent.responseText}\n(دەتوانیت لە بەشی «کارەکان» چاودێری بکەیت)",
                        intent.agentType,
                        structuredAction = "TIMER_STARTED",
                        actionPayload = id.toString()
                    )
                }

                ActionType.STOP_TIMER -> {
                    val activeTimer = allTasks.value.firstOrNull { it.taskType == TaskType.TIMER && it.isTimerRunning }
                    if (activeTimer != null) {
                        repository.updateTimerState(activeTimer.id, activeTimer.timerRemainingSeconds, false)
                        respondFromBasoka("تایمەرەکە بە سەرکەوتوویی ڕاگیرا.", intent.agentType)
                    } else {
                        respondFromBasoka("هیچ تایمەرێکی چالاک نەدۆزرایەوە بۆ ڕاگرتن.", intent.agentType)
                    }
                }

                ActionType.RESET_TIMER -> {
                    val timer = allTasks.value.firstOrNull { it.taskType == TaskType.TIMER }
                    if (timer != null) {
                        repository.updateTimerState(timer.id, timer.timerDurationSeconds, false)
                        respondFromBasoka("تایمەرەکە ڕیسێت کرایەوە بۆ کاتی بنەڕەتی.", intent.agentType)
                    } else {
                        respondFromBasoka("هیچ تایمەرێک نییە بۆ ڕیسێتکردن.", intent.agentType)
                    }
                }

                ActionType.SET_REMINDER -> {
                    repository.saveTask(
                        TaskEntity(
                            title = intent.title,
                            taskType = TaskType.REMINDER,
                            targetTimeMillis = intent.targetTimeMillis,
                            timeLabel = intent.timeLabel,
                            recurrence = intent.recurrence
                        )
                    )
                    // Also save to memory if enabled
                    if (isMemoryEnabled.value) {
                        repository.saveMemory("بیرخستنەوە", "${intent.title} (${intent.timeLabel})", "بیرخستنەوە")
                    }
                    respondFromBasoka(intent.responseText, intent.agentType, "REMINDER_SAVED")
                }

                ActionType.SET_ALARM -> {
                    repository.saveTask(
                        TaskEntity(
                            title = intent.title,
                            taskType = TaskType.ALARM,
                            targetTimeMillis = intent.targetTimeMillis,
                            timeLabel = intent.timeLabel
                        )
                    )
                    respondFromBasoka(intent.responseText, intent.agentType, "ALARM_SAVED")
                }

                ActionType.ADD_CALENDAR -> {
                    repository.saveTask(
                        TaskEntity(
                            title = intent.title,
                            taskType = TaskType.CALENDAR,
                            targetTimeMillis = intent.targetTimeMillis,
                            timeLabel = intent.timeLabel
                        )
                    )
                    respondFromBasoka("کۆبوونەوەکە بە سەرکەوتوویی لە ڕۆژژمێر زیادکرا: ${intent.title} (${intent.timeLabel})", intent.agentType)
                }

                ActionType.DEVICE_CONTROL -> {
                    when (intent.executableCommand) {
                        "FLASH_ON" -> deviceController.toggleFlashlight(true)
                        "FLASH_OFF" -> deviceController.toggleFlashlight(false)
                        "OPEN_WIFI" -> deviceController.openWifiSettings()
                        "OPEN_BLUETOOTH" -> deviceController.openBluetoothSettings()
                        "OPEN_SETTINGS" -> deviceController.openGeneralSettings()
                        "OPEN_MAPS" -> deviceController.openMaps()
                        "ADJUST_VOLUME" -> deviceController.adjustVolume(true)
                    }
                    respondFromBasoka(intent.responseText, intent.agentType)
                }

                ActionType.CLEAR_DATA -> {
                    repository.clearChat()
                    repository.clearMemories()
                    respondFromBasoka("تەواوی داتا و یادەوەرییەکان بە سەرکەوتوویی پاککرانەوە.", intent.agentType)
                }

                ActionType.AUTOMATION_RULE -> {
                    repository.saveTask(
                        TaskEntity(
                            title = intent.title,
                            taskType = TaskType.AUTOMATION,
                            automationCondition = intent.title
                        )
                    )
                    respondFromBasoka(intent.responseText, intent.agentType)
                }

                ActionType.FILE_OPERATION, ActionType.WRITING_TASK -> {
                    // Save document to files section as well
                    repository.saveDocument(
                        title = intent.title.ifEmpty { "بەڵگەنامەی نوێ" },
                        content = intent.responseText
                    )
                    respondFromBasoka(intent.responseText, intent.agentType, "FILE_SAVED")
                }

                ActionType.GENERATE_IMAGE -> {
                    _thinkingStatusText.value = "خەریکی بەرهەمهێنان و نەخشاندنی وێنەکەیە بە ژیری دەستکرد..."
                    val prompt = intent.detail.ifEmpty { intent.title }
                    val imageResult = geminiService.generateImage(prompt, getApplication())

                    repository.saveMessage(
                        ChatMessageEntity(
                            text = "${imageResult.description}\n\n🎨 وێنەکەت بە سەرکەوتوویی دروستکرا. دەتوانیت کلیکی لەسەر بکەیت بۆ بینینی گەورەکراو یان لە بەشی «فایلەکان» بیبینیت.",
                            sender = MessageSender.BASOKA,
                            agentType = intent.agentType,
                            attachmentType = "image",
                            attachmentUri = imageResult.imageUri,
                            attachmentName = prompt
                        )
                    )

                    // Also save document record in Files
                    repository.saveDocument(
                        title = "وێنەی ژیری دەستکرد: ${prompt.take(24)}",
                        content = "پڕۆمتی وێنە: $prompt\nڕێڕەو: ${imageResult.imageUri}\nوەسف: ${imageResult.description}",
                        summary = "وێنەی بەرهەمهێنراو بە ژیری دەستکرد"
                    )
                }

                ActionType.CHAT_RESPONSE, ActionType.QUERY_TIMER, ActionType.REQUEST_PERMISSION,
                ActionType.IMAGE_OPERATION, ActionType.STUDY_TASK, ActionType.CODE_TASK,
                ActionType.RESEARCH_TASK, ActionType.MEMORY_OPERATION -> {
                    // Attempt Gemini API if available, else use NLP engine response
                    val memoriesList = allMemories.value.map { "${it.key}: ${it.value}" }
                    val geminiResult = geminiService.generateKurdishResponse(intent.responseText, memoriesList)

                    val finalText = geminiResult.getOrNull() ?: intent.responseText
                    respondFromBasoka(finalText, intent.agentType)
                }
            }
            _isThinking.value = false
        }
    }

    private suspend fun respondFromBasoka(
        text: String,
        agent: AgentType,
        structuredAction: String? = null,
        actionPayload: String? = null
    ) {
        repository.saveMessage(
            ChatMessageEntity(
                text = text,
                sender = MessageSender.BASOKA,
                agentType = agent,
                structuredAction = structuredAction,
                actionPayload = actionPayload
            )
        )
    }

    // Timer background countdown loop
    private fun startTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                val runningTimers = allTasks.value.filter { it.taskType == TaskType.TIMER && it.isTimerRunning }
                for (timer in runningTimers) {
                    val newRemaining = timer.timerRemainingSeconds - 1
                    if (newRemaining <= 0) {
                        repository.updateTimerState(timer.id, 0, false)
                        repository.saveMessage(
                            ChatMessageEntity(
                                text = "⏰ کاتی ${timer.title} تەواو بوو!",
                                sender = MessageSender.BASOKA,
                                agentType = AgentType.DEVICE
                            )
                        )
                    } else {
                        repository.updateTimerState(timer.id, newRemaining, true)
                    }
                }
            }
        }
    }

    // Task actions
    fun toggleTaskComplete(id: Long, completed: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskComplete(id, completed)
        }
    }

    fun toggleTaskEnabled(id: Long, enabled: Boolean) {
        viewModelScope.launch {
            repository.toggleTaskEnabled(id, enabled)
        }
    }

    fun deleteTask(id: Long) {
        viewModelScope.launch {
            repository.deleteTaskById(id)
        }
    }

    fun toggleTimer(task: TaskEntity) {
        viewModelScope.launch {
            val newRunning = !task.isTimerRunning
            repository.updateTimerState(task.id, task.timerRemainingSeconds, newRunning)
        }
    }

    fun resetTimer(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTimerState(task.id, task.timerDurationSeconds, false)
        }
    }

    // Memory actions
    fun deleteMemory(id: Long) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    fun clearAllMemories() {
        viewModelScope.launch {
            repository.clearMemories()
        }
    }

    // File actions
    fun deleteDocument(id: Long) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    // Settings actions
    fun setVoiceMode(enabled: Boolean) {
        repository.setVoiceMode(enabled)
    }

    fun setMemoryEnabled(enabled: Boolean) {
        repository.setMemoryEnabled(enabled)
    }

    fun clearAllChat() {
        viewModelScope.launch {
            repository.clearChat()
        }
    }

    fun speakText(text: String) {
        ttsManager.speak(text)
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        ttsManager.shutdown()
        timerJob?.cancel()
    }
}
