package com.likethesalad.android.buddylib.modules.createmetadata.data

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.buddylib.providers.ProjectInfoProvider
import javax.inject.Inject

@LibraryScope
class LibraryGnvProviders @Inject constructor(
    private val projectInfoProvider: ProjectInfoProvider
) {
    val groupProvider: () -> String = { projectInfoProvider.getGroup() }
    val nameProvider: () -> String = { projectInfoProvider.getName() }
    val versionProvider: () -> String = { projectInfoProvider.getVersion() }
}

