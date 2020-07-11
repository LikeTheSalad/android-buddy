package com.likethesalad.android.buddylib.di

import com.likethesalad.android.buddylib.models.CreateJarDescriptionPropertiesArgs
import com.likethesalad.android.common.di.GeneralInjector

object LibraryInjector {

    private val component: LibraryComponent by lazy {
        DaggerLibraryComponent.builder().generalComponent(GeneralInjector.component).build()
    }

    fun getCreateJarDescriptionPropertiesArgs(): CreateJarDescriptionPropertiesArgs {
        return component.createJarDescriptionPropertiesArgs()
    }
}