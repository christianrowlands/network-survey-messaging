# network-survey-messaging
Defines the messages that are sent from the Network Survey Android App

Network Survey Messaging
===============
This repository defines the messages that are sent out from the [Network Survey Android App](https://github.com/christianrowlands/android-network-survey). 
Protobuf is being used to define the message schemas, since it is platform-agnostic, and gRPC is being utilized as
the transport mechanism, since it allows the software on each side to abstract the messages and use method calls. The 
Network Survey Android App acts as the gRPC client; therefore, this library should be used to create a server that consumes 
the messages from Network Survey Android App.

The build produces a Java library with all of the messages for use by the client and server software 
respectively.

More information about protobuf is available [here](https://developers.google.com/protocol-buffers/).
More information about gRPC is available [here](https://grpc.io/).

**Readme Contents**
- [Sample usage](#sample-usage)
- [Build and development instructions](#build-and-development-instructions)
- [Change log](#change-log)
- [Contact](#contact)


Sample usage
-----------------------------------
The gRPC website has tutorials for utilizing gRPC + protobuf for [Android](https://grpc.io/docs/quickstart/android/) and
[Java](https://grpc.io/docs/quickstart/java/).


Getting Started
-----------------------------------
#### Add Network Survey Messaging to your project

Network Survey Messaging is available via [mavenCentral](https://search.maven.org/search?q=network-survey-messaging)

```groovy
dependencies {
    implementation 'com.craxiom:network-survey-messaging:0.1.1'
}
```


Build and development instructions
-----------------------------------
#### Building the project
 - Clone the repository
 - Execute `gradlew build` in the root directory to produce the Java library with the protobuf messages.
 - Execute `gradlew publishToMavenLocal` in the root directory to publish the artifacts to the local Maven cache.


Change log
-----------------------------------
##### [0.1.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.1.1) - 2020-05-23
 * Added a protobuf message for 802.11 beacon frames

##### [0.1.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.1.0) - 2020-04-23
 * Switched to the full java version instead of the java lite protobuf implementation

##### [0.0.2](https://github.com/christianrowlands/network-survey-messaging/releases/tag/release-0.0.2) - 2020-01-06
 * Added support for streaming GSM, CDMA, and UMTS cellular survey records
 
##### [0.0.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/release-0.0.1) - 2019-09-27
 * Initial release of message definitions


Contact
-----------------------------------
Christian Rowlands <craxiomdev@gmail.com> 