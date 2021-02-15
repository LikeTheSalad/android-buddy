package com.likethesalad.android.common.models.libinfo.utils

import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.utils.Constants
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidBuddyLibraryInfoFqnBuilder @Inject constructor() {

    companion object {
        private val INVALID_CHARS_REGEX = Regex("[^a-zA-Z_\$.\\d]")
        private val INVALID_DIGIT_POSITION_REGEX = Regex("((?<=\\.)\\d|(^\\d))")
    }

    fun buildFqn(info: AndroidBuddyLibraryInfo): String {
        val name = "${info.group}.${info.name}.${Constants.PLUGINS_METADATA_CLASS_NAME}"
        return cleanUpFqn(name)
    }

    private fun cleanUpFqn(original: String): String {
        return original.replace(INVALID_CHARS_REGEX, "_")
            .replace(INVALID_DIGIT_POSITION_REGEX, "n$0")
    }
}