package com.likethesalad.android.buddy.modules.transform.utils.bytebuddy

import com.likethesalad.android.buddy.modules.transform.base.TransformationSkipPolicy
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.bytebuddy.build.Plugin

class SourceElementTransformationSkipPolicy
@AssistedInject constructor(@Assisted private val excludePrefixes: Set<String>) :
    TransformationSkipPolicy<Plugin.Engine.Source.Element> {

    @AssistedFactory
    interface Factory {
        fun create(excludePrefixes: Set<String>): SourceElementTransformationSkipPolicy
    }

    override fun shouldSkipItem(item: Plugin.Engine.Source.Element): Boolean {
        return excludePrefixes.any { prefix ->
            item.name.startsWith(prefix)
        }
    }
}