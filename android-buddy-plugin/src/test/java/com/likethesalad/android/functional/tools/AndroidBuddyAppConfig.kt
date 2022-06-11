package com.likethesalad.android.functional.tools

import com.likethesalad.tools.functional.testing.layout.items.GradleBlockItem

class AndroidBuddyAppConfig(
    private val librariesScope: LibrariesPolicyConfig
) : GradleBlockItem {

    override fun getItemText(): String {
        return """
            androidBuddy { 
                librariesPolicy {
                    scope {
                        type = "${librariesScope.value}"
                        ${if (librariesScope is LibrariesPolicyConfig.UseOnly) "args = ${getListAsArgs(librariesScope.libIds)}" else ""}
                    }
                }
            }
        """.trimIndent()
    }

    private fun getListAsArgs(libIds: List<String>): String {
        var result = "["

        val quotedIds = libIds.map { "\"$it\"" }
        result += quotedIds.joinToString(",")

        result += "]"
        return result
    }

    sealed class LibrariesPolicyConfig(val value: String) {
        object UseAll : LibrariesPolicyConfig("UseAll")
        object IgnoreAll : LibrariesPolicyConfig("IgnoreAll")
        class UseOnly(val libIds: List<String>) : LibrariesPolicyConfig("UseOnly")
    }
}