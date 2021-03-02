package com.likethesalad.android.buddy.modules.libraries

import com.likethesalad.android.common.base.AndroidBuddyException
import com.likethesalad.android.common.models.libinfo.AndroidBuddyLibraryInfo

class DuplicateLibraryIdException(
    val id: String,
    val librariesWithSameId: List<AndroidBuddyLibraryInfo>
) : AndroidBuddyException("Multiple android buddy libraries share the same ID '$id': $librariesWithSameId", null)