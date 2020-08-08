package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.AndroidBuddyLibraryPlugin
import com.likethesalad.android.buddylib.modules.createmetadata.CreateMetadataTaskGenerator
import com.likethesalad.android.buddylib.utils.LibDependencyHandlerUtil
import com.likethesalad.android.common.di.GeneralInjector

object LibraryInjector {

    private lateinit var component: LibraryComponent

    fun getDependencyHandlerUtil(): LibDependencyHandlerUtil {
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