package com.likethesalad.android.common.base

import com.likethesalad.android.common.providers.AndroidExtensionProvider
import com.likethesalad.android.common.providers.ProjectDependencyToolsProvider
import com.likethesalad.android.common.providers.ProjectLoggerProvider

interface BuddyPlugin : ProjectLoggerProvider, ProjectDependencyToolsProvider, AndroidExtensionProvider