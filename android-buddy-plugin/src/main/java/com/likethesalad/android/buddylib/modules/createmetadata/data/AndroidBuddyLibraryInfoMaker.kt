package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.providers.ProjectInfoProvider
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import javax.inject.Inject

@LibraryScope
class AndroidBuddyLibraryInfoMaker @Inject constructor(private val projectInfoProvider: ProjectInfoProvider) {

    companion object {
        private val VALID_ID_REGEX = Regex("[a-z]+[a-z0-9]*([.-]?[a-z0-9]+)*")
        private val VALID_GROUP_REGEX = Regex("[^\\s\\t\\n]+")
        private val CHECK_NAME_WRAPPING = Regex("(^[^a-zA-Z0-9]+|[^a-zA-Z0-9]+\$)")
        private val CLEAN_NAME_SPACING = Regex("[\\s\\t\\n]+")
    }

    fun make(
        id: String,
        pluginNames: Set<String>
    ): AndroidBuddyLibraryInfo {
        val group = projectInfoProvider.getGroup()
        val name = projectInfoProvider.getName()
        val version = projectInfoProvider.getVersion()

        validateId(id)
        validateGroup(group)
        validateName(name)

        return AndroidBuddyLibraryInfo(
            id,
            group,
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
        if (!VALID_GROUP_REGEX.matches(group)) {
            throw IllegalArgumentException("The group provided '$group' has not a valid format")
        }
    }

    private fun validateName(name: String) {
        if (name.trim().isEmpty()) {
            throw IllegalArgumentException("The name provided '$name' is not valid, it cannot be empty")
        }
    }

    private fun cleanName(name: String): String {
        val nameProperlyWrapped = name.replace(CHECK_NAME_WRAPPING, "")
        return nameProperlyWrapped.replace(CLEAN_NAME_SPACING, "_").toLowerCase()
    }

}