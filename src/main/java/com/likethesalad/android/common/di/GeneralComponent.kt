package com.likethesalad.android.common.di

import com.likethesalad.android.buddylib.actions.VerifyPluginClassesProvidedActionFactory
import com.likethesalad.android.common.utils.DirectoryCleaner
import com.likethesalad.android.common.utils.InstantiatorWrapper
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface GeneralComponent {
    fun verifyPluginClassesProvidedActionFactory(): VerifyPluginClassesProvidedActionFactory

    fun instantiatorWrapper(): InstantiatorWrapper

    fun directoryCleaner(): DirectoryCleaner
}