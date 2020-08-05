package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.AndroidBuddyLibraryPlugin
import com.likethesalad.android.buddylib.modules.createproperties.data.CreateJarDescriptionPropertiesArgs
import com.likethesalad.android.common.di.GeneralInjector
import com.likethesalad.android.common.utils.DependencyHandlerUtil

object LibraryInjector {

    private lateinit var component: LibraryComponent

    fun getCreateJarDescriptionPropertiesArgs(): CreateJarDescriptionPropertiesArgs {
        return component.createJarDescriptionPropertiesArgs()
    }

    fun getDependencyHandlerUtil(): DependencyHandlerUtil {
        return component.dependencyHandlerUtil()
    }

    fun init(plugin: AndroidBuddyLibraryPlugin) {
        GeneralInjector.init(plugin)
        component = DaggerLibraryComponent.builder()
            .generalComponent(GeneralInjector.component)
            .build()
    }
}