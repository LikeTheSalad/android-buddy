package com.likethesalad.android.buddy.modules.transform.base

interface TransformationSkipPolicy<T : Any> {
    fun shouldSkipItem(item: T): Boolean
}