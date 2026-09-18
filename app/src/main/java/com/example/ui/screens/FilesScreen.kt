package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FileDocEntity
import com.example.ui.BasokaViewModel
import com.example.ui.theme.BasokaBlack
import com.example.ui.theme.BasokaPrimary
import com.example.ui.theme.BasokaSecondary
import com.example.ui.theme.BasokaSurface
import com.example.ui.theme.BasokaSurfaceBorder
import com.example.ui.theme.BasokaSurfaceElevated
import com.example.ui.theme.BasokaTextPrimary
import com.example.ui.theme.BasokaTextSecondary
import com.example.ui.theme.BasokaTextTertiary

@Composable
fun FilesScreen(
    viewModel: BasokaViewModel,
    onSendPromptToChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val documents by viewModel.allDocuments.collectAsState()
    var selectedDocForView by remember { mutableStateOf<FileDocEntity?>(null) }
    var showNewDocDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BasokaBlack)
            .testTag("files_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "بەڵگەنامە و فایلەکان",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = BasokaTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "کورتکراوە، وەرگێڕان و ئەو بەڵگەنامانەی لەلایەن BASOKA داڕێژراون لێرەدا دەمێننەوە.",
                fontSize = 13.sp,
                color = BasokaTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (documents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = BasokaTextTertiary,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "هیچ فایلێک پاشەکەوت نەکراوە",
                            fontSize = 15.sp,
                            color = BasokaTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "دەتوانیت لە چات بڵێیت: «ئەم دەقەم بۆ بکە بە ڕاپۆرت»",
                            fontSize = 12.sp,
                            color = BasokaTextTertiary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(documents, key = { it.id }) { doc ->
                        DocumentItemCard(
                            doc = doc,
                            onClick = { selectedDocForView = doc },
                            onDelete = { viewModel.deleteDocument(doc.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(70.dp)) }
                }
            }
        }

        FloatingActionButton(
            onClick = { showNewDocDialog = true },
            containerColor = BasokaPrimary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_document_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "فایلی نوێ")
        }
    }

    // View Document Dialog
    if (selectedDocForView != null) {
        val doc = selectedDocForView!!
        AlertDialog(
            onDismissRequest = { selectedDocForView = null },
            containerColor = BasokaSurface,
            title = {
                Text(
                    text = doc.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = BasokaTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "ژمارەی وشە: ${doc.wordCount}",
                        fontSize = 12.sp,
                        color = BasokaSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = doc.content,
                        fontSize = 14.sp,
                        color = BasokaTextPrimary,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val prompt = "ئەم فایلە بە کوردی پوخت بکەرەوە: ${doc.title}"
                        selectedDocForView = null
                        onSendPromptToChat(prompt)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
                ) {
                    Text("شیکردنەوە لە چات")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedDocForView = null }) {
                    Text("داخستن", color = BasokaTextSecondary)
                }
            }
        )
    }

    // New Document Dialog
    if (showNewDocDialog) {
        var newTitle by remember { mutableStateOf("") }
        var newContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showNewDocDialog = false },
            containerColor = BasokaSurface,
            title = {
                Text(
                    text = "زیادکردنی تێبینی یان دەق",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = BasokaTextPrimary
                )
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("ناونیشانی بەڵگەنامە") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BasokaPrimary,
                            unfocusedBorderColor = BasokaSurfaceBorder,
                            focusedTextColor = BasokaTextPrimary,
                            unfocusedTextColor = BasokaTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("ناوەڕۆکی دەق") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BasokaPrimary,
                            unfocusedBorderColor = BasokaSurfaceBorder,
                            focusedTextColor = BasokaTextPrimary,
                            unfocusedTextColor = BasokaTextPrimary
                        ),
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank() && newContent.isNotBlank()) {
                            onSendPromptToChat("ئەم دەقەم بۆ کورت بکەرەوە و لە فایلەکاندا هەڵیگرە: $newTitle\n$newContent")
                            showNewDocDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
                ) {
                    Text("ناردن بۆ شیکاری")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewDocDialog = false }) {
                    Text("هەڵوەشاندنەوە", color = BasokaTextSecondary)
                }
            }
        )
    }
}

@Composable
fun DocumentItemCard(
    doc: FileDocEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("doc_card_${doc.id}"),
        color = BasokaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(BasokaSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = BasokaPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = doc.title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = BasokaTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${doc.wordCount} وشە • فۆرماتی ${doc.fileType.uppercase()}",
                        fontSize = 12.sp,
                        color = BasokaTextSecondary
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "سڕینەوەی فایل",
                    tint = BasokaTextSecondary
                )
            }
        }
    }
}
