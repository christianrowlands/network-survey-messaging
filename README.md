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


Build and development instructions
-----------------------------------
###Building the project
 

###Building the project
 - Clone the repository
 - Execute `gradlew build` in the root directory to produce the Java library with the protobuf messages.
 - Execute `gradlew install` in the root directory to install the Java library to the local Maven cache.


Change log
-----------------------------------
0.0.1 : Initial release of message definitions


Contact
-----------------------------------
Christian Rowlands <craxiomdev@gmail.com> 