package com.mrletourneau.earl

import java.io.FileInputStream
import java.io.IOException
import java.security.KeyStore
import javax.net.ServerSocketFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocketFactory

val EARL_DOCUMENT_ROOT = System.getenv("EARL_DOCUMENT_ROOT") ?: "./"
val EARL_PORT = (System.getenv("EARL_DOCUMENT_ROOT") ?: "1965").toInt()

fun main() {
    println("Hello (from) Earl!")
    try {
        val ssf: ServerSocketFactory = sslSocketServerFactory()
        val ss = ssf.createServerSocket(EARL_PORT)
        EarlServer(ss)
    } catch (e: IOException) {
        println(
            "Unable to start ClassServer: " +
                    e.message
        )
        e.printStackTrace()
    }
}
fun sslSocketServerFactory(): SSLServerSocketFactory {
    val ssf: SSLServerSocketFactory

    // set up key manager to do server authentication
    val passPhrase = getPassPhrase()

    val ctx = SSLContext.getInstance("TLS")
    val kmf = KeyManagerFactory.getInstance("SunX509")
    val ks = KeyStore.getInstance("JKS")

    ks.load(FileInputStream ("resources/keystore.jks"), passPhrase)
    kmf.init(ks, passPhrase)
    ctx.init(kmf.keyManagers, null, null)

    ssf = ctx.serverSocketFactory

    return ssf
}

private fun getPassPhrase() = System.getenv("EARL_CERT_PASS_PHRASE").toCharArray()