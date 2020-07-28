package com.likethesalad.android.buddy

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.likethesalad.android.buddy.di.AppInjector
import com.likethesalad.android.buddy.providers.AndroidExtensionProvider
import com.likethesalad.android.buddy.providers.FileTreeIteratorProvider
import com.likethesalad.android.buddy.providers.GradleConfigurationsProvider
import com.likethesalad.android.common.base.BuddyPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.logging.Logger
import java.io.File

@Suppress("UnstableApiUsage")
open class AndroidBuddyPlugin : Plugin<Project>, BuddyPlugin, FileTreeIteratorProvider,
    AndroidExtensionProvider, GradleConfigurationsProvider {

    private lateinit var project: Project
    private lateinit var androidExtension: BaseExtension

    override fun apply(project: Project) {
        this.project = project
        androidExtension = project.extensions.getByType(AppExtension::class.java)
        AppInjector.init(this)
        AppInjector.getCustomConfigurationCreator().createAndroidBuddyConfigurations()
        AppInjector.getDependencyHandlerUtil().addDependencies()
        androidExtension.registerTransform(AppInjector.getByteBuddyTransform())
        AppInjector.getCustomConfigurationVariantSetup().arrangeConfigurationsPerVariant()
    }

    override fun createFileTreeIterator(folder: File): Iterator<File> {
        return project.fileTree(folder).iterator()
    }

    override fun getLogger(): Logger {
        return project.logger
    }

    override fun getDependencyHandler(): DependencyHandler {
        return project.dependencies
    }

    override fun getRepositoryHandler(): RepositoryHandler {
        return project.repositories
    }

    override fun getAndroidExtension(): BaseExtension {
        return androidExtension
    }

    override fun getConfigurationContainer(): ConfigurationContainer {
        return project.configurations
    }
}