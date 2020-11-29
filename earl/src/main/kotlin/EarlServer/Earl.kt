package EarlServer

import EarlServer.Config.loadConfig
import java.io.FileInputStream
import java.io.IOException
import java.security.KeyStore
import javax.net.ServerSocketFactory
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLServerSocketFactory

fun main(args: Array<String>) {
    println("Hello (from) Earl!\n")

    println("Loading config...")

    if (args.isNotEmpty())
        loadConfig(args.first())
    else
        loadConfig()

    println("Config loaded!\n")
    println("Serving on port ${Config.EARL_PORT}...\n\n")

    try {
        val ssf: ServerSocketFactory = sslSocketServerFactory()
        val ss = ssf.createServerSocket(Config.EARL_PORT)

        EarlServer(ss)
    } catch (e: IOException) {
        println(
            "Unable to start Earl: " +
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

    ks.load(FileInputStream (Config.EARL_KEY_STORE), passPhrase)
    kmf.init(ks, passPhrase)
    ctx.init(kmf.keyManagers, null, null)

    ssf = ctx.serverSocketFactory

    return ssf
}

private fun getPassPhrase() = Config.EARL_CERT_PASS_PHRASE.toCharArray()