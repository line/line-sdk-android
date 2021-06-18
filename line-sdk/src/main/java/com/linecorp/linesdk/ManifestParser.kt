package com.linecorp.linesdk

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.annotation.RestrictTo
import com.linecorp.linesdk.api.LineEnvConfig

private const val LINE_ENV_CONFIG = "LineEnvConfig"

/**
 * @hide
 * */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ManifestParser {

    fun parse(context: Context): LineEnvConfig? {
        return runCatching {
            context.packageManager
                .getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
                .metaData
                ?.getString(LINE_ENV_CONFIG)
                ?.let { className ->
                    parseEnvConfig(className)
                }
        }.getOrNull()
    }

    private fun parseEnvConfig(className: String): LineEnvConfig {
        val clazz: Class<*>
        try {
            clazz = Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw IllegalArgumentException("Unable to find LineEnvConfig implementation", e)
        }

        val config: Any
        try {
            config = clazz.newInstance()
        } catch (e: ReflectiveOperationException) {
            throw RuntimeException(
                "Unable to instantiate LineEnvConfig implementation for $clazz", e
            )
        }
        if (config !is LineEnvConfig) {
            throw RuntimeException("Expected instanceof LineEnvConfig, but found: $config")
        }
        return config
    }
}
