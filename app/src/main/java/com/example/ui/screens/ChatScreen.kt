package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MessageSender
import com.example.ui.BasokaViewModel
import com.example.ui.components.DocumentExporter
import com.example.ui.components.ThinkingIndicator
import com.example.ui.theme.BasokaBlack
import com.example.ui.theme.BasokaChatBotBubble
import com.example.ui.theme.BasokaChatUserBubble
import com.example.ui.theme.BasokaPrimary
import com.example.ui.theme.BasokaSecondary
import com.example.ui.theme.BasokaSurface
import com.example.ui.theme.BasokaSurfaceBorder
import com.example.ui.theme.BasokaSurfaceElevated
import com.example.ui.theme.BasokaTextPrimary
import com.example.ui.theme.BasokaTextSecondary
import com.example.ui.theme.BasokaTextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    viewModel: BasokaViewModel,
    onNavigateToTasks: () -> Unit,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isThinking by viewModel.isThinking.collectAsState()
    val thinkingStatusText by viewModel.thinkingStatusText.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current

    var showVoiceModal by remember { mutableStateOf(false) }
    var attachedImageUri by remember { mutableStateOf<Uri?>(null) }
    var attachedFileName by remember { mutableStateOf<String?>(null) }

    // Android Photo Picker (Google Play Policy zero-permission compliant)
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        attachedImageUri = uri
    }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BasokaBlack)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (messages.isEmpty() && !isThinking) {
                // Empty state greeting matching exact prompt specifications
                EmptyStateGreeting(
                    onSuggestionClick = { suggestion ->
                        viewModel.onPromptSuggestionClicked(suggestion)
                    }
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(12.dp)) }

                    items(messages, key = { it.id }) { message ->
                        ChatMessageItem(
                            message = message,
                            onActionClick = onNavigateToTasks,
                            onSpeak = { text ->
                                if (isSpeaking) {
                                    viewModel.stopSpeaking()
                                } else {
                                    viewModel.speakText(text)
                                }
                            },
                            isSpeaking = isSpeaking,
                            onShare = { text ->
                                DocumentExporter.shareAsText(context, "پەیامی BASOKA AI", text)
                            }
                        )
                    }

                    if (isThinking) {
                        item {
                            ThinkingIndicator(statusText = thinkingStatusText)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }

        // Active attachments preview
        if (attachedImageUri != null || attachedFileName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BasokaSurface)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (attachedImageUri != null) {
                    AsyncImage(
                        model = attachedImageUri,
                        contentDescription = "وێنەی هاوپێچکراو",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "وێنە هاوپێچکراوە",
                        fontSize = 13.sp,
                        color = BasokaTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                } else if (attachedFileName != null) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = null,
                        tint = BasokaPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = attachedFileName ?: "",
                        fontSize = 13.sp,
                        color = BasokaTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                IconButton(
                    onClick = {
                        attachedImageUri = null
                        attachedFileName = null
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "سڕینەوەی هاوپێچ",
                        tint = BasokaTextSecondary
                    )
                }
            }
        }

        // Bottom Chat Composer
        ChatComposer(
            inputText = inputText,
            onTextChanged = { viewModel.updateInputText(it) },
            onSend = {
                val imageUriStr = attachedImageUri?.toString()
                val attachType = if (imageUriStr != null) "image" else if (attachedFileName != null) "file" else null
                viewModel.sendMessage(
                    attachmentType = attachType,
                    attachmentUri = imageUriStr,
                    attachmentName = attachedFileName
                )
                attachedImageUri = null
                attachedFileName = null
            },
            onMicClick = { showVoiceModal = true },
            onAttachImage = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onAttachFile = {
                // Simulate document attachment
                attachedFileName = "بەڵگەنامەی_ئەرکەکان.pdf"
            }
        )
    }

    // Voice Modal Dialog (Default: Voice responses are disabled, manual speech input)
    if (showVoiceModal) {
        VoiceInputDialog(
            onDismiss = { showVoiceModal = false },
            onSpeechResult = { recognizedText ->
                viewModel.updateInputText(recognizedText)
                showVoiceModal = false
            }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyStateGreeting(
    onSuggestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(2.dp, BasokaPrimary, CircleShape)
        ) {
            AsyncImage(
                model = R.drawable.basoka_logo,
                contentDescription = "لۆگۆی BASOKA",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "BASOKA",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = BasokaTextPrimary,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "سڵاو 👋 من BASOKA ـم.",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = BasokaTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "چ شتێکت بۆ بکەم؟",
            fontSize = 15.sp,
            color = BasokaTextSecondary
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Suggested prompt chips
        val suggestions = listOf(
            "وێنەی قەڵای هەولێر دروست بکە 🎨",
            "یارمەتیم بدە ڕۆژەکەم ڕێکبخەم",
            "ئەم دەقە بۆم کورت بکەرەوە",
            "ئەم فایلە بۆم شیکەرەوە",
            "بۆم پلانی خوێندن دابنێ",
            "تایمەرێکی 10 خولەکی دابنێ",
            "سبەی لە ٨ی بەیانی بیرم بخەرەوە"
        )

        FlowRow(
            horizontalArrangement = Arrangement.Center,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            suggestions.forEach { suggestion ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { onSuggestionClick(suggestion) }
                        .testTag("suggestion_chip_$suggestion"),
                    color = BasokaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
                ) {
                    Text(
                        text = suggestion,
                        fontSize = 13.sp,
                        color = BasokaTextPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessageEntity,
    onActionClick: () -> Unit,
    onSpeak: (String) -> Unit = {},
    isSpeaking: Boolean = false,
    onShare: (String) -> Unit = {}
) {
    val isUser = message.sender == MessageSender.USER
    var showFullImage by remember { mutableStateOf(false) }

    if (showFullImage && message.attachmentUri != null) {
        AlertDialog(
            onDismissRequest = { showFullImage = false },
            confirmButton = {
                Button(
                    onClick = { showFullImage = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
                ) {
                    Text("داخستن", color = Color.White)
                }
            },
            title = {
                Text(
                    text = message.attachmentName ?: "وێنەی بەرهەمهێنراو",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = BasokaTextPrimary
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = message.attachmentUri,
                        contentDescription = "بینینی تەواوی وێنە",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            },
            containerColor = BasokaSurfaceElevated
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(if (isUser) "user_message_${message.id}" else "basoka_message_${message.id}"),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(BasokaSurfaceBorder),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = BasokaPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 310.dp)
        ) {
            if (!isUser) {
                Text(
                    text = message.agentType.kurdishName,
                    fontSize = 11.sp,
                    color = BasokaSecondary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 2.dp, start = 4.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) BasokaChatUserBubble else BasokaChatBotBubble,
                border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder) else null
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (message.attachmentType == "image" && message.attachmentUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { showFullImage = true }
                        ) {
                            AsyncImage(
                                model = message.attachmentUri,
                                contentDescription = "وێنە",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Text(
                        text = message.text,
                        fontSize = 14.sp,
                        color = BasokaTextPrimary,
                        lineHeight = 22.sp
                    )

                    // Audio reading & share controls for AI response
                    if (!isUser) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = { onSpeak(message.text) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSpeaking) Icons.Default.Stop else Icons.Default.VolumeUp,
                                    contentDescription = "خوێندنەوە بە دەنگ",
                                    tint = if (isSpeaking) BasokaPrimary else BasokaTextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            IconButton(
                                onClick = { onShare(message.text) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "هاوبەشکردن",
                                    tint = BasokaTextTertiary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    if (message.structuredAction != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onActionClick() },
                            color = BasokaSurfaceElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "بینین لە بەشی کارەکان →",
                                    fontSize = 12.sp,
                                    color = BasokaPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatComposer(
    inputText: String,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit,
    onMicClick: () -> Unit,
    onAttachImage: () -> Unit,
    onAttachFile: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("chat_composer"),
        color = BasokaSurface,
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attach image button
            IconButton(
                onClick = onAttachImage,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("attach_image_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "هاوپێچکردنی وێنە",
                    tint = BasokaTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Attach file button
            IconButton(
                onClick = onAttachFile,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("attach_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "هاوپێچکردنی فایل",
                    tint = BasokaTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Text input
            OutlinedTextField(
                value = inputText,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        text = "لێرە بنووسە...",
                        fontSize = 14.sp,
                        color = BasokaTextTertiary
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BasokaPrimary,
                    unfocusedBorderColor = BasokaSurfaceBorder,
                    focusedContainerColor = BasokaSurfaceElevated,
                    unfocusedContainerColor = BasokaSurfaceElevated,
                    focusedTextColor = BasokaTextPrimary,
                    unfocusedTextColor = BasokaTextPrimary
                ),
                maxLines = 4,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .testTag("chat_input_field")
            )

            // Mic button (Explicit manual trigger only)
            IconButton(
                onClick = onMicClick,
                modifier = Modifier
                    .size(40.dp)
                    .testTag("mic_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "مایکروفۆن",
                    tint = BasokaTextSecondary,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Send button
            IconButton(
                onClick = onSend,
                enabled = inputText.isNotBlank(),
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank()) BasokaPrimary else BasokaSurfaceElevated)
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "ناردن",
                    tint = if (inputText.isNotBlank()) Color.White else BasokaTextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onSpeechResult: (String) -> Unit
) {
    val speechLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                onSpeechResult(spokenText)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BasokaSurface,
        titleContentColor = BasokaTextPrimary,
        textContentColor = BasokaTextSecondary,
        icon = {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(BasokaPrimary.copy(alpha = 0.2f))
                    .clickable {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ku")
                                putExtra(RecognizerIntent.EXTRA_PROMPT, "قسە بکە، BASOKA گوێت لێ دەگرێت...")
                            }
                            speechLauncher.launch(intent)
                        } catch (e: Exception) {
                            // Fallback if device lacks speech recognition package
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "قسەکردن بە دەنگ",
                    tint = BasokaPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }
        },
        title = {
            Text(
                text = "دۆخی دەنگیی ڕاستەوخۆ",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "کلیک لە مایکەکە بکە بۆ قسەکردن، یان یەکێک لە ڕستە خێراکان هەڵبژێرە:",
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Quick sample voice transcription options in Kurdish
                Text(
                    text = "فەرمانە دەنگییە ئامادەکراوەکان:",
                    fontSize = 12.sp,
                    color = BasokaPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                val samples = listOf(
                    "سبەی کاتژمێر ٨ بیرم بخەرەوە",
                    "تایمەری پێنج خولەکی دابنێ",
                    "پلانی ئەمڕۆم بۆ ڕێکبخە",
                    "ئەم دەقەم بۆ وەرگێڕە بۆ بادینی"
                )
                samples.forEach { sample ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onSpeechResult(sample) },
                        color = BasokaSurfaceElevated
                    ) {
                        Text(
                            text = "🎙️ «$sample»",
                            fontSize = 12.sp,
                            color = BasokaTextPrimary,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
            ) {
                Text("داخستن")
            }
        }
    )
}
