package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import javax.inject.Inject

@LibraryScope
class AndroidBuddyLibraryInfoMaker @Inject constructor() {

    companion object {
        private val VALID_ID_REGEX = Regex("[a-z]+[a-z0-9]*([.-]?[a-z0-9]+)*")
        private val CHECK_NAME_AND_GROUP_VALIDITY = Regex("[a-zA-Z0-9]+")
        private val CHECK_NAME_AND_GROUP_WRAPPING = Regex("(^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+\$)")
        private val CLEAN_NAME_SPACING = Regex("[\\s\\t\\n]+")
        private val CLEAN_GROUP_ILLEGAL_CHARS = Regex("[^a-zA-Z0-9.]+")
    }

    fun make(
        id: String,
        group: String,
        name: String,
        version: String,
        pluginNames: Set<String>
    ): AndroidBuddyLibraryInfo {
        validateId(id)
        validateGroup(group)
        validateName(name)

        return AndroidBuddyLibraryInfo(
            id,
            cleanGroup(group),
            cleanName(name),
            version,
            pluginNames
        )
    }

    private fun validateId(id: String) {
        if (!VALID_ID_REGEX.matches(id)) {
            throw IllegalArgumentException(
                "Id provided '$id' has not a valid format:" +
                        "\n -Only lowercase letters, numbers, '.' and '-' are allowed." +
                        "\n -It must start with a letter." +
                        "\n -It cannot end neither with '-' nor with '.'." +
                        "\n -There cannot be a consecutive '-' or '.' after a '-' and/or '.'."
            )
        }
    }

    private fun validateGroup(group: String) {
        if (!CHECK_NAME_AND_GROUP_VALIDITY.containsMatchIn(group)) {
            throw IllegalArgumentException("The group provided '$group' is not valid.")
        }
    }

    private fun validateName(name: String) {
        if (!CHECK_NAME_AND_GROUP_VALIDITY.containsMatchIn(name)) {
            throw IllegalArgumentException("The name provided '$name' is not valid.")
        }
    }

    private fun cleanName(name: String): String {
        val nameProperlyWrapped = name.replace(CHECK_NAME_AND_GROUP_WRAPPING, "")
        return nameProperlyWrapped.replace(CLEAN_NAME_SPACING, "_").toLowerCase()
    }

    private fun cleanGroup(group: String): String {
        val groupProperlyWrapped = group.replace(CHECK_NAME_AND_GROUP_WRAPPING, "")
        return groupProperlyWrapped.replace(CLEAN_GROUP_ILLEGAL_CHARS, ".").toLowerCase()
    }

}