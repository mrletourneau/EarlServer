package com.mrletourneau.earl

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.*
import java.net.ServerSocket
import kotlin.test.assertFailsWith

internal class EarlServerTest {
    private val earlServer: EarlServer

    init {
        val serverSocket = ServerSocket()
        earlServer = EarlServer(serverSocket)
    }

    @Test
    fun `header happy path`() {
        // Test calls to root
        assertEquals("/index.gmi", getPathTestHelper("gemini://localhost/"))
        assertEquals("/index.gmi", getPathTestHelper("gemini://localhost"))
        assertEquals("/index.gmi", getPathTestHelper("gemini://localhost:1965/"))
        assertEquals("/index.gmi", getPathTestHelper("gemini://localhost:1965"))

        assertEquals("/test.gmi", getPathTestHelper("gemini://localhost/test.gmi"))
        assertEquals("/test", getPathTestHelper("gemini://localhost/test"))

        // TODO: Currently unsupported -- https://github.com/mrletourneau/EarlServer/issues/6
        // assertEquals("/test/index.gmi", getPathTestHelper("gemini://localhost/test/"))
    }

    @Test
    fun `header unhappy path`() {
        assertFailsWith<IOException> {
            getPathTestHelper("")
        }

        assertFailsWith<IOException> {
            getPathTestHelper("test")
        }

        assertFailsWith<IOException> {
            getPathTestHelper("http://test")
        }

        assertFailsWith<IOException> {
            getPathTestHelper("af3*#Hfjd1::fdf3")
        }
    }

    private fun getPathTestHelper(rawHeader: String): String {
        val header = BufferedReader(StringReader(rawHeader))
        return earlServer.getPath(header)
    }

    @Test
    fun `mime types should be correctly detected`() {
        testFileType("test.gmi", "text/gemini")
        testFileType("test.txt", "text/plain")
        testFileType("test", "text/plain")
    }

    private fun testFileType(fileName: String, expectedMimeType: String) {
        var file = File(getPath(fileName))
        assertEquals(earlServer.getMimeType(file), expectedMimeType)
    }

    private fun getPath(fileName: String): String {
        return "src/test/resources/${fileName}"
    }
}