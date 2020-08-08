package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.modules.createmetadata.CreateMetadataTaskGenerator
import com.likethesalad.android.buddylib.utils.LibDependencyHandlerUtil
import com.likethesalad.android.common.di.GeneralComponent
import dagger.Component

@LibraryScope
@Component(modules = [LibraryModule::class], dependencies = [GeneralComponent::class])
interface LibraryComponent {
    fun createMetadataTaskGenerator(): CreateMetadataTaskGenerator
    fun dependencyHandlerUtil(): LibDependencyHandlerUtil
}