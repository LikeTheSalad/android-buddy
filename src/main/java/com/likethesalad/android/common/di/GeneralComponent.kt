package com.likethesalad.android.common.di

import com.likethesalad.android.buddylib.actions.VerifyPluginClassesProvidedActionFactory
import com.likethesalad.android.common.providers.ProjectLoggerProvider
import com.likethesalad.android.common.utils.ByteBuddyDependencyHandler
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.InstantiatorWrapper
import com.likethesalad.android.common.utils.Logger
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [GeneralModule::class])
interface GeneralComponent {
    fun verifyPluginClassesProvidedActionFactory(): VerifyPluginClassesProvidedActionFactory

    fun instantiatorWrapper(): InstantiatorWrapper

    fun directoryCleaner(): DirectoryCleaner

    fun projectLoggerProvider(): ProjectLoggerProvider

    fun logger(): Logger

    fun byteBuddyDependencyHandler(): ByteBuddyDependencyHandler
}