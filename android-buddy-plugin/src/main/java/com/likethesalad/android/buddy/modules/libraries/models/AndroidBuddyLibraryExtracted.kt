package com.likethesalad.android.buddy.modules.libraries.models

import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo
import java.io.File

data class AndroidBuddyLibraryExtracted(
    val jarFile: File,
    val info: AndroidBuddyLibraryInfo
)
