package com.likethesalad.android.buddy.bytebuddy

import com.likethesalad.android.buddy.bytebuddy.utils.ByteBuddyClassesMaker
import net.bytebuddy.build.Plugin
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SourceAndTargetProvider @Inject constructor(
    private val sourceForMultipleFoldersFactory: SourceForMultipleFoldersFactory,
    private val byteBuddyClassesMaker: ByteBuddyClassesMaker
) {
    fun getSource(folders: Set<File>): Plugin.Engine.Source {
        return sourceForMultipleFoldersFactory.create(folders)
    }

    fun getTarget(folder: File): Plugin.Engine.Target {
        return byteBuddyClassesMaker.makeTargetForFolder(folder)
    }
}