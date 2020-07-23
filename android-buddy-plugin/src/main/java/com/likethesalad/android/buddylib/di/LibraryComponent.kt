package com.likethesalad.android.buddylib.di

import com.likethesalad.android.common.di.GeneralComponent
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import dagger.Component

@LibraryScope
@Component(dependencies = [GeneralComponent::class])
interface LibraryComponent {
    fun dependencyHandlerUtil(): DependencyHandlerUtil
}