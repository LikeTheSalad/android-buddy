package com.likethesalad.android.buddy.modules.customconfig.data

enum class ConfigurationGroup(
    val bucketType: ConfigurationType,
    val resolvableType: ConfigurationType
) {
    COMPILE_GROUP(
        ConfigurationType.API,
        ConfigurationType.COMPILE_CLASSPATH
    ),
    RUNTIME_GROUP(
        ConfigurationType.IMPLEMENTATION,
        ConfigurationType.RUNTIME_CLASSPATH
    )
}