package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkipPolicy
import com.likethesalad.android.buddy.modules.transform.utils.bytebuddy.SourceElementTransformationSkippedStrategy
import com.likethesalad.android.buddy.utils.SourceElementsIterator
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import java.util.jar.Manifest

class CompoundSource @AssistedInject constructor(
    byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    @Assisted private val sourceOrigins: Set<Plugin.Engine.Source.Origin>,
    @Assisted private val skipPolicy: SourceElementTransformationSkipPolicy,
    @Assisted private val skippedStrategy: SourceElementTransformationSkippedStrategy
) : Plugin.Engine.Source,
    Plugin.Engine.Source.Origin {

    @AssistedFactory
    interface Factory {
        fun create(
            sourceOrigins: Set<Plugin.Engine.Source.Origin>,
            skipPolicy: SourceElementTransformationSkipPolicy,
            skippedStrategy: SourceElementTransformationSkippedStrategy
        ): CompoundSource
    }

    private val locator: ClassFileLocator by lazy {
        byteBuddyClassesInstantiator.makeCompoundClassFileLocator(sourceOrigins.map { it.classFileLocator })
    }

    override fun read(): Plugin.Engine.Source.Origin = this

    override fun getManifest(): Manifest? = Plugin.Engine.Source.Origin.NO_MANIFEST

    override fun iterator(): MutableIterator<Plugin.Engine.Source.Element> {
        return SourceElementsIterator(sourceOrigins.map {
            it.iterator()
        }.toMutableList(), skipPolicy, skippedStrategy)
    }

    override fun close() {
        sourceOrigins.forEach {
            it.close()
        }
    }

    override fun getClassFileLocator(): ClassFileLocator = locator
}