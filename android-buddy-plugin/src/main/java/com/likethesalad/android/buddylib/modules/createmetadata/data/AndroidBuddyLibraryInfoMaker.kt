package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.providers.ProjectInfoProvider
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import javax.inject.Inject

@LibraryScope
class AndroidBuddyLibraryInfoMaker @Inject constructor(private val projectInfoProvider: ProjectInfoProvider) {

    companion object {
        private val VALID_ID_REGEX = Regex("[a-z]+[a-z0-9]*([.-]?[a-z0-9]+)*")
    }

    fun make(
        id: String,
        pluginNames: Set<String>
    ): AndroidBuddyLibraryInfo {
        validateId(id)
        return AndroidBuddyLibraryInfo(
            id,
            projectInfoProvider.getGroup(),
            projectInfoProvider.getName(),
            projectInfoProvider.getVersion(),
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

}