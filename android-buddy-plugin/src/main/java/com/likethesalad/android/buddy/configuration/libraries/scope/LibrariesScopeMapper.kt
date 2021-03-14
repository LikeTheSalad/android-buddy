package com.likethesalad.android.buddy.configuration.libraries.scope

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.extension.libraries.scope.LibrariesScopeExtension
import com.likethesalad.android.buddy.extension.libraries.scope.LibrariesScopeType
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class LibrariesScopeMapper @Inject constructor() {

    fun librariesOptionsToLibrariesPolicy(scopeExtension: LibrariesScopeExtension): LibrariesScope {
        val args = scopeExtension.args.get()
        return when (val type = getLibraryPolicyType(scopeExtension.type.get())) {
            LibrariesScopeType.USE_ALL -> checkNoArgsAndReturn(type, args, LibrariesScope.UseAll)
            LibrariesScopeType.IGNORE_ALL -> checkNoArgsAndReturn(type, args, LibrariesScope.IgnoreAll)
            LibrariesScopeType.USE_ONLY -> createUseOnlyPolicy(args)
        }
    }

    private fun checkNoArgsAndReturn(
        type: LibrariesScopeType,
        args: List<Any>,
        librariesScope: LibrariesScope
    ): LibrariesScope {
        if (args.isNotEmpty()) {
            throw IllegalArgumentException("No args should be passed for the '${type.value}' policy")
        }

        return librariesScope
    }

    private fun createUseOnlyPolicy(args: List<Any>): LibrariesScope.UseOnly {
        if (args.isEmpty()) {
            throw IllegalArgumentException(
                "No library ids specified for '${LibrariesScopeType.USE_ONLY.value}', " +
                        "if you don't want to use any library you should set the libraries policy to " +
                        "'${LibrariesScopeType.IGNORE_ALL.value}' instead."
            )
        }

        return LibrariesScope.UseOnly((args as List<String>).toSet())
    }

    private fun getLibraryPolicyType(policyName: String): LibrariesScopeType {
        for (type in LibrariesScopeType.values()) {
            if (type.value == policyName) {
                return type
            }
        }

        throw IllegalArgumentException(
            "Invalid library policy name: '$policyName', the available options are: ${
                LibrariesScopeType.values().map { it.value }
            }"
        )
    }
}