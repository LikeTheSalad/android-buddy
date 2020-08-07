package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.AndroidBuddyLibraryPlugin
import com.likethesalad.android.buddylib.modules.createmetadata.CreateMetadataTaskGenerator
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.common.di.GeneralInjector
import com.likethesalad.android.common.utils.DependencyHandlerUtil

object LibraryInjector {

    private lateinit var component: LibraryComponent

    fun getCreateJarDescriptionPropertiesArgs(): CreateMetadataTaskArgs {
        return component.createJarDescriptionPropertiesArgs()
    }

    fun getDependencyHandlerUtil(): DependencyHandlerUtil {
        return component.dependencyHandlerUtil()
    }

    fun getCreateMetadataTaskGenerator(): CreateMetadataTaskGenerator {
        return component.createMetadataTaskGenerator()
    }

    fun init(plugin: AndroidBuddyLibraryPlugin) {
        GeneralInjector.init(plugin)
        component = DaggerLibraryComponent.builder()
            .libraryModule(LibraryModule(plugin))
            .generalComponent(GeneralInjector.component)
            .build()
    }
}