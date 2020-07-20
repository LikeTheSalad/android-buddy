package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.transform.ByteBuddyTransform
import com.likethesalad.android.common.di.GeneralInjector
import com.likethesalad.android.common.utils.DependencyHandlerUtil

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

    fun getDependencyHandlerUtil(): DependencyHandlerUtil {
        return component.dependencyHandlerUtil()
    }
}