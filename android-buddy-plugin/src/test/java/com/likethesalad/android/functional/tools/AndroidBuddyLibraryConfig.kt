package com.likethesalad.android.functional.tools

import com.likethesalad.tools.functional.testing.layout.items.GradleBlockItem

class AndroidBuddyLibraryConfig(
    private val id: String,
    private val transformationClassNames: List<String>
) : GradleBlockItem {

    override fun getItemText(): String {
        return """
            androidBuddyLibrary { 
                id = "$id"
                exposedTransformationNames = ${transformationClassNames.map { "\"$it\"" }}
            }
        """.trimIndent()
    }
}