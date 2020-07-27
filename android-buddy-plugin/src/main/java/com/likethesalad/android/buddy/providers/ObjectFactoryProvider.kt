package com.likethesalad.android.buddy.providers

import org.gradle.api.model.ObjectFactory

interface ObjectFactoryProvider {
    fun getObjectFactory(): ObjectFactory
}