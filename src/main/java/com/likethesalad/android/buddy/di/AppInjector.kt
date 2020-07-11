package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.transform.ByteBuddyTransform
import com.likethesalad.android.common.di.GeneralInjector

object AppInjector {

    private lateinit var component: AppComponent

    fun init(plugin: AndroidBuddyPlugin) {
        component = DaggerAppComponent.builder()
            .generalComponent(GeneralInjector.component)
            .appModule(AppModule(plugin))
            .build()
    }

    fun getByteBuddyTransform(): ByteBuddyTransform {
        return component.transform()
    }
}