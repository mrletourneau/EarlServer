package com.mrletourneau.earl

import java.io.*
import java.io.File
import java.net.ServerSocket
import java.net.Socket
import java.net.URI


class EarlServer constructor(ss: ServerSocket?) : Runnable {
    private var server: ServerSocket? = null

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
        // create a new thread to accept the next connection
        newListener()
        try {
            println("${socket.inetAddress.hostAddress} connected")
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
                val bytes = getBytes(path!!)

                try {
                    // Success header
                    out.print("20 text/gemini; lang=en\r\n")
                    out.flush()
                    rawOut.write(bytes)
                    rawOut.flush()
                } catch (ie: IOException) {
                    ie.printStackTrace()
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // write out error response
                out.println("40 " + e.message + "\r\n")
                out.flush()
            }
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

    @Throws(IOException::class)
    private fun getPath(`in`: BufferedReader): String? {
        val uri = URI(`in`.readLine())
        var path = uri.rawPath

        if (path == "/") path = "/index.gmi"

        return if (path.isNotEmpty()) {
            path
        } else {
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
    fun getBytes(path: String): ByteArray? {
        println("reading: $path")
        val f = File(EARL_DOCUMENT_ROOT + File.separator + path)
        val length: Int = f.length().toInt()
        return if (length == 0) {
            throw IOException("File length is zero: $path")
        } else {
            val fin = FileInputStream(f)
            val `in` = DataInputStream(fin)
            val bytecodes = ByteArray(length)
            `in`.readFully(bytecodes)
            bytecodes
        }
    }

    /**
     * Create a new thread to listen.
     */
    private fun newListener() {
        Thread(this).start()
    }

    init {
        server = ss
        newListener()
    }
}