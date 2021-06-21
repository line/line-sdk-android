package com.linecorp.linesdk

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import com.linecorp.linesdk.api.LineEnvConfig
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val MODULE_VALUE = "LineEnvConfig"
private const val PACKAGE_NAME_SDK_CLIENT = "com.linecorp.linesdktest"
private const val TEST_API_SERVER_BASE_URI = "https://api-test"

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [TestConfig.TARGET_SDK_VERSION])
internal class ManifestParserTest {

    private class InvalidClass

    private class NewEnvConfig : LineEnvConfig() {
        override val apiServerBaseUri = TEST_API_SERVER_BASE_URI
    }

    private val mockContext = mock(Context::class.java)

    private val parser = ManifestParser()
    private val applicationInfo = ApplicationInfo()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        `when`(mockContext.packageName).thenReturn(PACKAGE_NAME_SDK_CLIENT)

        val mockPackageManager = mock(PackageManager::class.java)
        `when`(
            mockPackageManager.getApplicationInfo(
                eq(PACKAGE_NAME_SDK_CLIENT),
                eq(PackageManager.GET_META_DATA)
            )
        )
            .thenReturn(applicationInfo)

        `when`(mockContext.packageManager).thenReturn(mockPackageManager)
    }

    @Test
    fun `parse returns NewEnvConfig if it is in metadata`() {
        applicationInfo.metaData = Bundle().apply {
            putString(MODULE_VALUE, NewEnvConfig::class.java.name)
        }

        Assert.assertNotNull(parser.parse(mockContext))

        Assert.assertEquals(
            NewEnvConfig().apiServerBaseUri,
            parser.parse(mockContext)!!.apiServerBaseUri
        )
    }

    @Test
    fun `parse returns null if no metadata`() {
        applicationInfo.metaData = Bundle()

        Assert.assertNull(parser.parse(mockContext))
    }

    @Test
    fun `parse returns null if no correct key in metadata`() {
        applicationInfo.metaData = Bundle().apply {
            putString("wrong_key", NewEnvConfig::class.java.name)
        }

        Assert.assertNull(parser.parse(mockContext))
    }

    @Test
    fun `parse returns null if no LineEnvConfig is provided in metadata`() {
        applicationInfo.metaData = Bundle().apply {
            putString(MODULE_VALUE, InvalidClass::class.java.name)
        }

        Assert.assertNull(parser.parse(mockContext))
    }

}
