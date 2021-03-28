package com.likethesalad.android.buddy.bytebuddy

import com.google.auto.factory.AutoFactory
import com.google.auto.factory.Provided
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import com.likethesalad.android.buddy.utils.ConcatIterator
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import java.io.File
import java.util.jar.Manifest

@AutoFactory
class SourceOriginForMultipleFolders(
    @Provided private val folderIteratorFactory: FolderIteratorFactory,
    @Provided private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    private val folders: Set<File>
) : Plugin.Engine.Source.Origin {

    private val elementIterator: MutableIterator<Plugin.Engine.Source.Element> by lazy {
        val folderIterators = folders.map {
            folderIteratorFactory.create(it)
        }.toMutableList()

        ConcatIterator(folderIterators)
    }

    private val locator: ClassFileLocator by lazy {
        val locators = folders.map {
            byteBuddyClassesInstantiator.makeFolderClassFileLocator(it)
        }
        byteBuddyClassesInstantiator.makeCompoundClassFileLocator(locators)
    }

    override fun getManifest(): Manifest? = Plugin.Engine.Source.Origin.NO_MANIFEST

    override fun iterator(): MutableIterator<Plugin.Engine.Source.Element> = elementIterator

    override fun close() {
        // Nothing to do here 🚀(._.)
    }

    override fun getClassFileLocator(): ClassFileLocator = locator
}