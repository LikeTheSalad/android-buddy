package com.likethesalad.android.common.di

import com.likethesalad.android.common.models.libinfo.LibraryInfoMapper
import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import com.likethesalad.android.common.utils.DependencyHandlerUtil
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.Logger
import com.likethesalad.android.common.utils.android.AndroidExtensionDataProvider
import com.likethesalad.android.common.utils.bytebuddy.ByteBuddyClassesInstantiator
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [GeneralModule::class])
interface GeneralComponent {

    fun instantiatorWrapper(): InstantiatorWrapper
    fun directoryCleaner(): DirectoryCleaner
    fun projectLoggerProvider(): ProjectLoggerProvider
    fun projectDependencyToolsProvider(): ProjectDependencyToolsProvider
    fun logger(): Logger
    fun dependencyHandlerUtil(): DependencyHandlerUtil
    fun androidExtensionDataProvider(): AndroidExtensionDataProvider
    fun byteBuddyClassesInstantiator(): ByteBuddyClassesInstantiator
    fun byteArrayClassLoaderUtil(): ByteArrayClassLoaderUtil
    fun androidBuddyLibraryInfoFqnBuilder(): AndroidBuddyLibraryInfoFqnBuilder
    fun libraryInfoMapper(): LibraryInfoMapper
}