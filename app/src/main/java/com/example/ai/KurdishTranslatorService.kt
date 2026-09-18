package com.example.ai

data class TranslationResult(
    val translatedText: String,
    val sourceDialect: String,
    val targetDialect: String,
    val pronunciationTip: String? = null,
    val grammarCorrections: List<String> = emptyList()
)

object KurdishTranslatorService {

    // Common vocabulary mappings across Kurdish dialects
    private val soraniToBadiniDict = mapOf(
        "ئەمڕۆ" to "ئەڤرۆ",
        "سبەی" to "سوبەهی",
        "دوێنێ" to "دوهی",
        "زۆر" to "گەلەک",
        "سوپاس" to "دەستخۆش / سوپاس",
        "چۆنی" to "چەوانی",
        "باشم" to "باشم / ساخلەمم",
        "نازانم" to "نزانم",
        "دەمەوێت" to "دڤێت",
        "نایەم" to "ناهێم",
        "خواردن" to "خوارن",
        "ئاو" to "ئاڤ",
        "ماڵ" to "خانی / ماڵ",
        "کتێب" to "پەرتووک",
        "کار" to "شول / کار",
        "تۆ" to "تۆ",
        "من" to "ئەز",
        "ئێمە" to "ئەم",
        "باشە" to "باشە / درستە",
        "سەیارە" to "ترۆمبێل",
        "پارە" to "پەرە / دراڤ",
        "چاکە" to "باشییە",
        "بەیانیت باش" to "سپێدە باش",
        "شەوت باش" to "شەڤ باش",
        "ئیستا" to "نوکە",
        "هەموو" to "هەمی",
        "نا" to "نەخێر / نێ"
    )

    private val soraniToHawramiDict = mapOf(
        "چۆنی" to "چەنی",
        "باشم" to "خاسەنا",
        "تۆ" to "تۆ",
        "من" to "من",
        "ئەمڕۆ" to "ئارۆ",
        "سوپاس" to "وەشبی",
        "نازانم" to "مەزانۆ",
        "دەمەوێت" to "گەرەکما",
        "خواردن" to "واردەی",
        "ئاو" to "ئاوێ",
        "ماڵ" to "یانە",
        "کار" to "کار",
        "بەیانیت باش" to "سەحەرو خەیر",
        "شەوت باش" to "شەوەت خەیر",
        "ئێستا" to "ئیسە",
        "زۆر" to "فرە"
    )

    // Translate locally between dialects
    fun translateDialect(text: String, from: String, to: String): String {
        val words = text.split(" ")
        val translatedWords = words.map { word ->
            val clean = word.trim().replace("،", "").replace(".", "")
            when {
                from == "سۆرانی" && to == "بادینی" -> soraniToBadiniDict[clean] ?: word
                from == "بادینی" && to == "سۆرانی" -> soraniToBadiniDict.entries.firstOrNull { it.value.contains(clean) }?.key ?: word
                from == "سۆرانی" && to == "هەورامی" -> soraniToHawramiDict[clean] ?: word
                from == "هەورامی" && to == "سۆرانی" -> soraniToHawramiDict.entries.firstOrNull { it.value == clean }?.key ?: word
                else -> word
            }
        }
        return translatedWords.joinToString(" ")
    }

    // Kurdish Grammar & Spell Checker (ڕێنووسی ڕێزمانی کوردی)
    fun checkKurdishGrammar(input: String): List<String> {
        val suggestions = mutableListOf<String>()
        if (input.contains("پێت بلێم") || input.contains("پێت بڵێم")) {
            suggestions.add("«پێت بڵێم» ڕاستترە لە «پێت بلێم» (بەکارهێنانی پیتی ڵ).")
        }
        if (input.contains("انشااللە") || input.contains("انشاللە")) {
            suggestions.add("ڕێنووسی دروست: «ئینشاڵڵا» یان «إن شاء الله».")
        }
        if (input.contains("سوپاستەکەم") || input.contains("سوپاست ئەکەم")) {
            suggestions.add("ڕێنووسی ستاندارد: «سوپاست دەکەم» (دەکەم لە جیاتی ئەکەم).")
        }
        if (input.contains("دەست خۆش")) {
            suggestions.add("باشترە بە لکێنراوی بنووسرێت: «دەستخۆش».")
        }
        if (input.contains("ئەتوانم")) {
            suggestions.add("لە زمانی ستاندارددا پاشگری «دە» بەکاردێت: «دەتوانم» لە جیاتی «ئەتوانم».")
        }
        if (suggestions.isEmpty()) {
            suggestions.add("ڕێنووسی دەقەکەت تەندروست و بێ کێشەی بەرچاوە! ✨")
        }
        return suggestions
    }
}
