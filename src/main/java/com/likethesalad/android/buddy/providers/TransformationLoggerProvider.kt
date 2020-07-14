package com.likethesalad.android.buddy.providers

import com.likethesalad.android.buddy.bytebuddy.TransformationLogger

interface TransformationLoggerProvider {
    fun getTransformationLogger(): TransformationLogger
}