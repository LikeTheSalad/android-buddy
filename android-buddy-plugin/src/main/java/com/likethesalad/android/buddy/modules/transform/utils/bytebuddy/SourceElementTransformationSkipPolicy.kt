package com.likethesalad.android.buddy.modules.transform.utils.bytebuddy

import com.google.auto.factory.AutoFactory
import com.likethesalad.android.buddy.modules.transform.base.TransformationSkipPolicy
import net.bytebuddy.build.Plugin
import javax.inject.Inject

@AutoFactory
class SourceElementTransformationSkipPolicy
@Inject constructor(private val excludePrefixes: Set<String>) : TransformationSkipPolicy<Plugin.Engine.Source.Element> {

    override fun shouldSkipItem(item: Plugin.Engine.Source.Element): Boolean {
        return excludePrefixes.any { prefix ->
            item.name.startsWith(prefix)
        }
    }
}