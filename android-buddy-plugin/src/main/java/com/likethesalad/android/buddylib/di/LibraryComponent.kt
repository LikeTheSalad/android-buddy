package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.modules.createproperties.data.CreateMetadataTaskArgs
import com.likethesalad.android.common.di.GeneralComponent
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import dagger.Component

@LibraryScope
@Component(dependencies = [GeneralComponent::class])
interface LibraryComponent {
    fun createJarDescriptionPropertiesArgs(): CreateMetadataTaskArgs
    fun dependencyHandlerUtil(): DependencyHandlerUtil
}