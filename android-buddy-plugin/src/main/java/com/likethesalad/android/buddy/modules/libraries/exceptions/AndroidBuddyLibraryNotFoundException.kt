package com.likethesalad.android.buddy.modules.libraries.exceptions

import com.likethesalad.android.common.base.AndroidBuddyException

class AndroidBuddyLibraryNotFoundException(val idsNotFound: List<String>) : AndroidBuddyException(
    "No Android Library(ies) with the following ID(s) have been found within the project's dependencies: $idsNotFound",
    null
)