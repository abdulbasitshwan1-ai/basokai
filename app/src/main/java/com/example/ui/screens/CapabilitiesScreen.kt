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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
