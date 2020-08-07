package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.modules.createmetadata.CreateMetadataTaskGenerator
import com.likethesalad.android.buddylib.modules.createmetadata.data.CreateMetadataTaskArgs
import com.likethesalad.android.common.di.GeneralComponent
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import dagger.Component

@LibraryScope
@Component(modules = [LibraryModule::class], dependencies = [GeneralComponent::class])
interface LibraryComponent {
    fun createJarDescriptionPropertiesArgs(): CreateMetadataTaskArgs
    fun createMetadataTaskGenerator(): CreateMetadataTaskGenerator
    fun dependencyHandlerUtil(): DependencyHandlerUtil
}