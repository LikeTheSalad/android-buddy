package com.likethesalad.android.buddy.modules.transform.base

interface TransformationSkippedStrategy<T : Any> {
    fun onTransformationSkipped(item: T)
}