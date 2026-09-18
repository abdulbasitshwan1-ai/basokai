package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.ai.KurdishCultureUtils
import com.example.ai.KurdishTranslatorService
import com.example.ui.BasokaViewModel
import com.example.ui.theme.BasokaBlack
import com.example.ui.theme.BasokaPrimary
import com.example.ui.theme.BasokaSecondary
import com.example.ui.theme.BasokaSurface
import com.example.ui.theme.BasokaSurfaceBorder
import com.example.ui.theme.BasokaSurfaceElevated
import com.example.ui.theme.BasokaTextPrimary
import com.example.ui.theme.BasokaTextSecondary

data class CapabilityModule(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val subFeaturesCount: Int,
    val samplePrompts: List<String>
)

@Composable
fun CapabilitiesScreen(
    viewModel: BasokaViewModel,
    onSelectPrompt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTranslatorDialog by remember { mutableStateOf(false) }
    var showCalendarDialog by remember { mutableStateOf(false) }
    var showDictionaryDialog by remember { mutableStateOf(false) }
    var showQuickAiToolsDialog by remember { mutableStateOf(false) }

    val modules = remember {
        listOf(
            CapabilityModule(
                title = "زیرەکی گشتی و وەڵامدانەوە",
                description = "وەڵامدانەوەی خێرا بە کوردیی سۆرانی، بیرکردنەوەی ژیرانە و شیتاڵکردنی بیرۆکەکان.",
                icon = Icons.Default.Psychology,
                subFeaturesCount = 120,
                samplePrompts = listOf(
                    "یارمەتیم بدە ڕۆژەکەم ڕێکبخەم",
                    "باشترین ستراتیژی بۆ چارەسەری کێشەکان چییە؟",
                    "ڕوونکردنەوەیەک لەسەر ژیری دەستکرد بدە"
                )
            ),
            CapabilityModule(
                title = "زمان و وەرگێڕان",
                description = "تێگەیشتن لە کوردیی ڕۆژانە، کورتکراوە، فەرمی و وەرگێڕانی دەق بۆ زمانەکانی تر.",
                icon = Icons.Default.Translate,
                subFeaturesCount = 85,
                samplePrompts = listOf(
                    "ئەم دەقەم بۆ وەرگێڕە بۆ کوردی",
                    "جیاوازی نێوان ئەم دوو دەستەواژەیە چییە؟",
                    "ئەم ڕستەیە بە ئینگلیزی چۆن دەگوترێت؟"
                )
            ),
            CapabilityModule(
                title = "نووسین و داڕشتن",
                description = "نووسینی ئیمەیڵ، نامە، داواکاری، سیڤی (CV)، کورتکردنەوە و چاککردنی هەڵەی ڕێنووس.",
                icon = Icons.Default.Edit,
                subFeaturesCount = 140,
                samplePrompts = listOf(
                    "ئیمەیڵێکی فەرمی داوای کارم بۆ بنووسە",
                    "ئەم دەقەم بۆ چاک بکە لە ڕووی ڕێنووسەوە",
                    "سیڤی (CV) ـیەکی ستانداردم بۆ دروست بکە"
                )
            ),
            CapabilityModule(
                title = "خوێندن و فێربوون",
                description = "پلانی خوێندنی تاقیکردنەوەکان، دروستکردنی پرسیار، کورتکردنەوەی وانە و هەڵسەنگاندنی وەڵام.",
                icon = Icons.Default.School,
                subFeaturesCount = 95,
                samplePrompts = listOf(
                    "بۆ تاقیکردنەوەکەم پلانی خوێندن دابنێ",
                    "لەسەر ئەم بابەتە ٢٠ پرسیارم بۆ دروست بکە",
                    "تاقیکردنەوەیەکم لێ بکە و وەڵامەکەم هەڵسەنگێنە"
                )
            ),
            CapabilityModule(
                title = "کۆد و گەشەپێدان",
                description = "دروستکردنی ئەپی Android، چارەسەری error، شیکاری کۆد و ڕوونکردنەوەی الگۆریتم بە زمانی کوردی.",
                icon = Icons.Default.Code,
                subFeaturesCount = 110,
                samplePrompts = listOf(
                    "بۆم ئەپێکی Android بە Jetpack Compose دروست بکە",
                    "ئەم error ـە بۆچی ڕوویداوە و چۆن چاکی بکەم؟",
                    "فەنکشنێکی هێنان و ناردنی داتا بە Kotlin بنووسە"
                )
            ),
            CapabilityModule(
                title = "فایل و بەڵگەنامەکان",
                description = "کورتکردنەوەی PDF، دەرهێنانی خاڵە سەرەکییەکان، بەراوردی دوو فایل و ڕێکخستنی دەق.",
                icon = Icons.Default.Description,
                subFeaturesCount = 75,
                samplePrompts = listOf(
                    "ئەم PDF ـە بۆم کورت بکەرەوە",
                    "گرنگترین خاڵەکانی ئەم فایلە چییە؟",
                    "ئەم بەڵگەنامەیە بۆم وەرگێڕە بۆ کوردی"
                )
            ),
            CapabilityModule(
                title = "دروستکردن و شیکاری وێنە (AI Art & Vision)",
                description = "دروستکردنی وێنە و تابلۆ بە ژیری دەستکرد، دەرهێنانی دەق لە وێنە، شیکاری screenshot و کێشانی نەخشەی هونەری.",
                icon = Icons.Default.Image,
                subFeaturesCount = 95,
                samplePrompts = listOf(
                    "وێنەی قەڵای هەولێر لە کاتی خۆرئاوابوون دروست بکە",
                    "وێنەی سروشتی شاخەکانی کوردستان لە بەهاردا بکێشە",
                    "دەقی ناو ئەم وێنەیە بۆم دەربهێنە"
                )
            ),
            CapabilityModule(
                title = "بیرخستنەوە و یادخەرەوە",
                description = "تێگەیشتنی کاتی سروشتی (ئەمڕۆ، سبەی، هەفتەی تر، هەموو ڕۆژ کاتژمێر ٨ی بەیانی).",
                icon = Icons.Default.NotificationsActive,
                subFeaturesCount = 90,
                samplePrompts = listOf(
                    "سبەی کاتژمێر ٨ بیرم بخەرەوە پەیوەندی بە هاوڕێکەم بکەم",
                    "هەر ڕۆژ ٨ی بەیانی بیرم بخەرەوە ئاوم بخۆمەوە",
                    "بیرم بخەرەوە سبەی کاتژمێر ٧ی بەیانی کتێب بخوێنمەوە"
                )
            ),
            CapabilityModule(
                title = "زەنگ و تایمەر",
                description = "دانانی زەنگی بێدارکەرەوە، تایمەری چەندین خولەکی و چاودێریکردنی کات بە دەقیقی.",
                icon = Icons.Default.Timer,
                subFeaturesCount = 60,
                samplePrompts = listOf(
                    "تایمەرێکی 10 خولەکی دابنێ",
                    "کاتژمێر ٦ی بەیانی زەنگم بۆ دابنێ",
                    "تایمەرەکە بوەستێنە"
                )
            ),
            CapabilityModule(
                title = "ڕۆژژمێر و کۆبوونەوەکان",
                description = "تۆمارکردنی چاوپێکەوتنەکان بە کات و ناونیشان و ناردنی ئاگاداری پێشوەختە بە ڕەزامەندی بەکارهێنەر.",
                icon = Icons.Default.Alarm,
                subFeaturesCount = 70,
                samplePrompts = listOf(
                    "سبەی کاتژمێر ٤ نیوەڕۆ کۆبوونەوەیەکم هەیە",
                    "کۆبوونەوەی هەفتەی داهاتووم بۆ تۆمار بکە"
                )
            ),
            CapabilityModule(
                title = "مۆڵەتەکانی مۆبایل و سیستەم",
                description = "فلاش، وایفای، بلوتوس، نەخشە، ڕێکخستن و کامێرا بەپێی ستانداردە پارێزراوەکانی ئەندرۆید.",
                icon = Icons.Default.PhoneAndroid,
                subFeaturesCount = 65,
                samplePrompts = listOf(
                    "فلاش بکەرەوە",
                    "Wi-Fi بکەرەوە",
                    "Bluetooth بکەرەوە",
                    "Settings بکەرەوە",
                    "Maps بکەرەوە"
                )
            ),
            CapabilityModule(
                title = "ئۆتۆماتیک و Workflow",
                description = "دروستکردنی زنجیرە کاری خودکار بە کوردی (بۆ نموونە کاتی خەو، کەمبوونەوەی باتری، پلانی بەیانیان).",
                icon = Icons.Default.SmartToy,
                subFeaturesCount = 80,
                samplePrompts = listOf(
                    "هەر شەو کاتژمێر 11 بیرم بخەرەوە بخەوم",
                    "هەر ڕۆژ 8ی بەیانی پلانەکەی ئەمڕۆم پیشان بدە",
                    "کاتێک کۆبوونەوەکەم تەواو بوو کارەکانم پیشان بدە"
                )
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BasokaBlack)
            .padding(horizontal = 16.dp)
            .testTag("capabilities_screen")
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = BasokaSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = BasokaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تواناکانی BASOKA (1000+)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = BasokaTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "فەلسەفەی BASOKA: «سادە لە دەرەوە، زۆر بەهێز لە ناوەوە». پێویست بە هەزاران دوگمە ناکات، تەنها بە کوردیی سروشتی فرمان بدە!",
                        fontSize = 13.sp,
                        color = BasokaTextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Access Interactive Tools Row
            Text(
                text = "ئامرازە ئامادەکراوەکانی ژیری دەستکرد و بەردەست بە ئۆفلاین:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BasokaPrimary,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Multi-Dialect Translator
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showTranslatorDialog = true },
                    color = BasokaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = BasokaPrimary, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("وەرگێڕی دیالێکت", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BasokaTextPrimary)
                    }
                }

                // AI Ready Tools (Email, CV, Summary)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showQuickAiToolsDialog = true },
                    color = BasokaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BasokaSecondary, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("ئامرازی AI خێرا", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BasokaTextPrimary)
                    }
                }

                // Offline Kurdish Calendar & Occasions
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showCalendarDialog = true },
                    color = BasokaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BasokaPrimary, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("ڕۆژژمێری کوردی", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BasokaTextPrimary)
                    }
                }

                // Offline Kurdish Dictionary
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showDictionaryDialog = true },
                    color = BasokaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = BasokaSecondary, modifier = Modifier.size(26.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("فەرهەنگی ئۆفلاین", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BasokaTextPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "کەتەلۆگی توانستە سەرەکییەکان:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = BasokaTextPrimary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        items(modules) { module ->
            CapabilityModuleItem(
                module = module,
                onPromptClick = onSelectPrompt
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // 1. Multi-Dialect Translator Dialog
    if (showTranslatorDialog) {
        MultiDialectTranslatorDialog(
            onDismiss = { showTranslatorDialog = false },
            onSendToChat = { prompt ->
                showTranslatorDialog = false
                onSelectPrompt(prompt)
            }
        )
    }

    // 2. Quick AI Tools Dialog (Email, CV, Summary)
    if (showQuickAiToolsDialog) {
        QuickAiToolsDialog(
            onDismiss = { showQuickAiToolsDialog = false },
            onSendToChat = { prompt ->
                showQuickAiToolsDialog = false
                onSelectPrompt(prompt)
            }
        )
    }

    // 3. Offline Kurdish Calendar Dialog
    if (showCalendarDialog) {
        KurdishCalendarDialog(
            onDismiss = { showCalendarDialog = false }
        )
    }

    // 4. Offline Kurdish Tech Dictionary Dialog
    if (showDictionaryDialog) {
        KurdishDictionaryDialog(
            onDismiss = { showDictionaryDialog = false }
        )
    }
}

@Composable
fun CapabilityModuleItem(
    module: CapabilityModule,
    onPromptClick: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { isExpanded = !isExpanded },
        color = BasokaSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, BasokaSurfaceBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
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
                            imageVector = module.icon,
                            contentDescription = null,
                            tint = BasokaPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = module.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = BasokaTextPrimary
                        )
                        Text(
                            text = "+${module.subFeaturesCount} تایبەتمەندی پەیوەندیدار",
                            fontSize = 11.sp,
                            color = BasokaSecondary
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = BasokaTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = module.description,
                fontSize = 12.sp,
                color = BasokaTextSecondary,
                lineHeight = 18.sp
            )

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "نموونەی فەرمانەکان (دەستی لێبدە بۆ ئەنجامدان):",
                        fontSize = 12.sp,
                        color = BasokaPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    module.samplePrompts.forEach { prompt ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onPromptClick(prompt) },
                            color = BasokaSurfaceElevated
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "💬 «$prompt»",
                                    fontSize = 12.sp,
                                    color = BasokaTextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// 1. Multi-Dialect Translator Dialog
@Composable
fun MultiDialectTranslatorDialog(
    onDismiss: () -> Unit,
    onSendToChat: (String) -> Unit
) {
    var sourceText by remember { mutableStateOf("") }
    var fromDialect by remember { mutableStateOf("سۆرانی") }
    var toDialect by remember { mutableStateOf("بادینی") }
    var translatedResult by remember { mutableStateOf("") }
    var grammarTips by remember { mutableStateOf<List<String>>(emptyList()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BasokaSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = BasokaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("وەرگێڕی شێوەزارەکانی کوردی", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BasokaTextPrimary)
            }
        },
        text = {
            Column {
                Text("دەق بنووسە بۆ وەرگێڕانی نێوان دیالێکتەکانی زمانی کوردی:", fontSize = 12.sp, color = BasokaTextSecondary)
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("لە: $fromDialect", fontSize = 12.sp, color = BasokaPrimary, fontWeight = FontWeight.Bold)
                    Button(
                        onClick = {
                            val temp = fromDialect
                            fromDialect = toDialect
                            toDialect = temp
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BasokaSurfaceElevated),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("گۆڕین ⇄", fontSize = 11.sp, color = BasokaTextPrimary)
                    }
                    Text("بۆ: $toDialect", fontSize = 12.sp, color = BasokaSecondary, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sourceText,
                    onValueChange = {
                        sourceText = it
                        if (it.isNotBlank()) {
                            translatedResult = KurdishTranslatorService.translateDialect(it, fromDialect, toDialect)
                            grammarTips = KurdishTranslatorService.checkKurdishGrammar(it)
                        } else {
                            translatedResult = ""
                            grammarTips = emptyList()
                        }
                    },
                    placeholder = { Text("دەق لێرە بنووسە (وەک: چۆنی، ئەڤرۆ، باشم)...", fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BasokaPrimary,
                        unfocusedBorderColor = BasokaSurfaceBorder
                    ),
                    maxLines = 3
                )

                if (translatedResult.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = BasokaSurfaceElevated,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("ئەنجامی وەرگێڕان:", fontSize = 11.sp, color = BasokaPrimary, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(translatedResult, fontSize = 14.sp, color = BasokaTextPrimary, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                if (grammarTips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("تێبینیی ڕێنووس:", fontSize = 11.sp, color = BasokaSecondary, fontWeight = FontWeight.Bold)
                    grammarTips.forEach { tip ->
                        Text("• $tip", fontSize = 11.sp, color = BasokaTextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (sourceText.isNotBlank()) {
                        onSendToChat("ئەم دەقەم بۆ وەرگێڕە لە $fromDialect بۆ $toDialect و ڕێنووسەکەی شی بکەرەوە:\n$sourceText")
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
            ) {
                Text(if (sourceText.isNotBlank()) "ناردن بۆ چاتی AI" else "داخستن")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("داخستن", color = BasokaTextSecondary)
            }
        }
    )
}

// 2. Quick AI Tools Dialog (Email, CV, Summary)
@Composable
fun QuickAiToolsDialog(
    onDismiss: () -> Unit,
    onSendToChat: (String) -> Unit
) {
    var selectedTool by remember { mutableStateOf(0) }
    val toolTitles = listOf("نووسینی ئیمەیڵ", "دروستکردنی CV", "کورتکردنەوە")
    var inputDetail by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BasokaSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BasokaSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ئامرازە ئامادەکراوەکانی ژیری دەستکرد", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BasokaTextPrimary)
            }
        },
        text = {
            Column {
                TabRow(
                    selectedTabIndex = selectedTool,
                    containerColor = BasokaSurfaceElevated,
                    contentColor = BasokaPrimary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTool]),
                            color = BasokaPrimary
                        )
                    }
                ) {
                    toolTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTool == index,
                            onClick = { selectedTool = index },
                            text = { Text(title, fontSize = 11.sp, maxLines = 1) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                when (selectedTool) {
                    0 -> {
                        Text("بابەتی ئیمەیڵەکە یان داواکارییەکەت چییە؟", fontSize = 12.sp, color = BasokaTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = inputDetail,
                            onValueChange = { inputDetail = it },
                            placeholder = { Text("وەک: داوای مۆڵەتی پشوودان لە بەڕێوەبەر بۆ دوو ڕۆژ", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                    1 -> {
                        Text("پیشە و تایبەتمەندییە سەرەکییەکانت چییە بۆ CV؟", fontSize = 12.sp, color = BasokaTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = inputDetail,
                            onValueChange = { inputDetail = it },
                            placeholder = { Text("وەک: پەرەپێدەری ئەندرۆید، ٢ ساڵ ئەزموون لە کۆتلین و Jetpack Compose", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                    }
                    2 -> {
                        Text("دەقی درێژ دابنێ بۆ کورتکردنەوەی پڕۆفیشناڵ:", fontSize = 12.sp, color = BasokaTextSecondary)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = inputDetail,
                            onValueChange = { inputDetail = it },
                            placeholder = { Text("دەق لێرە دابنێ...", fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 4
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val prompt = when (selectedTool) {
                        0 -> "ئیمەیڵێکی فەرمی و جوانم بۆ بنووسە بە کوردی سەبارەت بە: ${inputDetail.ifEmpty { "داوای کارکردن لە کۆمپانیا" }}"
                        1 -> "سیڤییەکی (CV) ستاندارد و پڕۆفیشناڵم بۆ دابڕێژە بەم زانیارییانە: ${inputDetail.ifEmpty { "پەرەپێدەری سۆفتوێر" }}"
                        else -> "ئەم دەقەم بۆ کورت بکەرەوە بە کوردی لە ٥ خاڵی گرنگدا:\n$inputDetail"
                    }
                    onSendToChat(prompt)
                },
                colors = ButtonDefaults.buttonColors(containerColor = BasokaPrimary)
            ) {
                Text("دروستکردن بە AI")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("داخستن", color = BasokaTextSecondary)
            }
        }
    )
}

// 3. Offline Kurdish Calendar & Occasions Dialog
@Composable
fun KurdishCalendarDialog(onDismiss: () -> Unit) {
    val info = remember { KurdishCultureUtils.getTodayKurdishInfo() }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BasokaSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = BasokaPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ڕۆژژمێری کوردی و کاتەکانی بانگ", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BasokaTextPrimary)
            }
        },
        text = {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BasokaSurfaceElevated,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("ڕۆژژمێری کوردی:", fontSize = 11.sp, color = BasokaPrimary, fontWeight = FontWeight.Bold)
                        Text(info.kurdishDate, fontSize = 14.sp, color = BasokaTextPrimary, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(info.gregorianDate, fontSize = 12.sp, color = BasokaTextSecondary)
                        Text(info.hijriDate, fontSize = 12.sp, color = BasokaTextSecondary)
                    }
                }

                if (info.specialOccasion != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = BasokaPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(info.specialOccasion, fontSize = 12.sp, color = BasokaPrimary, modifier = Modifier.padding(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text("کاتەکانی بانگ (کوردستان):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BasokaTextPrimary)
                Spacer(modifier = Modifier.height(6.dp))

                info.prayerTimes.forEach { (name, time) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(name, fontSize = 12.sp, color = BasokaTextSecondary)
                        Text(time, fontSize = 12.sp, color = BasokaSecondary, fontWeight = FontWeight.SemiBold)
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

// 4. Offline Kurdish Tech Dictionary Dialog
@Composable
fun KurdishDictionaryDialog(onDismiss: () -> Unit) {
    var searchWord by remember { mutableStateOf("") }
    val words = KurdishCultureUtils.offlineDictionary.filter {
        it.first.contains(searchWord, ignoreCase = true) || it.second.contains(searchWord, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = BasokaSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MenuBook, contentDescription = null, tint = BasokaSecondary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("فەرهەنگی تەکنەلۆژیی کوردی (ئۆفلاین)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = BasokaTextPrimary)
            }
        },
        text = {
            Column(modifier = Modifier.height(320.dp)) {
                OutlinedTextField(
                    value = searchWord,
                    onValueChange = { searchWord = it },
                    placeholder = { Text("گەڕان بە ئینگلیزی یان کوردی...", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(words) { (en, ku) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            color = BasokaSurfaceElevated,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(en, fontSize = 12.sp, color = BasokaPrimary, fontWeight = FontWeight.Bold)
                                Text(ku, fontSize = 13.sp, color = BasokaTextPrimary)
                            }
                        }
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
