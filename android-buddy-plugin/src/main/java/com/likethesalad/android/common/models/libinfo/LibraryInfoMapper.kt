package com.likethesalad.android.common.models.libinfo

import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.FixedValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryInfoMapper @Inject constructor(private val fqnBuilder: AndroidBuddyLibraryInfoFqnBuilder) {

    fun convertToClassByteArray(info: AndroidBuddyLibraryInfo): ByteArray {
        return ByteBuddy()
            .subclass(Any::class.java)
            .name(fqnBuilder.buildFqn(info))
            .defineMethod("getId", String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.id))
            .defineMethod("getGroup", String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.group))
            .defineMethod("getName", String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.name))
            .defineMethod("getPluginNames", String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.pluginNames.joinToString(",")))
            .make()
            .bytes
    }
}