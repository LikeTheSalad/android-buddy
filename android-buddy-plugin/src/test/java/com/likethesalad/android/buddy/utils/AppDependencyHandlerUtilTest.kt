package com.likethesalad.android.buddy.utils

import com.google.common.truth.Truth
import com.likethesalad.android.buddy.plugin.generated.BuildConfig
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android.common.utils.DependencyHandlerUtilTest
import org.junit.Test

class AppDependencyHandlerUtilTest : DependencyHandlerUtilTest() {

    override fun initBaseDependencyHandlerUtil(): DependencyHandlerUtil {
        return AppDependencyHandlerUtil(projectDependencyToolsProvider)
    }

    @Test
    fun `Add android buddy tools dependency with plugin version`() {
        executeAddDependencies()

        verifyAndroidBuddyToolsDependencyAdded()
    }

    private fun verifyAndroidBuddyToolsDependencyAdded() {
        val dependencyUri = BuildConfig.ANDROID_BUDDY_TOOLS_URI
        Truth.assertThat(dependencyUri).startsWith("com.likethesalad.android:android-buddy-tools:")
        verifyDependencyAdded(dependencyUri)
    }
}