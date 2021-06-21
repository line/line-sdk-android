package com.linecorp.linesdk

import android.content.Context
import android.content.pm.PackageManager
import androidx.annotation.CheckResult
import androidx.annotation.RestrictTo
import com.linecorp.linesdk.api.LineEnvConfig

private const val LINE_ENV_CONFIG = "LineEnvConfig"

/**
 * Parse [LineEnvConfig] reference out of the AndroidManifest file.
 *
 * @hide
 * */
@RestrictTo(RestrictTo.Scope.LIBRARY)
internal class ManifestParser {

    /**
     * Parse meta-data value in AndroidManifest.xml, retrieve a [LineEnvConfig] instance if key
     * *LineEnvConfig* is presented in the meta-data.
     *
     * @return *null* if something went wrong or a [LineEnvConfig] instance instead.
     * */
    @CheckResult
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
