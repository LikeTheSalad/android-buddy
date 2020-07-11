package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.models.CreateJarDescriptionPropertiesArgs
import com.likethesalad.android.common.di.GeneralComponent
import dagger.Component

@LibraryScope
@Component(dependencies = [GeneralComponent::class])
interface LibraryComponent {
    fun createJarDescriptionPropertiesArgs(): CreateJarDescriptionPropertiesArgs
}