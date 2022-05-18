package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.utils.ConcatIterator
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import net.bytebuddy.build.Plugin
import net.bytebuddy.dynamic.ClassFileLocator
import java.io.File
import java.util.jar.Manifest

class SourceOriginForMultipleFolders @AssistedInject constructor(
    private val folderIteratorFactory: FolderIterator.Factory,
    private val byteBuddyClassesInstantiator: ByteBuddyClassesInstantiator,
    @Assisted private val folders: Set<File>
) : Plugin.Engine.Source.Origin {

    @AssistedFactory
    interface Factory {
        fun create(folders: Set<File>): SourceOriginForMultipleFolders
    }

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