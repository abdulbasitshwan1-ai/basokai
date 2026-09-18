package com.example.ai

import java.util.Calendar

data class KurdishCalendarInfo(
    val kurdishDate: String,
    val hijriDate: String,
    val gregorianDate: String,
    val prayerTimes: Map<String, String>,
    val specialOccasion: String?
)

object KurdishCultureUtils {

    // Simple Kurdish and Hijri calendar calculation
    fun getTodayKurdishInfo(): KurdishCalendarInfo {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Kurdish year is Gregorian + 700 (standard Kurdish calendar)
        val kurdishYear = year + 700
        val kurdishMonths = listOf(
            "نەورۆز / خاکەلێوە", "گوڵان", "جۆزەردان", "پووشپەڕ", "گەلاوێژ", "خەرمانان",
            "بەران / ڕەزبەر", "گەڵاڕێزان", "سەرماوەز", "بەفرانبار", "ڕێبەندان", "ڕەشەمە"
        )
        val monthIndex = (month + 9) % 12
        val kurdishMonthName = kurdishMonths[monthIndex]

        val kurdishDateStr = "$day ـی $kurdishMonthName، ساڵی $kurdishYear کوردی"
        val gregorianDateStr = "$day-$month-$year زایینی"
        val hijriDateStr = "ساڵی ١٤٤٧ کۆچی"

        val prayers = mapOf(
            "بەیانی (فەجر)" to "٠٤:٣٠",
            "نیوەڕۆ" to "١٢:١٥",
            "عەسر" to "١٥:٤٥",
            "ئێوارە (مەغریب)" to "١٨:٢٠",
            "عیشا" to "١٩:٥٠"
        )

        val occasion = when {
            month == 3 && day == 21 -> "جەژنی نەورۆز و سەری ساڵی کوردی پیرۆز بێت! 🌸🔥"
            month == 12 && day == 17 -> "ڕۆژی ئاڵای کوردستان پیرۆز بێت! ☀️"
            else -> null
        }

        return KurdishCalendarInfo(
            kurdishDate = kurdishDateStr,
            hijriDate = hijriDateStr,
            gregorianDate = gregorianDateStr,
            prayerTimes = prayers,
            specialOccasion = occasion
        )
    }

    // Common offline Kurdish dictionary
    val offlineDictionary = listOf(
        "AI" to "ژیریی دەستکرد",
        "Algorithm" to "ئەلگۆریتم / ڕێسای هەژمارکردن",
        "Database" to "بنکەدراوە / بەستەری داتا",
        "Hardware" to "ڕەقەکاڵا",
        "Software" to "نەرمەکاڵا",
        "Network" to "تۆڕ",
        "Computer" to "کۆمپیوتەر / بژمێر",
        "Processor" to "چارەسەرکەر / پرۆسێسەر",
        "Memory" to "بیرگە / یادگە",
        "Internet" to "ئینتەرنێت",
        "Settings" to "ڕێکخستنەکان",
        "Assistant" to "یاریدەدەر",
        "Download" to "داگرتن / دابەزاندن",
        "Upload" to "بارکردن / ناردنە سەرەوە",
        "Security" to "ئاسایش و پاراستن",
        "Update" to "نوێکردنەوە",
        "Application" to "بەرنامە / ئەپڵیکەیشن"
    )
}
