package com.likethesalad.android.buddylib.modules.createproperties.utils

import javax.inject.Singleton

@Singleton
class CreatePropertiesTaskNameGenerator {

    fun generateTaskName(variantName: String): String {
        return "create${variantName.capitalize()}AndroidBuddyLibraryProperties"
    }
}