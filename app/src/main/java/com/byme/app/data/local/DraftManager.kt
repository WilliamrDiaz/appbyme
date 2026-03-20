package com.byme.app.data.local

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DraftManager @Inject constructor() {
    private val drafts = mutableMapOf<String, String>()
    private val draftNames = mutableMapOf<String, String>()

    fun saveDraft(chatId: String, text: String, professionalName: String = "") {
        if (text.isBlank()) {
            drafts.remove(chatId)
            draftNames.remove(chatId)
        } else {
            drafts[chatId] = text
            if (professionalName.isNotEmpty()) draftNames[chatId] = professionalName
        }
    }

    fun getDraft(chatId: String): String {
        return drafts[chatId] ?: ""
    }

    fun getDraftName(chatId: String): String = draftNames[chatId] ?: ""

    fun clearDraft(chatId: String) {
        drafts.remove(chatId)
        draftNames.remove(chatId)
    }

    fun getAllDrafts(): Map<String, String> {
        return drafts.toMap()
    }
}