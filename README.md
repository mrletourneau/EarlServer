# Earl Gemini Server

## Generating a certificate
```
keytool -genkey -alias <alias> -keyalg RSA -keypass <key passphrase> -storepass <store passphrase> -keystore keystore.jks
```
## Links for learnin'
- Kotlin socket server - https://gist.github.com/Silverbaq/a14fe6b3ec57703e8cc1a63b59605876
- Gemini spec - https://gemini.circumlunar.space/docs/specification.html
- Java TLS example - https://docs.oracle.com/javase/10/security/sample-code-illustrating-secure-socket-connection-client-and-server.htm#GUID-3561ED02-174C-4E65-8BB1-5995E9B7282C__CLASSFILESERVER.JAVA-3314B74B