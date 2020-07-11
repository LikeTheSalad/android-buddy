package com.likethesalad.android.common.di

object GeneralInjector {
    val component: GeneralComponent by lazy {
        DaggerGeneralComponent.builder().build()
    }
}