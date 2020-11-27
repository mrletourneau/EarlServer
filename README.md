# Earl Gemini Server

A simple Gemini server written in Kotlin. Currently in extreme alpha.

## Generating a certificate
In the `resources` folder in the root of this project, run the following command:
```
keytool -genkey -alias <alias> -keyalg RSA -keypass <key passphrase> -storepass <store passphrase> -keystore keystore.jks
```
When prompted for "First and Last Name", enter your hostname.

## Running the server via intellij

### 1. Set Configuration options as environment variables

- `EARL_CERT_PASS_PHRASE` - this should match your pass phrase of your cert
- `EARL_DOCUMENT_ROOT` - where your files will be served from
- `EARL_PORT` - the port you wish to serve from. Defaults to 1965

### 2. Running

Simply run the `main` function within `Earl.kt`.

## Links for learnin'

- Kotlin socket server - https://gist.github.com/Silverbaq/a14fe6b3ec57703e8cc1a63b59605876
- Gemini spec - https://gemini.circumlunar.space/docs/specification.html
- Java TLS example - https://docs.oracle.com/javase/10/security/sample-code-illustrating-secure-socket-connection-client-and-server.htm#GUID-3561ED02-174C-4E65-8BB1-5995E9B7282C__CLASSFILESERVER.JAVA-3314B74B