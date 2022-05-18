package com.likethesalad.android.buddylib.utils

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android_android.buddy.plugin.generated.BuildConfig
import javax.inject.Inject

@LibraryScope
class LibDependencyHandlerUtil
@Inject constructor(
    projectDependencyToolsProvider: ProjectDependencyToolsProvider
) : DependencyHandlerUtil(projectDependencyToolsProvider) {

    companion object {
        private const val BYTE_BUDDY_URI = "net.bytebuddy:byte-buddy:${BuildConfig.BYTE_BUDDY_VERSION}"
    }

    override fun addDependencies() {
        super.addDependencies()
        addCompileOnly(BYTE_BUDDY_URI)
    }
}