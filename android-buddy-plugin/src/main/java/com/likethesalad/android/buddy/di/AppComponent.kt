package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.modules.customconfig.CustomBucketConfigurationCreator
import com.likethesalad.android.buddy.modules.customconfig.CustomConfigurationVariantSetup
import com.likethesalad.android.buddy.modules.transform.ByteBuddyTransform
import com.likethesalad.android.buddy.utils.AppDependencyHandlerUtil
import com.likethesalad.android.common.di.GeneralComponent
import dagger.Component

@AppScope
@Component(modules = [AppModule::class], dependencies = [GeneralComponent::class])
interface AppComponent {
    fun transform(): ByteBuddyTransform
    fun dependencyHandlerUtil(): AppDependencyHandlerUtil
    fun customConfigurationCreator(): CustomBucketConfigurationCreator
    fun customConfigurationVariantSetup(): CustomConfigurationVariantSetup
}