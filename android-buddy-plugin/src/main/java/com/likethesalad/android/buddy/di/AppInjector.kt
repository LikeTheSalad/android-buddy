package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.modules.transform.ByteBuddyTransform
import com.likethesalad.android.buddy.utils.AppDependencyHandlerUtil
import com.likethesalad.android.common.di.GeneralInjector

object AppInjector {

    private lateinit var component: AppComponent

    fun init(plugin: AndroidBuddyPlugin) {
        GeneralInjector.init(plugin)
        component = DaggerAppComponent.builder()
            .generalComponent(GeneralInjector.component)
            .appModule(AppModule(plugin))
            .build()
    }

    fun getByteBuddyTransform(): ByteBuddyTransform {
        return component.transform()
    }

    fun getDependencyHandlerUtil(): AppDependencyHandlerUtil {
        return component.dependencyHandlerUtil()
    }
}