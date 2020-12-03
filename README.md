# Earl Gemini Server

A simple Gemini server written in Kotlin. Currently in alpha.

## Generating a certificate
To generate the key, run the following command (requires keytool):
```
keytool -genkey -alias <alias> -keyalg RSA -keypass <key passphrase> -storepass <store passphrase> -keystore keystore.jks
```
When prompted for "First and Last Name", enter your hostname.

## Running the server

1. Copy `earl_config_sample` to desired directory. By default, Earl will search `~/.earl_config`
2. From the project root directory, run `./gradlew run`. If you chose to put your config in a different directory, you can specify that directory by running `./gradlew run --args='/path/to/.earl_config'`

## Building the server

Simply run `./gradlew build`. The resultant distributable will be located in `earl/build/distributions`. To start Earl, simply run `bin/earl` from the root of the extracted distributable bundle.

## Links for learnin'

- [Gemini spec](https://gemini.circumlunar.space/docs/specification.html)
- [Java TLS example](https://docs.oracle.com/javase/10/security/sample-code-illustrating-secure-socket-connection-client-and-server.htm#GUID-3561ED02-174C-4E65-8BB1-5995E9B7282C__CLASSFILESERVER.JAVA-3314B74B)
