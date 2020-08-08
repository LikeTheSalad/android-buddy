package com.likethesalad.android.buddylib.utils

import com.likethesalad.android.buddy.plugin.generated.BuildConfig
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android.common.utils.DependencyHandlerUtilTest
import org.junit.Test

class LibDependencyHandlerUtilTest : DependencyHandlerUtilTest() {

    override fun initBaseDependencyHandlerUtil(): DependencyHandlerUtil {
        return LibDependencyHandlerUtil(projectDependencyToolsProvider)
    }

    @Test
    fun `Add bytebuddy dependency`() {
        executeAddDependencies()

        verifyDependencyAdded("net.bytebuddy:byte-buddy:${BuildConfig.BYTE_BUDDY_VERSION}")
    }
}