package com.mrletourneau.earl

import java.io.FileInputStream
import java.io.IOException
import java.security.KeyStore
import javax.net.ServerSocketFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocketFactory

fun main() {
    println("Hello (from) Earl!")
    try {
        val ssf: ServerSocketFactory = sslSocketServerFactory()
        val ss = ssf.createServerSocket(1965)
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
    val passPhrase = "henrimarcelannabellamarie".toCharArray()

    val ctx = SSLContext.getInstance("TLS")
    val kmf = KeyManagerFactory.getInstance("SunX509")
    val ks = KeyStore.getInstance("JKS")

    ks.load(FileInputStream ("resources/keystore.jks"), passPhrase)
    kmf.init(ks, passPhrase)
    ctx.init(kmf.keyManagers, null, null)

    ssf = ctx.serverSocketFactory

    return ssf
}