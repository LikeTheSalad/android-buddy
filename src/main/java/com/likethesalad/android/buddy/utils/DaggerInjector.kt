package com.likethesalad.android.buddy.utils

import com.likethesalad.android.buddy.AndroidBuddyPlugin
import com.likethesalad.android.buddy.di.AndroidBuddyComponent
import com.likethesalad.android.buddy.di.AndroidBuddyModule
import com.likethesalad.android.buddy.di.DaggerAndroidBuddyComponent
import com.likethesalad.android.buddy.transform.ByteBuddyTransform

object DaggerInjector {

    private lateinit var component: AndroidBuddyComponent

    fun init(plugin: AndroidBuddyPlugin) {
        component = DaggerAndroidBuddyComponent.builder()
            .androidBuddyModule(AndroidBuddyModule(plugin))
            .build()
    }

    fun getByteBuddyTransform(): ByteBuddyTransform {
        return component.transform()
    }
}