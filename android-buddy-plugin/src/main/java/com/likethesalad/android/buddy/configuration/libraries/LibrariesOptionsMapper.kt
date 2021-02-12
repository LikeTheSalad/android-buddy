package com.likethesalad.android.buddy.configuration.libraries

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.buddy.extension.libraries.LibrariesOptions
import com.likethesalad.android.buddy.extension.libraries.LibrariesPolicyType
import javax.inject.Inject

@Suppress("UnstableApiUsage")
@AppScope
class LibrariesOptionsMapper @Inject constructor() {

    fun librariesOptionsToLibrariesPolicy(options: LibrariesOptions): LibrariesPolicy {
        val args = options.args.get()
        return when (val type = getLibraryPolicyType(options.policyName.get())) {
            LibrariesPolicyType.USE_ALL -> checkNoArgsAndReturn(type, args, LibrariesPolicy.UseAll)
            LibrariesPolicyType.IGNORE_ALL -> checkNoArgsAndReturn(type, args, LibrariesPolicy.IgnoreAll)
            LibrariesPolicyType.USE_ONLY -> createUseOnlyPolicy(args)
        }
    }

    private fun checkNoArgsAndReturn(
        type: LibrariesPolicyType,
        args: List<Any>,
        librariesPolicy: LibrariesPolicy
    ): LibrariesPolicy {
        if (args.isNotEmpty()) {
            throw IllegalArgumentException("No args should be passed for the '${type.value}' policy")
        }

        return librariesPolicy
    }

    private fun createUseOnlyPolicy(args: List<Any>): LibrariesPolicy.UseOnly {
        if (args.isEmpty()) {
            throw IllegalArgumentException(
                "No library ids specified for '${LibrariesPolicyType.USE_ONLY.value}', " +
                        "if you don't want to use any library you should set the libraries policy to " +
                        "'${LibrariesPolicyType.IGNORE_ALL.value}' instead."
            )
        }

        return LibrariesPolicy.UseOnly((args as List<String>).toSet())
    }

    private fun getLibraryPolicyType(policyName: String): LibrariesPolicyType {
        for (type in LibrariesPolicyType.values()) {
            if (type.value == policyName) {
                return type
            }
        }

        throw IllegalArgumentException(
            "Invalid library policy name: '$policyName', the available options are: ${
                LibrariesPolicyType.values().map { it.value }
            }"
        )
    }
}