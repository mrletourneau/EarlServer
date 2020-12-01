package com.mrletourneau.earl

import junit.framework.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.net.ServerSocket
import java.nio.file.Paths

internal class EarlServerTest {
    private val earlServer: EarlServer

    init {
        val serverSocket = ServerSocket()
        earlServer = EarlServer(serverSocket)
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
        return "${Paths.get("").toAbsolutePath()}/earl/src/test/resources/${fileName}"
    }
}