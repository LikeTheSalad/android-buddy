package com.likethesalad.android.buddy.modules.customconfig.data

enum class ConfigurationGroup(
    val bucketType: ConfigurationType,
    val consumableType: ConfigurationType,
    val resolvableType: ConfigurationType
) {
    API_GROUP(
        ConfigurationType.API,
        ConfigurationType.API_ELEMENTS,
        ConfigurationType.API_CLASSPATH
    ),
    RUNTIME_GROUP(
        ConfigurationType.IMPLEMENTATION,
        ConfigurationType.RUNTIME_ELEMENTS,
        ConfigurationType.RUNTIME_CLASSPATH
    )
}