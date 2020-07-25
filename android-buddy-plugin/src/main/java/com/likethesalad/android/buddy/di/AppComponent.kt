package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationCreator
import com.likethesalad.android.buddy.modules.transform.ByteBuddyTransform
import com.likethesalad.android.common.di.GeneralComponent
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import dagger.Component

@AppScope
@Component(modules = [AppModule::class], dependencies = [GeneralComponent::class])
interface AppComponent {
    fun transform(): ByteBuddyTransform
    fun dependencyHandlerUtil(): DependencyHandlerUtil
    fun customConfigurationCreator(): CustomConfigurationCreator
}