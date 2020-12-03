package com.mrletourneau.earl

import java.io.*
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.net.URI
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*


class EarlServer constructor(serverSocket: ServerSocket?) : Runnable {
    private var server: ServerSocket? = null

    data class Transaction(
        var ip: String?,
        var request: String?,
        var response: String
    )

    data class FileData(val bytes: ByteArray, val mimeType: String)

    /**
     * The "listen" thread that accepts a connection to the
     * server, parses the header to obtain the file name
     * and sends back the bytes for the file (or error
     * if the file is not found or the response was malformed).
     */
    override fun run() {
        val socket: Socket
        // accept a connection
        socket = try {
            server!!.accept()
        } catch (e: IOException) {
            println("Class Server died: " + e.message)
            e.printStackTrace()
            return
        }
        val transaction = Transaction(ip = socket.inetAddress.hostAddress, response = "", request = null)
        // create a new thread to accept the next connection
        newListener()
        try {
            val rawOut = socket.getOutputStream()
            val out = PrintWriter(
                BufferedWriter(
                    OutputStreamWriter(
                        rawOut
                    )
                )
            )
            try {
                val `in` = BufferedReader(
                    InputStreamReader(socket.getInputStream())
                )
                val path = getPath(`in`)

                transaction.request = path

                val fileData = getFile(path)

                try {
                    val response = "20 ${fileData.mimeType}; lang=en\r\n"
                    transaction.response = response
                    // Success header
                    out.print(response)
                    out.flush()
                    rawOut.write(fileData.bytes)
                    rawOut.flush()
                } catch (ie: IOException) {
                    ie.printStackTrace()
                    return
                }
            } catch (e: Exception) {
                // e.printStackTrace()
                // write out error response
                val response = "40 " + e.message + "\r\n"
                transaction.response = response
                out.println(response)
                out.flush()
            }
            logTransaction(transaction)
        } catch (ex: IOException) { // eat exception (could log error to log file, but
// write out to stdout for now).
            println("error writing response: " + ex.message)
            ex.printStackTrace()
        } finally {
            try {
                socket.close()
            } catch (e: IOException) {
            }
        }
    }

    fun now():String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").format(Date.from(Instant.now()))
    }

    private fun logTransaction(transaction: Transaction) {
        println("${now()}\t\t${transaction.ip}\t\t${transaction.request}\t\t${transaction.response.trim()}")
    }

    @Throws(IOException::class)
    fun getPath(`in`: BufferedReader): String {
        try {
            val rawUri = `in`.readLine()

            if (rawUri.isNullOrEmpty()) {
                throw IOException("Invalid URI, empty request")
            }

            val uri = URI(rawUri)

            if (uri.scheme == null) {
                throw IOException("Missing scheme (no \"gemini://\" URI component found)")
            }

            if (!uri.scheme.equals("gemini", ignoreCase = true)) {
                throw IOException("Invalid scheme. Authority must match \"gemini\". Found \"${uri.scheme}\"")
            }

            var path = uri.rawPath

            if (path == "/" || path.isEmpty()) path = "/index.gmi"

            return path
        }
        catch(e: Exception) {
            e.printStackTrace()
            throw IOException("Malformed Header")
        }
    }

    /**
     * Returns an array of bytes containing the bytes for
     * the file represented by the argument **path**.
     *
     * @return the bytes for the file
     * @exception FileNotFoundException if the file corresponding
     * to **path** could not be loaded.
     */
    @Throws(IOException::class)
    fun getFile(path: String): FileData {
        val f = File(Config.EARL_DOCUMENT_ROOT + File.separator + path)
        val length: Int = f.length().toInt()
        return if (length == 0) {
            throw IOException("File not found")
        } else {
            val fin = FileInputStream(f)
            val `in` = DataInputStream(fin)
            val bytes = ByteArray(length)
            `in`.readFully(bytes)

            return FileData(bytes, getMimeType(f))
        }
    }

    private fun isGeminiFile(fileName: String) = fileName.endsWith(".gmi") || fileName.endsWith(".gemini")

    fun getMimeType(file: File): String {
        val path = file.toPath()

        if (isGeminiFile(file.name)) return "text/gemini"

        return when (val mimeType = Files.probeContentType(path)) {
            null -> "text/plain"
            else -> mimeType
        }
    }

    /**
     * Create a new thread to listen.
     */
    fun newListener() {
        Thread(this).start()
    }

    init {
        server = serverSocket
    }
}
