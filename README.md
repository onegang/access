[![Build Status](https://travis-ci.org/onegang/access.svg?branch=master)](https://travis-ci.org/onegang/access)

# Access

A prototype to manage users access.

[Demo (live master)](https://tinyurl.com/yaqqsddc)


## Development
- Start Kakfa
 - `bin\windows\zookeeper-server-start.bat config\zookeeper.properties`
 - `bin\windows\kafka-server-start.bat config\server.properties`
- Start Kafka monitoring tool (Kafdrop)
 - Download binary: `https://github.com/obsidiandynamics/kafdrop/releases`
 - Launch cmd
 - Run `set PATH="C:\Program Files\Java\jdk-11.0.7\bin"`
 - Run `java --add-opens=java.base/sun.nio.ch=ALL-UNNAMED -jar kafdrop-3.26.0.jar`
- Run `org.onegang.access.MainApplication` with the arguments `-Djasypt.encryptor.password=ENCRYPTOR -Dspring.profiles.active=dev,kafka`
 - `dev`: Allow loading of mock data into database
 - `kafka`: Allow broadcast of request changes to consumers 