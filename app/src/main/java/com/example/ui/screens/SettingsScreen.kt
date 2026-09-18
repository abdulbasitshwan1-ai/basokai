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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.BasokaViewModel
import com.example.ui.theme.BasokaAmber
import com.example.ui.theme.BasokaBlack
import com.example.ui.theme.BasokaPrimary
import com.example.ui.theme.BasokaRose
import com.example.ui.theme.BasokaSecondary
import com.example.ui.theme.BasokaSurface
import com.example.ui.theme.BasokaSurfaceBorder
import com.example.ui.theme.BasokaSurfaceElevated
import com.example.ui.theme.BasokaTertiary
import com.example.ui.theme.BasokaTextPrimary
import com.example.ui.theme.BasokaTextSecondary
import com.example.ui.theme.BasokaTextTertiary

@Composable
fun SettingsScreen(
    viewModel: BasokaViewModel,
    modifier: Modifier = Modifier
) {
    val isVoiceEnabled by viewModel.isVoiceModeEnabled.collectAsState()
    val isMemoryEnabled by viewModel.isMemoryEnabled.collectAsState()
    val memories by viewModel.allMemories.collectAsState()

    var showClearDataConfirm by remember { mutableStateOf(false) }
    var showMemoriesDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showPermissionsDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BasokaBlack)
            .padding(horizontal = 16.dp)
            .testTag("settings_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            // Profile / Brand Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BasokaSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BasokaSurfaceElevated)
                    ) {
                        AsyncImage(
                            model = R.drawable.basoka_logo,
                            contentDescription = "BASOKA",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "BASOKA Assistant",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = BasokaTextPrimary
                        )
                        Text(
                            text = "وەشانی پیشەیی ١.٠ • پڕۆفایل پارێزراوە",
                            fontSize = 12.sp,
                            color = BasokaSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // 1. Voice Settings
        item {
            SettingsCategoryHeader(title = "دەنگ و گفتوگۆ")
            SettingsToggleCard(
                icon = Icons.Default.Mic,
                title = "دۆخی دەنگ (Voice Mode)",
                subtitle = "بە بنەڕەتی کوژاوەیە. لە چاتدا بە نووسین وەڵام دەدرێتەوە تاوەکو بێزاری دروست نەبێت.",
                isChecked = isVoiceEnabled,
                onCheckedChange = { viewModel.setVoiceMode(it) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 2. Memory Settings
        item {
            SettingsCategoryHeader(title = "یادەوەری و کەسایەتی")
            SettingsToggleCard(
                icon = Icons.Default.Psychology,
                title = "یادەوەری زیرەک (Smart Memory)",
                subtitle = "پاراستنی حەز و زانیارییە گرنگەکان بۆ پێشکەشکردنی وەڵامی تایبەتمەند.",
                isChecked = isMemoryEnabled,
                onCheckedChange = { viewModel.setMemoryEnabled(it) }
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsClickableCard(
                icon = Icons.Default.Storage,
                title = "بینینی یادەوەرییەکانم (${memories.size})",
                subtitle = "بەڕێوەبردن، دەستکاریکردن و سڕینەوەی ئەو شتانەی BASOKA لەبیرێتی.",
                onClick = { showMemoriesDialog = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 3. System & Permissions
        item {
            SettingsCategoryHeader(title = "مۆڵەتەکانی مۆبایل و سیستەم")
            SettingsClickableCard(
                icon = Icons.Default.PhoneAndroid,
                title = "مۆڵەتەکانی ئەندرۆید",
                subtitle = "کامێرا، فلاش، دەنگ، ڕۆژژمێر و ئاگادارکردنەوەکان بە شێوازی پارێزراو.",
                onClick = { showPermissionsDialog = true }
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsClickableCard(
                icon = Icons.Default.Security,
                title = "ئاسایش و نهێنی پارێزی",
                subtitle = "تەواوی داتاکان لە ناو مۆبایلەکەتدا دەمێننەوە (Offline First) و هیچی بێ مۆڵەت ناڕوات.",
                onClick = {}
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 4. Data Control
        item {
            SettingsCategoryHeader(title = "کۆنترۆڵی داتا و پاککردنەوە")
            SettingsClickableCard(
                icon = Icons.Default.Delete,
                title = "سڕینەوەی هەموو داتاکان",
                subtitle = "پاککردنەوەی تەواوی چات، یادەوەرییەکان و کارەکان بە یەکجاری.",
                titleColor = BasokaRose,
                onClick = { showClearDataConfirm = true }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // 5. About
        item {
            SettingsCategoryHeader(title = "دەربارەی ئەپ")
            SettingsClickableCard(
                icon = Icons.Default.Info,
                title = "فەلسەفەی BASOKA",
                subtitle = "«سادە لە دەرەوە، زۆر بەهێز لە ناوەوە»",
                onClick = { showAboutDialog = true }
            )
            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Clear Data Confirmation Dialog
    if (showClearDataConfirm) {
        AlertDialog(
            onDismissRequest = { showClearDataConfirm = false },
            containerColor = BasokaSurface,
            title = {
                Text(
                    text = "دڵنیایت لە سڕینەوە؟",
                    fontWeight = FontWeight.Bold,
                    color = BasokaRose
                )
            },
            text = {
                Text(
                    text = "ئەم کردارە تەواوی نامەکانی چات و یادەوەرییەکان دەسڕێتەوە و ناگەڕێتەوە.",
                    color = BasokaTextPrimary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllChat()
                        viewModel.clearAllMemories()
                        showClearDataConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaRose)
                ) {
                    Text("بەڵێ، پاکی بکەرەوە")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDataConfirm = false }) {
                    Text("هەڵوەشاندنەوە", color = BasokaTextSecondary)
                }
            }
        )
    }

    // Memories Dialog
    if (showMemoriesDialog) {
        AlertDialog(
            onDismissRequest = { showMemoriesDialog = false },
            containerColor = BasokaSurface,
            title = {
                Text(
                    text = "یادەوەرییە هەڵگیراوەکان",
                    fontWeight = FontWeight.Bold,
                    color = BasokaTextPrimary
                )
            },
            text = {
                if (memories.isEmpty()) {
                    Text(
                        text = "هیچ یادەوەرییەک تۆمار نەکراوە تا ئێستا.",
                        color = BasokaTextSecondary
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        memories.forEach { mem ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = BasokaSurfaceElevated
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = mem.key,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BasokaPrimary
                                        )
                                        Text(
                                            text = mem.value,
                                            fontSize = 12.sp,
                                            color = BasokaTextPrimary
                                        )
                                    }
                                    IconButton(onClick = { viewModel.deleteMemory(mem.id) }) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "سڕینەوە",
                                            tint = BasokaTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showMemoriesDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
                ) {
                    Text("داخستن")
                }
            }
        )
    }

    // About Dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = BasokaSurface,
            title = {
                Text(
                    text = "BASOKA - یاریدەدەری ژیر",
                    fontWeight = FontWeight.Bold,
                    color = BasokaTextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "فەلسەفەی سەرەکی:\n«سادە لە دەرەوە، زۆر بەهێز لە ناوەوە.»\n\nئەم ئەپە توانای هەیە زیاتر لە ١٠٠٠+ ئەرک، بیرخستنەوە، کۆد، خوێندن، فایل و ئۆتۆماتیک بە زمانی کوردیی سۆرانی ئەنجام بدات بەبێ ئەوەی ڕووکارەکەی بە هەزاران دوگمە قەرەباڵغ بکرێت.",
                        fontSize = 13.sp,
                        color = BasokaTextPrimary,
                        lineHeight = 22.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showAboutDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
                ) {
                    Text("باشە")
                }
            }
        )
    }

    // Permissions Dialog
    if (showPermissionsDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionsDialog = false },
            containerColor = BasokaSurface,
            title = {
                Text(
                    text = "مۆڵەتەکان و ئاسایشی ئەندرۆید",
                    fontWeight = FontWeight.Bold,
                    color = BasokaTextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "• کامێرا: تەنها کاتێک بەکاردێت کە خۆت داوای بکەیت بۆ شیکاری وێنە یان فلاش.",
                        fontSize = 13.sp,
                        color = BasokaTextPrimary
                    )
                    Text(
                        text = "• دەنگ: مایک تەنها لە کاتی دەستنیشانکراودا کاردەکات و هیچ کات بە نهێنی تۆمار ناکات.",
                        fontSize = 13.sp,
                        color = BasokaTextPrimary
                    )
                    Text(
                        text = "• ئینتەرنێت: بۆ پەیوەندی بە ژیری دەستکردی Gemini و زمانی کوردی بەکاردێت.",
                        fontSize = 13.sp,
                        color = BasokaTextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPermissionsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
                ) {
                    Text("تێگەیشتم")
                }
            }
        )
    }
}

@Composable
fun SettingsCategoryHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = BasokaPrimary,
        modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp)
    )
}

@Composable
fun SettingsToggleCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = BasokaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(BasokaSurfaceElevated),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = BasokaPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = BasokaTextPrimary
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        color = BasokaTextSecondary,
                        lineHeight = 16.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = BasokaPrimary,
                    uncheckedTrackColor = BasokaSurfaceBorder
                )
            )
        }
    }
}

@Composable
fun SettingsClickableCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    titleColor: Color = BasokaTextPrimary,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = BasokaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BasokaSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (titleColor != BasokaTextPrimary) titleColor else BasokaPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = titleColor
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = BasokaTextSecondary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
