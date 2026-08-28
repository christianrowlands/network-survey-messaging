# Changelog

## [2.6.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.6.0) - 2026-08-28
* Adds the `gnssAge` and `networkAge` fields to the DeviceStatus message: the age in milliseconds of the GNSS provider fix (`gnssLatitude`/`gnssLongitude`) and of the network provider fix (`networkLatitude`/`networkLongitude`) respectively. These mirror the semantics of the existing `locationAge`, which describes only the primary location in `latitude`/`longitude` and therefore never measures the staleness of the two extra provider fixes. A value of 0, or an absent field, means the age is unknown. The ages exist so that a consumer can distinguish a receiver that has stopped producing fixes, where the age grows while the reported coordinates stay the same, from a receiver that keeps reporting an identical but freshly computed fix, where the age stays small. Without an age those two cases are identical on the wire, and a frozen position looks like a live one.
* Adds the `mockLocation` field to the DeviceStatus message: true when the primary location in `latitude`/`longitude` came from a mock location provider, from Android's `Location.isMock()` (API 31+) or the deprecated `Location.isFromMockProvider()` below that. The field is only ever present as true, and an absent field means the location did not come from a mock provider or the producer did not check. Producers must not send an explicit false: mocking is the exceptional case, and a negative assertion stamped on every ordinary record spends wire bytes on every message to say nothing. Consumers that need to distinguish "not mocked" from "never checked" should read the producer's `version` field, which names the API version the message was built against.

## [2.5.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.5.0) - 2026-07-27
* Adds the `connectionStatus` field to the LTE and NR record messages: a string enum (`UNKNOWN`, `PRIMARY_SERVING`, `SECONDARY_SERVING`, `NEIGHBOR`) sourced from Android's `CellInfo.getCellConnectionStatus()` (API 28+). This makes cells the device is actively connected to as a secondary distinguishable from neighbors, which the `servingCell` boolean cannot express: the 5G NSA (EN-DC) secondary NR cell has `servingCell=false` even though the device is actively using it. The enum value `NEIGHBOR` corresponds to Android's `CONNECTION_NONE` (a measured but unconnected cell). A `SECONDARY_SERVING` cell whose scan group's `PRIMARY_SERVING` cell is the same radio technology is a carrier aggregation SCell; where the group's primary is a different radio technology it is a dual connectivity secondary leg (e.g. a `SECONDARY_SERVING` NR record in a group whose primary is LTE is the 5G NSA data leg). Presence and trust rules: the protobuf enum's zero value never serializes, so an absent field and `UNKNOWN` are indistinguishable and both mean the device did not report a status; `servingCell` remains the primary serving-versus-neighbor discriminator, and `connectionStatus` is a refinement layered on top that is authoritative only for its positive `SECONDARY_SERVING` assertion. Note that shipping Android devices do not expose LTE carrier aggregation SCells as separate cells, so `SECONDARY_SERVING` is rare on an LTE record and must not be used as a carrier aggregation indicator; use `cellBandwidthsKhz` for that.
* Adds the `cellBandwidthsKhz` field to the LTE and NR record messages: the per-carrier channel bandwidths, in kHz, of the device's cellular data connection at the time of the scan, from Android's `ServiceState.getCellBandwidths()` (API 28+). Two or more entries means carrier aggregation was active and the sum is the aggregate downlink bandwidth. The field is device-level connection state stamped only on the records for cells the device was actively using (`servingCell=true` or `SECONDARY_SERVING`) and is omitted on neighbor records. The producer filters invalid platform sentinel values, so every entry is a valid bandwidth. An absent or empty array means the device did not report bandwidths and must not be read as "no carrier aggregation": some devices report nothing, or only the primary carrier, while aggregating.

## [2.4.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.4.0) - 2026-07-02
* Adds two new messages for the Wi-Fi Watchlist feature. `WatchlistMatch` (topic `watchlist_match_message`) is published when a device observes a watched SSID/BSSID; each message represents one cooldown-deduped presence appearance. `WatchlistEntryUpdate` (topic `watchlist_entry_message`) is published when a device connects to the broker and whenever the device's watchlist changes. Every entry update is a `SNAPSHOT` carrying the device's full authoritative list of entries; a cleared watchlist is a SNAPSHOT with an empty entries list (omitted from the JSON, so an absent entries field means empty). Each entry carries a device-minted `entryUuid`, and every `WatchlistEntryUpdate` carries a device-global monotonic `messageSequence` (a JSON string per the protobuf JSON mapping); a consumer keeps only the entries list from the highest `messageSequence` it has seen for a device. A device that has never had a watchlist entry publishes no entry update messages at all, so a device with no messages means an empty watchlist.

## [2.3.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.3.0) - 2026-03-26
* Adds the `plmn` string field (format "MCC-MNC", e.g., "310-01") to all cellular record messages (GSM, UMTS, LTE, NR) and CellIdentity messages in PhoneState. This field preserves MNC leading zeros that are lost when MNC is represented as an integer. Consumers should prefer the `plmn` field over the separate `mcc`/`mnc` integer fields.

## [2.2.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.2.0) - 2026-03-09
* **Breaking (protobuf binary only):** Reorder the Bluetooth `AddressType` enum so that `UNKNOWN = 0` is the default, matching the project convention. This ensures `PUBLIC` address types are serialized in JSON output instead of being omitted as the default value.

## [2.1.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.1.0) - 2026-03-02
* Adds the `missionId` and `recordNumber` fields to the DeviceStatus message for consistency with all other record types.

## [2.0.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v2.0.0) - 2025-10-20
* Removes the unused fields `heading`, `pitch`, `roll`, `fieldOfView`, and `receiverSensitivity`.
* Remove the deprecated gRPC connection handshake.

## [1.9.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.9.0) - 2025-08-30
* Adds the `mfgData` field to the Bluetooth message.

## [1.8.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.8.0) - 2025-04-10
* Adds the `addressType`, `deviceClass`, `serviceUuids`, and `companyId` fields to the Bluetooth message.

## [1.7.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.7.0) - 2025-02-12
* Adds the 'locationAge' field to all the messages. This field is used to indicate how old the location information is.

## [1.6.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.6.0) - 2024-10-17
* Adds the nonTerrestrialNetwork field to the PhoneState message (satellite usage).

## [1.5.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.5.0) - 2024-08-19
* Support for streaming Bluetooth and Phone State messages over gRPC.

## [1.4.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.4.0) - 2024-08-08
* Adds the slot field to the phone state record. See the docs for more information on the slot field.
* Upgrade to the latest protobuf (4.27.3) and gRPC (1.65.1) library versions.

## [1.3.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.3.0) - 2024-08-05
* Adds the appVersion field to the device status message.

## [1.2.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.2.0) - 2024-04-10
* Adds SNR for the reference signal to the LTE message.

## [1.1.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.1.0) - 2024-02-13
* Adds two more sets of optional location fields to the device status message, one for GNSS and one for Network based locations.

## [1.0.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v1.0.0) - 2024-01-26
* Adds 802.11be (Wi-Fi 7) to the list of 802.11 standards.
* Adds the bandwidth field to the Wi-Fi Beacon record.

## [0.15.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.15.0) - 2023-12-14
* Adds the slot field to the cellular records (GSM, CDMA, UMTS, LTE, and NR). See the docs for more information on the slot field.

## [0.14.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.14.0) - 2023-12-05
* Adds CQI to the LTE record.

## [0.13.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.13.1) - 2023-11-15
* Adds WPA2_WPA3 as an option for the Wi-Fi EncryptionType.

## [0.13.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.13.0) - 2023-11-13
* Adds the passpoint boolean field to the WifiBeaconRecord.

## [0.12.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.12.0) - 2023-09-18
* Adds frameType and frameSubtype to the wifi ota message.
* Upgrades the protobuf version to 3.24.3 and the gRPC version to 1.58.0.

## [0.11.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.11.0) - 2023-06-29
* Added the speed field to all the message schemas.

## [0.10.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.10.0) - 2023-01-19
* BREAKING CHANGE: Renamed the wifi_deauthentication_message topic to 80211_deauthentication_message for consistency with the other topics.
* BREAKING CHANGE: Renamed the wifi_ota_message topic to 80211_ota_message for consistency with the other topics.

## [0.9.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.9.0) - 2023-01-10
* Added a message schema for holding a Wi-Fi Probe Request record.
* Added a message schema for holding a Wi-Fi Deauthentication record.
* Added a various sensor metadata fields to the records.
* Added the mdmOverride field to the Device Status record.

## [0.8.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.8.0) - 2021-10-26
* Added the `ecno` field to the UMTS Record.

## [0.7.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.7.0) - 2021-09-09
* BREAKING CHANGE: Swapped the rawMessage for pcapRecord in the Cellular OTA messages.
* BREAKING CHANGE: Renamed the `lte_ota_message` topic to `cellular_ota_message` to allow it to be more generic.
* Added message schemas for GSM, UMTS NAS, and WCDMA RRC OTA messages (coming from Network Survey+).
* Added the accuracy field to all the messages which can be used to describe the accuracy of the provided location.

## [0.6.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.6.1) - 2021-07-06
* Added the missionID and recordNumber fields to the Phone State message schema.

## [0.6.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.6.0) - 2021-06-10
* Added a deviceModel field to the message schemas.
* Added a PhoneState message that is sent over the device_status_message topic that reports changes to the serving cell.

## [0.5.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.5.0) - 2021-01-22
* Added a message schema for holding cellular LTE Over The Air (OTA) NAS messages.

## [0.4.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.4.0) - 2021-01-19
* Added a message schema for 5G New Radio (NR) cellular survey records.
* Added a message schema for Bluetooth survey records.

## [0.3.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.3.0) - 2020-12-18
* Added a message schema for holding cellular LTE Over The Air (OTA) RRC messages.

## [0.2.2](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.2.2) - 2020-09-23
* Added a GNSS Survey message to the API.

## [0.2.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.2.1) - 2020-08-19
* BREAKING CHANGES were made to the JSON output of the protobuf messages.
* Modified timestamps to use (RFC3339)[https://tools.ietf.org/html/rfc3339#page-6] date-time format.

## [0.2.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.2.0) - 2020-08-11
* BREAKING CHANGES were made to the JSON output of the protobuf messages.
* Redesigned this messaging API to define the messages in JSON using AsyncAPI.
* Redefined the proto files to following the new JSON message schema.
* Deprecated the original proto definitions.
* Added a Signal Detection and an Energy Detection message to this API.

## [0.1.3](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.1.3) - 2020-06-26
* Added the device_name field to all the wireless proto messages.

## [0.1.2](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.1.2) - 2020-05-29
* Swapped out the 802.11 service set type from a String to an enum.

## [0.1.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.1.1) - 2020-05-23
* Added a protobuf message for 802.11 beacon frames.

## [0.1.0](https://github.com/christianrowlands/network-survey-messaging/releases/tag/v0.1.0) - 2020-04-23
* Switched to the full java version instead of the java lite protobuf implementation.

## [0.0.2](https://github.com/christianrowlands/network-survey-messaging/releases/tag/release-0.0.2) - 2020-01-06
* Added support for streaming GSM, CDMA, and UMTS cellular survey records.

## [0.0.1](https://github.com/christianrowlands/network-survey-messaging/releases/tag/release-0.0.1) - 2019-09-27
* Initial release of message definitions.
