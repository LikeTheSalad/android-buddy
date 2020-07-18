package com.likethesalad.android.buddy.bytebuddy

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.utils.ConcatIterator
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import java.util.jar.Manifest

@AutoFactory
class CompoundSource(
    @Provided byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val sourceOrigins: Set<Plugin.Engine.Source.Origin>
) : Plugin.Engine.Source,
    Plugin.Engine.Source.Origin {

    private val locator: ClassFileLocator by lazy {
        byteBuddyClassesInstantiator.makeCompoundClassFileLocator(sourceOrigins.map { it.classFileLocator })
    }

    override fun read(): Plugin.Engine.Source.Origin = this

    override fun getManifest(): Manifest? = Plugin.Engine.Source.Origin.NO_MANIFEST

    override fun iterator(): MutableIterator<Plugin.Engine.Source.Element> {
        return ConcatIterator(sourceOrigins.map {
            it.iterator()
        }.toMutableList())
    }

    override fun close() {
        sourceOrigins.forEach {
            it.close()
        }
    }

    override fun getClassFileLocator(): ClassFileLocator = locator
}