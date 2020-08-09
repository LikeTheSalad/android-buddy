package com.likethesalad.android.buddylib.modules.createmetadata.utils

import com.likethesalad.android.buddylib.di.LibraryScope
import javax.inject.Inject

@LibraryScope
class CreateMetadataTaskNameGenerator @Inject constructor() {

    fun generateTaskName(variantName: String): String {
        return "create${variantName.capitalize()}AndroidBuddyLibraryMetadata"
    }
}