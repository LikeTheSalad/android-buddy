package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.di.AppScope
import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android_android.buddy.plugin.generated.BuildConfig
import javax.inject.Inject

@AppScope
class AppDependencyHandlerUtil
@Inject constructor(
    projectDependencyToolsProvider: ProjectDependencyToolsProvider
) : DependencyHandlerUtil(projectDependencyToolsProvider) {

    companion object {
        private const val ANDROID_BUDDY_TOOLS_DEPENDENCY_URI = BuildConfig.ANDROID_BUDDY_TOOLS_URI
    }

    override fun addDependencies() {
        super.addDependencies()
        addCompileOnly(ANDROID_BUDDY_TOOLS_DEPENDENCY_URI)
    }
}