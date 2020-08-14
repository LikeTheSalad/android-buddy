package com.likethesalad.android.buddy

import com.android.build.gradle.BaseExtension
import com.likethesalad.android.buddy.di.AppInjector
import com.likethesalad.android.buddy.extension.AndroidBuddyExtension
import com.likethesalad.android.buddy.providers.AndroidBuddyExtensionProvider
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
    GradleConfigurationsProvider, AndroidBuddyExtensionProvider {

    private lateinit var project: Project
    private lateinit var androidExtension: BaseExtension
    private lateinit var androidBuddyExtension: AndroidBuddyExtension

    companion object {
        private const val EXTENSION_NAME = "androidBuddy"
    }

    override fun apply(project: Project) {
        this.project = project
        androidBuddyExtension = project.extensions.create(EXTENSION_NAME, AndroidBuddyExtension::class.java)
        androidExtension = project.extensions.getByType(BaseExtension::class.java)
        AppInjector.init(this)
        AppInjector.getCustomConfigurationCreator().createAndroidBuddyConfigurations()
        AppInjector.getDependencyHandlerUtil().addDependencies()
        AppInjector.getCustomConfigurationVariantSetup().arrangeConfigurationsPerVariant()

        androidExtension.registerTransform(AppInjector.getByteBuddyTransform())
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

    override fun getAndroidBuddyExtension(): AndroidBuddyExtension {
        return androidBuddyExtension
    }
}