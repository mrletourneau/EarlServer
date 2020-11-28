package EarlServer

import java.io.FileInputStream
import java.util.*


object Config {
        lateinit var EARL_CERT_PASS_PHRASE: String
        lateinit var EARL_DOCUMENT_ROOT: String
        lateinit var EARL_KEY_STORE: String
        var EARL_PORT: Int = 1965

    fun loadConfig(configFile: String = defaultConfigLocation()) {
        FileInputStream(configFile).use { input ->
            val prop = Properties()
            // load a properties file
            prop.load(input)

            EARL_CERT_PASS_PHRASE = prop.getProperty("CERT_PASS_PHRASE")
            EARL_DOCUMENT_ROOT = prop.getProperty("DOCUMENT_ROOT") ?: "/var/earl-server"
            EARL_KEY_STORE = prop.getProperty("KEY_STORE_LOCATION")
            EARL_PORT = if (!prop.getProperty("PORT").isNullOrEmpty()) prop.getProperty("PORT").toInt() else EARL_PORT
        }
    }

    private fun defaultConfigLocation() = "${System.getProperty("user.home")}/.earl_config"
}