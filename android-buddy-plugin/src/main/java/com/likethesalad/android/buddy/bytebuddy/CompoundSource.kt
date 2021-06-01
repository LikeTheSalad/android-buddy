package com.likethesalad.android.buddy.bytebuddy

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.utils.ConcatIterator
import com.likethesalad.android.buddy.utils.SourceElementsIterator
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import java.util.jar.Manifest

@AutoFactory
class CompoundSource(
    @Provided byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val sourceOrigins: Set<Plugin.Engine.Source.Origin>,
    private val excludePrefixes: Set<String>
) : Plugin.Engine.Source,
    Plugin.Engine.Source.Origin {

    private val locator: ClassFileLocator by lazy {
        byteBuddyClassesInstantiator.makeCompoundClassFileLocator(sourceOrigins.map { it.classFileLocator })
    }

    override fun read(): Plugin.Engine.Source.Origin = this

    override fun getManifest(): Manifest? = Plugin.Engine.Source.Origin.NO_MANIFEST

    override fun iterator(): MutableIterator<Plugin.Engine.Source.Element> {
        return SourceElementsIterator(sourceOrigins.map {
            it.iterator()
        }.toMutableList(), excludePrefixes)
    }

    override fun close() {
        sourceOrigins.forEach {
            it.close()
        }
    }

    override fun getClassFileLocator(): ClassFileLocator = locator
}