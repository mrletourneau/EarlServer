package com.mrletourneau.earl

import java.io.*
import java.net.ServerSocket
import java.net.Socket


/*
 * ClassServer.java -- a simple file server that can serve
 * Http get request in both clear and secure channel
 */

/*
 * ClassServer.java -- a simple file server that can serve
 * Http get request in both clear and secure channel
 */
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
                // retrieve bytecodes
                val bytes = "Hello Mackenize!!".toByteArray()
                try {
                    // Success header
                    out.print("20 text/plain\r\n")
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

    /**
     * Create a new thread to listen.
     */
    private fun newListener() {
        Thread(this).start()
    }

    /**
     * Constructs a ClassServer based on **ss** and
     * obtains a file's bytecodes using the method **getBytes**.
     *
     */
    init {
        server = ss
        newListener()
    }
}