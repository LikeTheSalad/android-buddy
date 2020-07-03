package com.likethesalad.android.buddy.di

import com.likethesalad.android.buddy.transform.ByteBuddyTransform
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidBuddyModule::class])
interface AndroidBuddyComponent {
    fun transform(): ByteBuddyTransform
}