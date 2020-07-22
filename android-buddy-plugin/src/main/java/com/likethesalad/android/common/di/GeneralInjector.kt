package com.likethesalad.android.common.di

import com.likethesalad.android.common.base.BuddyPlugin

object GeneralInjector {

    lateinit var component: GeneralComponent
        private set

    fun init(buddyPlugin: BuddyPlugin) {
        component = DaggerGeneralComponent.builder()
            .generalModule(GeneralModule(buddyPlugin))
            .build()
    }
}