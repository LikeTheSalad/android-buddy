package com.likethesalad.android.buddylib.modules.createmetadata.utils

import com.likethesalad.android.buddylib.di.LibraryScope
import com.likethesalad.android.common.models.AndroidBuddyLibraryInfo
import com.likethesalad.android.common.utils.Constants
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers
import javax.inject.Inject

@LibraryScope
class LibraryInfoMapper @Inject constructor() {
    // Todo move this to common and make it able to map info to bytearray and class to info

    fun convertToClassByteArray(info: AndroidBuddyLibraryInfo): ByteArray {
        val bytes = ByteBuddy().subclass(Any::class.java)
            .name(Constants.PLUGINS_METADATA_CLASS_NAME)
            .method(ElementMatchers.isToString())
            .intercept(FixedValue.value(pluginNames.joinToString(",")))
            .make()
            .bytes
    }
}