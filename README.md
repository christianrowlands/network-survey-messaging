Network Survey Messaging
===============

**Readme Contents**
- [What is it?](#what-is-it?)
- [Why?](#why?)
- [Some Details](#some-details)
- [Why are there protobuf files?](#why-are-there-protobuf-files?)
- [Build and development instructions](#build-and-development-instructions)
- [Change log](#change-log)
- [Contact](#contact)


## What is it?
The Network Survey Messaging API defines a set of messages that can be used for sending wireless network survey
messages between two applications.

The latest version of the API is published [here](https://messaging.networksurvey.app/).


## Why?
The original purpose for this API was to facilitate sending LTE survey messages from the [Network Survey Android App](https://github.com/christianrowlands/android-network-survey)
to a remote server via gRPC. However, it has been expanded not only to support additional protocols such as
GSM, CDMA, UMTS and 802.11, but has also been expanded to allow for its use outside the original Android application
by any system wanting to share wireless protocol data, or send wireless signal events.


## Some Details
The messages in this API are defined using [AsyncAPI](https://www.asyncapi.com/). All the messages are in JSON format.
In addition to the messages this API defines, it also defines the topics the messages can be sent on. For the most part,
the topic is close to the message name, but the idea is that these messages can be sent over transports such as MQTT
or Kafka. If they are, then the topic name in MQTT, Kafka, or any other system should follow the "channel" defined in
this API. For example, the [Network Survey Android App](https://github.com/christianrowlands/android-network-survey)
has a feature that sends out the GSM, CDMA, UMTS, LTE, 802.11, and Bluetooth JSON messages defined in this API. In doing so, it
publishes them to the MQTT topic that follows the channel name in this API. Therefore, anyone wanting to consume survey
records from that app simply needs to subscribe the the pre-defined topic/channel name and consume the JSON messages.


## Why are there protobuf files?
Protobuf was originally used at the IDL, and gRPC was used as the RPC framework / transport. However, to allow the
survey messages to flow over tools such as Kafka and be consumed more easily without requiring a protobuf library to
deserialize the binary messages, the actual messages were sent in JSON instead. That being said, it is really easy to
construct a protobuf object and use the Protobuf `JsonFormat.Printer` to convert a protobuf object to JSON. Therefore,
proto definitions matching the JSON messages are provided as a convenient way to generate the JSON message strings.

In other words, the protobuf files and gRPC SDKs are provided as convenience libraries only and are not an official part
of the Network Survey Messaging API. The API is instead only the JSON message schema and channel/topic names for the messages.

As a result, if a gRPC setup is of interest to a user of these messages, then the generated library can be employed to
send the protobuf messages over gRPC.  The build produces Java and Python libraries with all of the messages for use by the client
and server software respectively.

More information about protobuf is available [here](https://developers.google.com/protocol-buffers/).
More information about gRPC is available [here](https://grpc.io/).

Of note, the protobuf definitions were changed between version 0.1.3 and 0.2.0. The old definitions have been left for
backwards compatibility, but the new ones in the `com.craxiom.messaging` package should be leveraged instead.


## Sample usage
The gRPC website has tutorials for utilizing gRPC + protobuf for [Android](https://grpc.io/docs/quickstart/android/) and
[Java](https://grpc.io/docs/quickstart/java/).


## Getting Started
#### Add Network Survey Messaging to your project

The Network Survey Messaging protobuf library is available via [mavenCentral](https://search.maven.org/search?q=network-survey-messaging)

```groovy
dependencies {
    implementation 'com.craxiom:network-survey-messaging:1.1.0'
}
```

## Build and development instructions
#### Prerequisites

To build the AsyncAPI files you need to have `npm` installed. For macOS, it can be installed via brew
 - `brew install node`

Then install the html template generator
 - `npm install -g @asyncapi/generator`
 - `npm install -g @asyncapi/html-template`

Additional AsyncAPI generators can be found here:  https://github.com/asyncapi/generator#list-of-official-generator-templates

There is also an option to use a docker container for running the AsyncAPI commands instead of running locally. More information 
can be found [here](https://www.npmjs.com/package/@asyncapi/generator#cli-usage-with-docker).

An example command: `docker run --rm -it -v ${PWD}/src/main/asyncapi/network_survey_messaging.yaml:/app/network_survey_messaging.yaml -v ${PWD}/builddocs:/app/output asyncapi/generator:1.17.0 /app/network_survey_messaging.yaml @asyncapi/html-template@2.1.1 -o /app/output --force-write`

#### Building the HTML content from the AsyncAPI file
 - Execute `ag src/main/asyncapi/network_survey_messaging.yaml @asyncapi/html-template -o build/network-survey-messaging-html`
 - The HTML content will be located in the directory specified after the `-o` option.

To publish a new version of the Network Survey Messaging API HTML page, use the following command
 - `ag src/main/asyncapi/network_survey_messaging.yaml @asyncapi/html-template -o docs`

This will overwrite the current HTML content from the docs directory with the last API definition from the yaml file.

#### Building the protobuf Java and Python libraries
The Java library is built and published with Java 8 to support older Android versions, however it can be built with 
Java 11 if needed.

 - Execute `gradlew build` in the root directory to produce the Java library with the protobuf messages.
 - Execute `gradlew publishToMavenLocal` in the root directory to publish the artifacts to the local Maven cache.

Generated code is here:
 - Java: build/generated/source/proto/main/java/com/craxiom/messaging
 - Python: build/generated/source/proto/main/python/com/craxiom/messaging

## Change log

See the change log for details about each release: [`CHANGELOG.md`](CHANGELOG.md)

Contact
-----------------------------------
Christian Rowlands <craxiomdev@gmail.com>
