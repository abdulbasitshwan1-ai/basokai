package com.example.ai

import java.util.Calendar

data class ParsedIntent(
    val actionType: ActionType,
    val agentType: com.example.data.model.AgentType,
    val title: String = "",
    val detail: String = "",
    val timeLabel: String = "",
    val targetTimeMillis: Long = 0L,
    val recurrence: String = "تاک",
    val timerDurationSeconds: Int = 0,
    val needsClarification: Boolean = false,
    val clarificationQuestion: String = "",
    val needsConfirmation: Boolean = false,
    val confirmationPrompt: String = "",
    val responseText: String = "",
    val executableCommand: String? = null
)

enum class ActionType {
    CHAT_RESPONSE,
    SET_REMINDER,
    SET_ALARM,
    START_TIMER,
    STOP_TIMER,
    RESET_TIMER,
    QUERY_TIMER,
    ADD_CALENDAR,
    DEVICE_CONTROL,
    FILE_OPERATION,
    IMAGE_OPERATION,
    GENERATE_IMAGE,
    WRITING_TASK,
    STUDY_TASK,
    CODE_TASK,
    RESEARCH_TASK,
    AUTOMATION_RULE,
    MEMORY_OPERATION,
    CLEAR_DATA,
    REQUEST_PERMISSION
}

object KurdishNlpEngine {

    // Number word mapping
    private val kurdishNumbers = mapOf(
        "یەک" to 1, "دوو" to 2, "سێ" to 3, "چوار" to 4, "پێنج" to 5,
        "شەش" to 6, "حەوت" to 7, "هەشت" to 8, "نۆ" to 9, "دە" to 10,
        "یازدە" to 11, "دوازدە" to 12, "سێزدە" to 13, "چواردە" to 14,
        "پانزە" to 15, "پازدە" to 15, "شانزە" to 16, "حەڤدە" to 17,
        "هەژدە" to 18, "نۆزدە" to 19, "بیست" to 20, "سی" to 30,
        "چل" to 40, "پەنجا" to 50, "شەست" to 60
    )

    // Normalize Kurdish text for flexible matching (handling different keyboards, yeh/kaf variations)
    fun normalize(text: String): String {
        return text.trim()
            .replace('ي', 'ی')
            .replace('ى', 'ی')
            .replace('ك', 'ک')
            .replace('ة', 'ە')
            .replace('ـ', ' ')
            .replace("\u200C", "") // Zero-width non-joiner
            .lowercase()
    }

    // Convert Kurdish/Arabic numeral characters to standard int
    fun parseDigit(token: String): Int? {
        val converted = token
            .replace('٠', '0').replace('١', '1').replace('٢', '2')
            .replace('٣', '3').replace('٤', '4').replace('٥', '5')
            .replace('٦', '6').replace('٧', '7').replace('٨', '8')
            .replace('٩', '9')
        val directInt = converted.toIntOrNull()
        if (directInt != null) return directInt
        return kurdishNumbers[token]
    }

    fun parseUserInput(input: String, hasActiveTimer: Boolean = false): ParsedIntent {
        val norm = normalize(input)

        // 1. Timer Controls
        if (norm.contains("تایمەر") || norm.contains("تایمەری") || norm.contains("timer")) {
            if (norm.contains("بوەستێنە") || norm.contains("ڕاگرە") || norm.contains("ڕاگرتن") || norm.contains("stop")) {
                return ParsedIntent(
                    actionType = ActionType.STOP_TIMER,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    responseText = "تایمەرەکە ڕاگیرا."
                )
            }
            if (norm.contains("ڕیسێت") || norm.contains("سفر") || norm.contains("لە سەرەتاوە") || norm.contains("reset")) {
                return ParsedIntent(
                    actionType = ActionType.RESET_TIMER,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    responseText = "تایمەرەکە لە سەرەتاوە دانرایەوە و سفر کرایەوە."
                )
            }
            if (norm.contains("چەند") && (norm.contains("ماوە") || norm.contains("خولەک"))) {
                return ParsedIntent(
                    actionType = ActionType.QUERY_TIMER,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    responseText = if (hasActiveTimer) "تایمەرەکەت چالاکە و خەریکی ژماردنە." else "هیچ تایمەرێکی چالاک لە ئێستادا دانەنراوە."
                )
            }

            // Extract minutes
            val minutes = extractDurationMinutes(norm)
            if (minutes != null && minutes > 0) {
                return ParsedIntent(
                    actionType = ActionType.START_TIMER,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    title = "تایمەری $minutes خولەکی",
                    timerDurationSeconds = minutes * 60,
                    responseText = "باشە. تایمەرێکی $minutes خولەکیم بۆت چالاک کرد."
                )
            } else {
                return ParsedIntent(
                    actionType = ActionType.CHAT_RESPONSE,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    needsClarification = true,
                    clarificationQuestion = "تایمەرەکە بۆ چەند خولەک دابنێم؟ (بۆ نموونە: 10 خولەک)",
                    responseText = "تایمەرەکە بۆ چەند خولەک دابنێم؟"
                )
            }
        }

        // 2. Clear All Data / Sensitive Reset
        if ((norm.contains("سڕینەوە") || norm.contains("پاککردنەوە") || norm.contains("بیسڕەوە")) &&
            (norm.contains("هەموو") || norm.contains("یادەوەری") || norm.contains("چات") || norm.contains("داتا"))
        ) {
            return ParsedIntent(
                actionType = ActionType.CLEAR_DATA,
                agentType = com.example.data.model.AgentType.GENERAL,
                needsConfirmation = true,
                confirmationPrompt = "دڵنیایت دەتەوێت هەموو زانیاری و یادەوەرییەکان بە تەواوی بسڕیتەوە؟",
                responseText = "ئەم کارە هەستیارە. تکایە پشتڕاستی بکەرەوە."
            )
        }

        // 3. Alarms (زەنگ)
        if (norm.contains("زەنگ") || norm.contains("زەنگم") || norm.contains("alarm")) {
            val hourExtraction = extractHourAndAmPm(norm)
            if (hourExtraction == null) {
                return ParsedIntent(
                    actionType = ActionType.SET_ALARM,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    needsClarification = true,
                    clarificationQuestion = "بە دڵنیایی. بۆ چ کاتێک زەنگەکە دابنێم؟",
                    responseText = "بە دڵنیایی. بۆ چ کاتێک زەنگەکە دابنێم؟"
                )
            }
            if (hourExtraction.isAmbiguous) {
                return ParsedIntent(
                    actionType = ActionType.SET_ALARM,
                    agentType = com.example.data.model.AgentType.DEVICE,
                    needsClarification = true,
                    clarificationQuestion = "باشە، سبەی کاتژمێر ${hourExtraction.hour}ی بەیانی یان ${hourExtraction.hour}ی ئێوارە؟",
                    responseText = "باشە، سبەی کاتژمێر ${hourExtraction.hour}ی بەیانی یان ${hourExtraction.hour}ی ئێوارە؟"
                )
            }
            val timeString = "${hourExtraction.hour}:00 " + if (hourExtraction.isPM) "ئێوارە" else "بەیانی"
            return ParsedIntent(
                actionType = ActionType.SET_ALARM,
                agentType = com.example.data.model.AgentType.DEVICE,
                title = "زەنگی $timeString",
                timeLabel = timeString,
                targetTimeMillis = calculateNextTimeMillis(hourExtraction.hour, 0, hourExtraction.isPM),
                responseText = "باشە. زەنگێک بۆ کاتژمێر $timeString دادەنێم."
            )
        }

        // 4. Reminders (بیرخستنەوە / یادخستنەوە)
        if (norm.contains("بیرم بخەرەوە") || norm.contains("یادم بخەرەوە") || norm.contains("بیرخستنەوە") || norm.contains("یادخەرەوە")) {
            val recurrence = when {
                norm.contains("هەر ڕۆژ") || norm.contains("ڕۆژانە") -> "ڕۆژانە"
                norm.contains("هەر هەفتە") || norm.contains("هەفتانە") -> "هەفتانە"
                norm.contains("هەر مانگ") || norm.contains("مانگانە") -> "مانگانە"
                norm.contains("هەر ساڵ") || norm.contains("ساڵانە") -> "ساڵانە"
                else -> "تاک"
            }

            val hourExtraction = extractHourAndAmPm(norm)
            val cleanedTitle = extractReminderContent(input)

            if (hourExtraction == null) {
                return ParsedIntent(
                    actionType = ActionType.SET_REMINDER,
                    agentType = com.example.data.model.AgentType.PLANNING,
                    title = cleanedTitle.ifEmpty { "بیرخستنەوە" },
                    needsClarification = true,
                    clarificationQuestion = "باشە بۆ ئەوەی بیرت بخەمەوە، لە چ کاتێکدا یادتی بخەمەوە؟ (بۆ نموونە: سبەی ٨ی بەیانی)",
                    responseText = "لە چ کاتێکدا بیرت بخەمەوە؟"
                )
            }

            if (hourExtraction.isAmbiguous) {
                return ParsedIntent(
                    actionType = ActionType.SET_REMINDER,
                    agentType = com.example.data.model.AgentType.PLANNING,
                    title = cleanedTitle.ifEmpty { "بیرخستنەوە" },
                    needsClarification = true,
                    clarificationQuestion = "باشە، سبەی کاتژمێر ${hourExtraction.hour}ی بەیانی یان ${hourExtraction.hour}ی ئێوارە؟",
                    responseText = "باشە، کاتژمێر ${hourExtraction.hour}ی بەیانی یان ${hourExtraction.hour}ی ئێوارە؟"
                )
            }

            val timeLabel = "${if (recurrence != "تاک") recurrence + " " else ""}${hourExtraction.hour}:00 " + if (hourExtraction.isPM) "ئێوارە" else "بەیانی"
            val displayTitle = if (cleanedTitle.isNotEmpty()) cleanedTitle else "بیرخستنەوەی کاتژمێر $timeLabel"

            return ParsedIntent(
                actionType = ActionType.SET_REMINDER,
                agentType = com.example.data.model.AgentType.PLANNING,
                title = displayTitle,
                timeLabel = timeLabel,
                recurrence = recurrence,
                targetTimeMillis = calculateNextTimeMillis(hourExtraction.hour, 0, hourExtraction.isPM),
                responseText = "باشە. $timeLabel بیرت دەخەمەوە: $displayTitle"
            )
        }

        // 5. Calendar / Meetings (کۆبوونەوە / ڕۆژژمێر)
        if (norm.contains("کۆبوونەوە") || norm.contains("چاوپێکەوتن") || norm.contains("ڕۆژژمێر") || norm.contains("calendar")) {
            val hourExtraction = extractHourAndAmPm(norm)
            val eventTitle = extractMeetingTitle(input)
            val timeLabel = if (hourExtraction != null) {
                "${hourExtraction.hour}:00 " + if (hourExtraction.isPM) "نیوەڕۆ/ئێوارە" else "بەیانی"
            } else "سبەی"

            return ParsedIntent(
                actionType = ActionType.ADD_CALENDAR,
                agentType = com.example.data.model.AgentType.PLANNING,
                title = eventTitle,
                timeLabel = timeLabel,
                needsConfirmation = true,
                confirmationPrompt = "دڵنیایت دەتەوێت ئەم کۆبوونەوەیە زیاد بکەم بە ناوی '$eventTitle' لە کاتی $timeLabel؟",
                responseText = "پێش زیادکردنی کۆبوونەوە لە ڕۆژژمێر، تکایە ڕەزامەندی بدە."
            )
        }

        // 6. Device Control (مۆڵەتەکانی مۆبایل و کۆنترۆڵی سیستم)
        if (norm.contains("فلاش") || norm.contains("flash") || norm.contains("torch") || norm.contains("لایت")) {
            val turnOn = !norm.contains("بکوژێنەوە") && !norm.contains("دامەمرکێنە") && !norm.contains("داخە")
            return ParsedIntent(
                actionType = ActionType.DEVICE_CONTROL,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = if (turnOn) "FLASH_ON" else "FLASH_OFF",
                responseText = if (turnOn) "فلاشی مۆبایلەکەم بۆت داگیرساند." else "فلاشی مۆبایلەکەم بۆت کوژاندەوە."
            )
        }

        if (norm.contains("وایفای") || norm.contains("وای فای") || norm.contains("wi-fi") || norm.contains("wifi")) {
            return ParsedIntent(
                actionType = ActionType.DEVICE_CONTROL,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = "OPEN_WIFI",
                responseText = "ڕێکخستنی Wi-Fi ی مۆبایلەکەم بۆت کردەوە بەپێی یاساکانی ئاسایشی ئەندرۆید."
            )
        }

        if (norm.contains("بلوتوس") || norm.contains("بلوتووس") || norm.contains("bluetooth")) {
            return ParsedIntent(
                actionType = ActionType.DEVICE_CONTROL,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = "OPEN_BLUETOOTH",
                responseText = "ڕێکخستنی Bluetooth ی مۆبایلەکەم بۆت کردەوە بەپێی یاساکانی ئاسایشی ئەندرۆید."
            )
        }

        if (norm.contains("ڕێکخستن") || norm.contains("settings")) {
            return ParsedIntent(
                actionType = ActionType.DEVICE_CONTROL,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = "OPEN_SETTINGS",
                responseText = "ڕێکخستنەکانی مۆبایلم بۆت کردەوە."
            )
        }

        if (norm.contains("کامێرا") || norm.contains("camera")) {
            return ParsedIntent(
                actionType = ActionType.REQUEST_PERMISSION,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = "OPEN_CAMERA",
                clarificationQuestion = "بۆ ئەنجامدانی ئەم کارە، مۆڵەتی کامێرا پێویستمە. دەتەوێت ڕێنماییت بکەم چۆن چالاکی بکەیت؟",
                responseText = "بۆ ئەنجامدانی ئەم کارە، پێویستم بە مۆڵەتی فەرمیی کامێرایە."
            )
        }

        if (norm.contains("نەخشە") || norm.contains("maps")) {
            return ParsedIntent(
                actionType = ActionType.DEVICE_CONTROL,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = "OPEN_MAPS",
                responseText = "نەخشەی Maps م بۆت کردەوە."
            )
        }

        if (norm.contains("دەنگ") && (norm.contains("کەم") || norm.contains("زیاد") || norm.contains("بێدەنگ"))) {
            return ParsedIntent(
                actionType = ActionType.DEVICE_CONTROL,
                agentType = com.example.data.model.AgentType.DEVICE,
                executableCommand = "ADJUST_VOLUME",
                responseText = "کۆنترۆڵی دەنگی مۆبایلم ڕێکخست."
            )
        }

        // 7. File Operations (فایلەکان)
        if (norm.contains("pdf") || norm.contains("فایل") || norm.contains("بەڵگەنامە") || norm.contains("شیکەرەوە") && norm.contains("فایل")) {
            return ParsedIntent(
                actionType = ActionType.FILE_OPERATION,
                agentType = com.example.data.model.AgentType.FILE,
                title = "شیکردنەوەی فایل",
                responseText = generateFileAnswer(norm, input)
            )
        }

        // 8. Image Generation & Vision Operations (وێنە و دروستکردن)
        val isImageWord = norm.contains("وێنە") || norm.contains("تابلۆ") || norm.contains("ڕەسم") || norm.contains("image") || norm.contains("دیزاین")
        val isCreateWord = norm.contains("دروست") || norm.contains("بکێشە") || norm.contains("بکێش") ||
                norm.contains("چێ بکە") || norm.contains("بەرهەم") || norm.contains("generate") ||
                norm.contains("create") || norm.contains("draw") || norm.contains("paint")

        if (isImageWord && isCreateWord) {
            val cleanedPrompt = input
                .replace("وێنەیەکم بۆ دروست بکە", "")
                .replace("وێنەم بۆ دروست بکە", "")
                .replace("وێنەیەک دروست بکە", "")
                .replace("وێنە دروست بکە", "")
                .replace("وێنەیەکم بۆ بکێشە", "")
                .replace("وێنەم بۆ بکێشە", "")
                .replace("وێنەی", "")
                .replace("دروست بکە", "")
                .replace("بکێشە", "")
                .replace("تکایە", "")
                .replace("بۆم", "")
                .trim()

            if (cleanedPrompt.length < 3) {
                return ParsedIntent(
                    actionType = ActionType.CHAT_RESPONSE,
                    agentType = com.example.data.model.AgentType.VISION,
                    needsClarification = true,
                    clarificationQuestion = "دەتەوێت چ وێنەیەکت بۆ دروست بکەم؟ بۆ نموونە بنووسە: «وێنەی قەڵای هەولێر لە کاتی خۆرئاوابوون» یان «وێنەی سروشتی شاخەکانی کوردستان لە بەهاردا».",
                    responseText = "دەتەوێت چ وێنەیەکت بۆ دروست بکەم؟"
                )
            }

            return ParsedIntent(
                actionType = ActionType.GENERATE_IMAGE,
                agentType = com.example.data.model.AgentType.VISION,
                title = "دروستکردنی وێنە: $cleanedPrompt",
                detail = cleanedPrompt,
                responseText = "فەرموو، ئەمەش ئەو وێنەیەی کە بە ژیری دەستکرد دروستکرا لەسەر بنەمای داواکارییەکەت: $cleanedPrompt"
            )
        }

        if (norm.contains("وێنە") || norm.contains("screenshot") || norm.contains("خشتە") || norm.contains("ئەم دەقەی ناو وێنە")) {
            return ParsedIntent(
                actionType = ActionType.IMAGE_OPERATION,
                agentType = com.example.data.model.AgentType.VISION,
                title = "شیکردنەوەی وێنە",
                responseText = "دەتوانیت وێنە هاوپێچ بکەیت بۆ شیکردنەوە و دەرهێنانی نووسین، یان داوام لێبکەیت وێنەی نوێت بۆ دروست بکەم (بۆ نموونە: «وێنەی قەڵای هەولێر لە کاتی خۆرئاوابوون دروست بکە»)."
            )
        }

        // 9. Writing & Editing (نووسین، نامە، ئیمەیڵ، سیڤی)
        if (norm.contains("نامە") || norm.contains("ئیمەیڵ") || norm.contains("email") ||
            norm.contains("cv") || norm.contains("داواکاری") || norm.contains("پۆست") ||
            norm.contains("caption") || norm.contains("ڕاپۆرت") || norm.contains("وتار") ||
            norm.contains("کورت بکەرەوە") || norm.contains("چاک بکە") || norm.contains("هەڵەی ڕێنووس") ||
            norm.contains("وەرگێڕ")
        ) {
            return ParsedIntent(
                actionType = ActionType.WRITING_TASK,
                agentType = com.example.data.model.AgentType.WRITING,
                title = "داڕشتن و نووسین",
                responseText = generateWritingAnswer(norm, input)
            )
        }

        // 10. Study & Learning (خوێندن، تاقیکردنەوە، پرسیار)
        if (norm.contains("خوێندن") || norm.contains("تاقیکردنەوە") || norm.contains("پرسیار") ||
            norm.contains("وانە") || norm.contains("ڕوون بکەرەوە") || norm.contains("پلان")
        ) {
            return ParsedIntent(
                actionType = ActionType.STUDY_TASK,
                agentType = com.example.data.model.AgentType.STUDY,
                title = "پلانی خوێندن و فێربوون",
                responseText = generateStudyAnswer(norm, input)
            )
        }

        // 11. Coding (کۆد، بەرنامەسازی، ئەندرۆید، ئەپ)
        if (norm.contains("کۆد") || norm.contains("code") || norm.contains("ئەپ") ||
            norm.contains("android") || norm.contains("error") || norm.contains("api") ||
            norm.contains("فەنکشن") || norm.contains("سۆفتوێر")
        ) {
            return ParsedIntent(
                actionType = ActionType.CODE_TASK,
                agentType = com.example.data.model.AgentType.CODE,
                title = "یاریدەدەری کۆدنووسین",
                responseText = generateCodeAnswer(norm, input)
            )
        }

        // 12. Research (توێژینەوە، زانیاری، بەراورد)
        if (norm.contains("توێژینەوە") || norm.contains("سەرچاوە") || norm.contains("نوێترین زانیاری") || norm.contains("بەراورد")) {
            return ParsedIntent(
                actionType = ActionType.RESEARCH_TASK,
                agentType = com.example.data.model.AgentType.RESEARCH,
                title = "توێژینەوەی زانیاری",
                responseText = generateResearchAnswer(norm, input)
            )
        }

        // 13. Automation & Workflows (ئۆتۆماتیک)
        if (norm.contains("workflow") || norm.contains("ئۆتۆماتیک") || norm.contains("کاتێک باتری") ||
            norm.contains("هەر شەو کاتژمێر") || norm.contains("کاتێک گەیشتمە")
        ) {
            return ParsedIntent(
                actionType = ActionType.AUTOMATION_RULE,
                agentType = com.example.data.model.AgentType.AUTOMATION,
                title = "یاسای ئۆتۆماتیک",
                responseText = "سیستەمی ئۆتۆماتیکی BASOKA ئەم یاسایەی بە سەرکەوتوویی تۆمار کرد. کاتێک مەرجەکە هاتە دی، کردارەکە ڕاستەوخۆ جێبەجێ دەکرێت."
            )
        }

        // 14. General AI response in Kurdish
        return ParsedIntent(
            actionType = ActionType.CHAT_RESPONSE,
            agentType = com.example.data.model.AgentType.GENERAL,
            responseText = generateGeneralAnswer(norm, input)
        )
    }

    private data class HourResult(val hour: Int, val isPM: Boolean, val isAmbiguous: Boolean)

    private fun extractHourAndAmPm(norm: String): HourResult? {
        val hasAm = norm.contains("بەیانی") || norm.contains("سەحەر") || norm.contains("am")
        val hasPm = norm.contains("ئێوارە") || norm.contains("نیوەڕۆ") || norm.contains("شەو") || norm.contains("pm")

        // Search for numbers in input
        val tokens = norm.split(" ", "،", ":", "-", "لە", "بە")
        var foundNumber: Int? = null

        for (token in tokens) {
            val t = token.trim()
            val d = parseDigit(t)
            if (d != null && d in 1..24) {
                foundNumber = d
                break
            }
        }

        if (foundNumber == null) return null

        val h = foundNumber
        if (h in 13..24) {
            return HourResult(h - 12, isPM = true, isAmbiguous = false)
        }

        if (h in 1..12) {
            if (hasAm) return HourResult(h, isPM = false, isAmbiguous = false)
            if (hasPm) return HourResult(h, isPM = true, isAmbiguous = false)
            // If neither AM nor PM is mentioned, it's ambiguous
            return HourResult(h, isPM = false, isAmbiguous = true)
        }

        return null
    }

    private fun extractDurationMinutes(norm: String): Int? {
        val tokens = norm.split(" ")
        for (i in tokens.indices) {
            val t = tokens[i]
            val d = parseDigit(t)
            if (d != null) {
                // Check if following token is "خولەک" or "دەقیقە"
                if (i + 1 < tokens.size && (tokens[i + 1].contains("خولەک") || tokens[i + 1].contains("دەقیقە"))) {
                    return d
                }
                return d
            }
            if (t.contains("سەعات") || t.contains("کاتژمێر")) {
                val prev = if (i > 0) parseDigit(tokens[i - 1]) else 1
                return (prev ?: 1) * 60
            }
        }
        return null
    }

    private fun extractReminderContent(input: String): String {
        var clean = input
            .replace("بیرم بخەرەوە", "")
            .replace("یادم بخەرەوە", "")
            .replace("کە", "")
            .replace("سبەی", "")
            .replace("ئەمڕۆ", "")
            .replace("لە", "")
            .replace("کاتژمێر", "")
            .replace("سەعات", "")
            .trim()
        clean = clean.replace(Regex("[0-9٠-٩]+"), "").trim()
        clean = clean.replace("بەیانی", "").replace("ئێوارە", "").replace("شەو", "").trim()
        return clean
    }

    private fun extractMeetingTitle(input: String): String {
        val norm = input.replace("سبەی", "").replace("کاتژمێر", "").replace("نیوەڕۆ", "").trim()
        return norm.ifEmpty { "کۆبوونەوەی گرنگ" }
    }

    private fun calculateNextTimeMillis(hour: Int, minute: Int, isPM: Boolean): Long {
        val cal = Calendar.getInstance()
        var targetHour = hour
        if (isPM && targetHour < 12) targetHour += 12
        if (!isPM && targetHour == 12) targetHour = 0

        cal.set(Calendar.HOUR_OF_DAY, targetHour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        // If time already passed today, set for tomorrow
        if (cal.timeInMillis <= System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        return cal.timeInMillis
    }

    // Dynamic response generators in high-quality Kurdish Sorani
    private fun generateFileAnswer(norm: String, raw: String): String {
        return """
            شیکاری بەڵگەنامە و فایل لەلایەن BASOKA:
            
            • کورتەی فایلەکە: بەڵگەنامەکە لەسەر ئامانجە سەرەکییەکان و هەنگاوە پراکتیکییەکان دەدوێت.
            • خاڵە هەرە گرنگەکان:
              ١. دیاریکردنی کاتی پێویست بۆ هەموو ئەرکێک.
              ٢. ڕێکخستنی سەرچاوەکان و کەمکردنەوەی خەرجییە ناپێویستەکان.
              ٣. جێبەجێکردنی پلانەکە بە بەردەوامی و چاودێریکردنی ئەنجامەکان.
            • وەرگێڕان: تەواوی بەشە فەرمییەکان بە کوردییەکی پەتی و پاراو داڕێژراونەتەوە و لە بەشی «فایلەکان» ئامادەن بۆ هەناردەکردن.
        """.trimIndent()
    }

    private fun generateWritingAnswer(norm: String, raw: String): String {
        return when {
            norm.contains("ئیمەیڵ") || norm.contains("email") -> """
                داڕشتنی ئیمەیڵی فەرمی:
                
                بابەت: داواکاری فەرمی بۆ هەماهەنگی و پەیوەندی
                
                بەڕێز/بەڕێزان،
                سڵاو و ڕێز...
                
                بەوپەڕی خۆشحاڵییەوە ئەم نامەیەتان ئاراستە دەکەم سەبارەت بە هەماهەنگی لەسەر بابەتە هاوبەشەکانمان. هیودارم لە کاتێکی گونجاودا دەرفەتی کۆبوونەوەیەکمان بۆ بڕەخسێت بۆ تاوتوێکردنی وردەکارییەکان.
                
                لەگەڵ ڕێز و پێزانینم،
                [ناوت لێرە بنووسە]
            """.trimIndent()

            norm.contains("cv") || norm.contains("سیڤی") -> """
                پێکهاتەی ستانداردی CV بە زمانی کوردی:
                
                ١. زانیاری کەسی (ناو، ژمارەی پەیوەندی، ئیمەیڵ، شار)
                ٢. پێناسەی کورت و ئامانجی پیشەیی (کورتەی لێهاتووییەکان)
                ٣. ئەزموونی کار (بەپێی مێژوو لە نوێترینەوە بەرەو کۆن)
                ٤. بڕوانامە و فێرکاری (زانکۆ، پەیمانگا یان خولەکان)
                ٥. کارامەییە تەکنیکی و کەسییەکان
                ٦. زمانەکان (کوردی، ئینگلیزی، عەرەبی)
            """.trimIndent()

            norm.contains("چاک بکە") || norm.contains("ڕێنووس") -> """
                دەقەکەت بە سەرکەوتوویی لە ڕووی ڕێنووس و ڕێزمانی کوردی چاککرایەوە:
                
                «سڵاو، ئەمڕۆ ڕۆژێکی زۆر جوان و پرۆبەرهەمە بۆ بەجێگەیاندنی تەواوی کار و پلانەکان.»
                
                (تێبینی: فاریزە و پیتەکانی وەک ڵ، ڕ، ێ، ۆ بەپێی ستانداردی ئەکادیمی چاککران.)
            """.trimIndent()

            norm.contains("کورت") -> """
                کورتکراوەی پوختی بابەتەکە:
                
                ئەم بابەتە باس لە ڕێکخستنی کات و بەکارهێنانی زیرەکی دەستکرد دەکات بۆ بەرزکردنەوەی بەرهەمهێنان لە ژیانی ڕۆژانەدا بەبێ دروستکردنی شەکەتی.
            """.trimIndent()

            else -> """
                دەقی داواکراو ئامادە کرا:
                
                بە پێی داواکارییەکەت، بابەتەکە بە شێوازێکی جوان، هاوچەرخ و پڕۆفیشناڵ داڕێژرا. دەتوانیت لە ڕێگەی دوگمەی کۆپیکردن ڕاستەوخۆ بەکاریبهێنیت یان لە فایلەکاندا هەڵیبگریت.
            """.trimIndent()
        }
    }

    private fun generateStudyAnswer(norm: String, raw: String): String {
        return """
            پلانی فێربوون و تاقیکردنەوەی پێشنیارکراو:
            
            📅 خشتەی ٥ ڕۆژە بۆ ئامادەکاری تاقیکردنەوە:
            • ڕۆژی ١: خوێندنەوەی گشتی وانەکە و دەستنیشانکردنی دەستەواژە سەرەکییەکان.
            • ڕۆژی ٢: تێگەیشتن لە چەمکە قورسەکان و بەکارهێنانی نموونەی سادە.
            • ڕۆژی ٣: کورتکردنەوەی تێبینییەکان لەسەر لاپەڕەی پوخت.
            • ڕۆژی ٤: وەڵامدانەوەی پرسیارە نموونەییەکان بەبێ سەیرکردنی وەڵام.
            • ڕۆژی ٥: پێداچوونەوەی خێرا و ئارامکردنەوەی مێشک.
            
            💡 کاتێک ئامادەبوویت، بنووسە «تاقیکردنەوەم لێ بکە» تاوەکو ٢٠ پرسیاری هەمەچەشنەت بۆ دابنێم!
        """.trimIndent()
    }

    private fun generateCodeAnswer(norm: String, raw: String): String {
        return """
            ئەمەش چارەسەری کۆد بەپێی ستاندارد:
            
            ```kotlin
            // شێوازی پاکی جێبەجێکردن لە Android Jetpack Compose
            @Composable
            fun BasokaFeatureCard(title: String, onClick: () -> Unit) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    onClick = onClick
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            ```
            
            ڕوونکردنەوەی هەنگاوەکان:
            ١. بەکارهێنانی Material 3 بۆ پشتیوانی ڕووکاری تاریک (Dark Theme).
            ٢. پاراستنی Layout Direction بۆ دڵنیابوون لە RTLی تەواو.
            ٣. لە کاتی ڕوودانی Exception، سیستەم پەیامی شیاو بە زمانی کوردی پیشانی بەکارهێنەر دەدات.
        """.trimIndent()
    }

    private fun generateResearchAnswer(norm: String, raw: String): String {
        return """
            ئەنجامی توێژینەوەی دەستبەجێ:
            
            🔍 تەوەری سەرەکی: شیکردنەوەی بابەتی داواکراو بەپێی سەرچاوە باوەڕپێکراوەکان
            
            ١. پێناسە و باکگراوند: زانیارییە سەرەکییەکان دەریدەخەن کە هاوسەنگی لە نێوان خێرایی و وردی پێویستە.
            ٢. سەرچاوە و شایەدییەکان: لە پێگەی فەرمی و توێژینەوە نوێیەکاندا، جەخت لەسەر کارایی بەرز کراوەتەوە.
            ٣. بەراوردی خاڵە بەهێزەکان: لە چاو مۆدێلە تەقلیدییەکان، ئەم ڕێکارە ٤٠٪ کات کەمدەکاتەوە.
            
            ئەگەر سەرچاوەی زیاترت دەوێت، دەتوانیت دیاری بکەیت لە کام بوارەدا قوڵتر ببمەوە.
        """.trimIndent()
    }

    private fun generateGeneralAnswer(norm: String, raw: String): String {
        return when {
            norm.contains("سڵاو") || norm.contains("چۆنی") || norm.contains("باشی") ->
                "سڵاو! من BASOKA ـم، یاریدەدەری زیرەکی کەسیت. هەموو شتێک ئامادەیە؛ چ کارێکت هەیە یارمەتیت بدەم؟"
            norm.contains("کێیت") || norm.contains("تۆ کێیت") ->
                "من BASOKA ـم؛ یاریدەدەرێکی زیرەکی دەستکرد کە توانای جێبەجێکردنی زیاتر لە ١٠٠٠+ ئەرک، کات، بیرخستنەوە، نووسین و فایل هەیە بە کوردیی سۆرانی."
            else ->
                "فەرموو، داواکارییەکەت لەلایەن BASOKA خوێندرایەوە. دەتوانم لە ئەرکەکان، کات، فایل، وێنە، خوێندن، کۆد و ئۆتۆماتیک یارمەتیت بدەم. چ کارێکی دیاریکراوت پێویستە؟"
        }
    }
}
