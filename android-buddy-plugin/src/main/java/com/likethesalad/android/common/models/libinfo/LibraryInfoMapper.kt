package com.likethesalad.android.common.models.libinfo

import com.likethesalad.android.common.models.libinfo.utils.AndroidBuddyLibraryInfoFqnBuilder
import com.likethesalad.android.common.utils.ByteArrayClassLoaderUtil
import net.bytebuddy.ByteBuddy
import net.bytebuddy.description.modifier.Visibility
import net.bytebuddy.implementation.FixedValue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryInfoMapper
@Inject constructor(
    private val fqnBuilder: AndroidBuddyLibraryInfoFqnBuilder,
    private val byteArrayClassLoaderUtil: ByteArrayClassLoaderUtil
) {

    companion object {
        private const val GET_ID_METHOD_NAME = "getId"
        private const val GET_GROUP_METHOD_NAME = "getGroup"
        private const val GET_NAME_METHOD_NAME = "getName"
        private const val GET_PLUGIN_NAMES_METHOD_NAME = "getPluginNames"
    }

    fun convertToClassByteArray(info: AndroidBuddyLibraryInfo): NamedClassInfo {
        val name = fqnBuilder.buildFqn(info)
        val pluginNamesString = when {
            info.pluginNames.isEmpty() -> ""
            else -> info.pluginNames.joinToString(",")
        }
        val byteArray = ByteBuddy()
            .subclass(Any::class.java)
            .name(name)
            .defineMethod(GET_ID_METHOD_NAME, String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.id))
            .defineMethod(GET_GROUP_METHOD_NAME, String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.group))
            .defineMethod(GET_NAME_METHOD_NAME, String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(info.name))
            .defineMethod(GET_PLUGIN_NAMES_METHOD_NAME, String::class.java, Visibility.PUBLIC)
            .intercept(FixedValue.value(pluginNamesString))
            .make()
            .bytes

        return NamedClassInfo(name, byteArray)
    }

    fun convertToAndroidBuddyLibraryInfo(namedClassInfo: NamedClassInfo): AndroidBuddyLibraryInfo {
        val classType = byteArrayClassLoaderUtil.loadClass(namedClassInfo.name, namedClassInfo.byteArray)
        val instance = classType.newInstance()
        val id = callNoArgsStringMethodByReflection(GET_ID_METHOD_NAME, instance)
        val group = callNoArgsStringMethodByReflection(GET_GROUP_METHOD_NAME, instance)
        val name = callNoArgsStringMethodByReflection(GET_NAME_METHOD_NAME, instance)
        val pluginNamesString = callNoArgsStringMethodByReflection(GET_PLUGIN_NAMES_METHOD_NAME, instance)
        val pluginNames = when {
            pluginNamesString.isEmpty() -> emptySet()
            else -> pluginNamesString.split(",").toSet()
        }

        return AndroidBuddyLibraryInfo(id, group, name, pluginNames)
    }

    private fun callNoArgsStringMethodByReflection(
        name: String,
        fromInstance: Any
    ): String {
        return fromInstance.javaClass.getDeclaredMethod(name).invoke(fromInstance) as String
    }
}