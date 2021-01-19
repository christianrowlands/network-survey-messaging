package com.craxiom.messaging;

import com.craxiom.messaging.bluetooth.SupportedTechnologies;
import com.craxiom.messaging.bluetooth.Technology;
import com.craxiom.messaging.gnss.Constellation;
import com.craxiom.messaging.wifi.AkmSuite;
import com.craxiom.messaging.wifi.CipherSuite;
import com.craxiom.messaging.wifi.EncryptionType;
import com.craxiom.messaging.wifi.NodeType;
import com.craxiom.messaging.wifi.ServiceSetType;
import com.craxiom.messaging.wifi.Standard;
import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import com.google.protobuf.util.JsonFormat;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for converting the protobuf objects to JSON strings. The resulting JSON strings must match the schema outlined
 * in the Network Survey Messaging API. See the asyncapi yaml file and the HTML definition (https://messaging.networksurvey.app/)
 */
public class JsonConversionTest
{
    private static final double FLOAT_TOLERANCE = 0.0001;

    private final JsonFormat.Printer jsonFormatter = JsonFormat.printer().omittingInsignificantWhitespace();
    private final JsonFormat.Parser jsonParser = JsonFormat.parser();

    @Test
    public void testGsmToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"GsmRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Craxiom Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"mcc\":310,\"mnc\":410,\"lac\":174,\"ci\":47241,\"arfcn\":557,\"bsic\":25,\"signalStrength\":-73.0,\"ta\":4,\"servingCell\":false,\"provider\":\"ATT\"}}";

        final GsmRecord.Builder recordBuilder = GsmRecord.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("GsmRecord");

        final GsmRecordData.Builder dataBuilder = GsmRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("1234");
        dataBuilder.setDeviceName("Craxiom Pixel");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("Survey1 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(1);
        dataBuilder.setMcc(Int32Value.newBuilder().setValue(310).build());
        dataBuilder.setMnc(Int32Value.newBuilder().setValue(410).build());
        dataBuilder.setLac(Int32Value.newBuilder().setValue(174).build());
        dataBuilder.setCi(Int32Value.newBuilder().setValue(47241).build());
        dataBuilder.setArfcn(Int32Value.newBuilder().setValue(557).build());
        dataBuilder.setBsic(Int32Value.newBuilder().setValue(25).build());
        dataBuilder.setSignalStrength(FloatValue.newBuilder().setValue(-73).build());
        dataBuilder.setTa(Int32Value.newBuilder().setValue(4).build());
        dataBuilder.setServingCell(BoolValue.newBuilder().setValue(false).build());
        dataBuilder.setProvider("ATT");

        recordBuilder.setData(dataBuilder);

        final GsmRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testGsmFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"GsmRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Craxiom Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey2 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"mcc\":310,\"mnc\":410,\"lac\":174,\"ci\":47241,\"arfcn\":557,\"bsic\":25,\"signalStrength\":-73.0,\"ta\":4,\"servingCell\":false,\"provider\":\"ATT\"}}";

        final GsmRecord.Builder builder = GsmRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final GsmRecord convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("GsmRecord", convertedRecord.getMessageType());

        final GsmRecordData data = convertedRecord.getData();
        assertEquals("1234", data.getDeviceSerialNumber());
        assertEquals("Craxiom Pixel", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("Survey2 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals(1, data.getGroupNumber());
        assertEquals(310, data.getMcc().getValue());
        assertEquals(410, data.getMnc().getValue());
        assertEquals(174, data.getLac().getValue());
        assertEquals(47241, data.getCi().getValue());
        assertEquals(557, data.getArfcn().getValue());
        assertEquals(25, data.getBsic().getValue());
        assertEquals(-73, data.getSignalStrength().getValue());
        assertEquals(4, data.getTa().getValue());
        assertFalse(data.getServingCell().getValue());
        assertEquals("ATT", data.getProvider());
    }

    @Test
    public void testCdmaToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"CdmaRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"My Device\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"How much wood can a woodchuck chuck\",\"recordNumber\":1,\"groupNumber\":1,\"sid\":139,\"nid\":4,\"zone\":232,\"bsid\":12731,\"channel\":384,\"pnOffset\":136,\"signalStrength\":-73.0,\"ecio\":-11.4,\"servingCell\":false,\"provider\":\"Verizon\"}}";

        final CdmaRecord.Builder recordBuilder = CdmaRecord.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("CdmaRecord");

        final CdmaRecordData.Builder dataBuilder = CdmaRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("1234");
        dataBuilder.setDeviceName("My Device");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("How much wood can a woodchuck chuck");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(1);
        dataBuilder.setSid(Int32Value.newBuilder().setValue(139).build());
        dataBuilder.setNid(Int32Value.newBuilder().setValue(4).build());
        dataBuilder.setZone(Int32Value.newBuilder().setValue(232).build());
        dataBuilder.setBsid(Int32Value.newBuilder().setValue(12731).build());
        dataBuilder.setChannel(Int32Value.newBuilder().setValue(384).build());
        dataBuilder.setPnOffset(Int32Value.newBuilder().setValue(136).build());
        dataBuilder.setSignalStrength(FloatValue.newBuilder().setValue(-73).build());
        dataBuilder.setEcio(FloatValue.newBuilder().setValue(-11.4f).build());
        dataBuilder.setServingCell(BoolValue.newBuilder().setValue(false).build());
        dataBuilder.setProvider("Verizon");

        recordBuilder.setData(dataBuilder);

        final CdmaRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testCdmaFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"CdmaRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"My Device\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"How much wood can a woodchuck chuck\",\"recordNumber\":509,\"groupNumber\":155,\"sid\":139,\"nid\":4,\"zone\":232,\"bsid\":12731,\"channel\":384,\"pnOffset\":136,\"signalStrength\":-73.0,\"ecio\":-11.4,\"servingCell\":false,\"provider\":\"Verizon\"}}";

        final CdmaRecord.Builder builder = CdmaRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final CdmaRecord convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("CdmaRecord", convertedRecord.getMessageType());

        final CdmaRecordData data = convertedRecord.getData();
        assertEquals("1234", data.getDeviceSerialNumber());
        assertEquals("My Device", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("How much wood can a woodchuck chuck", data.getMissionId());
        assertEquals(509, data.getRecordNumber());
        assertEquals(155, data.getGroupNumber());
        assertEquals(139, data.getSid().getValue());
        assertEquals(4, data.getNid().getValue());
        assertEquals(232, data.getZone().getValue());
        assertEquals(12731, data.getBsid().getValue());
        assertEquals(384, data.getChannel().getValue());
        assertEquals(136, data.getPnOffset().getValue());
        assertEquals(-73, data.getSignalStrength().getValue());
        assertEquals(-11.4, data.getEcio().getValue(), FLOAT_TOLERANCE);
        assertFalse(data.getServingCell().getValue());
        assertEquals("Verizon", data.getProvider());
    }

    @Test
    public void testUmtsToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"UmtsRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Big Phone\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"COW13 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"mcc\":310,\"mnc\":260,\"lac\":65535,\"cid\":61381,\"uarfcn\":9800,\"psc\":141,\"rscp\":-73.0,\"signalStrength\":-73.0,\"servingCell\":true,\"provider\":\"T-Mobile\"}}";

        final UmtsRecord.Builder recordBuilder = UmtsRecord.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("UmtsRecord");

        final UmtsRecordData.Builder dataBuilder = UmtsRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("1234");
        dataBuilder.setDeviceName("Big Phone");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("COW13 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(1);
        dataBuilder.setMcc(Int32Value.newBuilder().setValue(310).build());
        dataBuilder.setMnc(Int32Value.newBuilder().setValue(260).build());
        dataBuilder.setLac(Int32Value.newBuilder().setValue(65535).build());
        dataBuilder.setCid(Int32Value.newBuilder().setValue(61381).build());
        dataBuilder.setUarfcn(Int32Value.newBuilder().setValue(9800).build());
        dataBuilder.setPsc(Int32Value.newBuilder().setValue(141).build());
        dataBuilder.setRscp(FloatValue.newBuilder().setValue(-73).build());
        dataBuilder.setSignalStrength(FloatValue.newBuilder().setValue(-73).build());
        dataBuilder.setServingCell(BoolValue.newBuilder().setValue(true).build());
        dataBuilder.setProvider("T-Mobile");

        recordBuilder.setData(dataBuilder);

        final UmtsRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testUmtsFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"UmtsRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Big Phone\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"COW13 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"mcc\":310,\"mnc\":260,\"lac\":65535,\"cid\":61381,\"uarfcn\":9800,\"psc\":141,\"rscp\":-73.0,\"signalStrength\":-73.0,\"servingCell\":true,\"provider\":\"T-Mobile\"}}";

        final UmtsRecord.Builder builder = UmtsRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final UmtsRecord convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("UmtsRecord", convertedRecord.getMessageType());

        final UmtsRecordData data = convertedRecord.getData();
        assertEquals("1234", data.getDeviceSerialNumber());
        assertEquals("Big Phone", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("COW13 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals(1, data.getGroupNumber());
        assertEquals(310, data.getMcc().getValue());
        assertEquals(260, data.getMnc().getValue());
        assertEquals(65535, data.getLac().getValue());
        assertEquals(61381, data.getCid().getValue());
        assertEquals(9800, data.getUarfcn().getValue());
        assertEquals(141, data.getPsc().getValue());
        assertEquals(-73, data.getRscp().getValue());
        assertEquals(-73, data.getSignalStrength().getValue());
        assertTrue(data.getServingCell().getValue());
        assertEquals("T-Mobile", data.getProvider());
    }

    @Test
    public void testLteToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"LteRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Craxiom Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"mcc\":311,\"mnc\":480,\"tac\":52803,\"eci\":52824577,\"earfcn\":5230,\"pci\":234,\"rsrp\":-107.0,\"rsrq\":-11.0,\"ta\":27,\"servingCell\":true,\"lteBandwidth\":\"MHZ_10\",\"provider\":\"Verizon\",\"signalStrength\":-88.5}}";

        final LteRecord.Builder recordBuilder = LteRecord.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("LteRecord");

        final LteRecordData.Builder dataBuilder = LteRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("1234");
        dataBuilder.setDeviceName("Craxiom Pixel");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("Survey1 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(1);
        dataBuilder.setMcc(Int32Value.newBuilder().setValue(311).build());
        dataBuilder.setMnc(Int32Value.newBuilder().setValue(480).build());
        dataBuilder.setTac(Int32Value.newBuilder().setValue(52803).build());
        dataBuilder.setEci(Int32Value.newBuilder().setValue(52824577).build());
        dataBuilder.setEarfcn(Int32Value.newBuilder().setValue(5230).build());
        dataBuilder.setPci(Int32Value.newBuilder().setValue(234).build());
        dataBuilder.setRsrp(FloatValue.newBuilder().setValue(-107).build());
        dataBuilder.setRsrq(FloatValue.newBuilder().setValue(-11).build());
        dataBuilder.setTa(Int32Value.newBuilder().setValue(27).build());
        dataBuilder.setServingCell(BoolValue.newBuilder().setValue(true).build());
        dataBuilder.setLteBandwidth(LteBandwidth.MHZ_10);
        dataBuilder.setProvider("Verizon");
        dataBuilder.setSignalStrength(FloatValue.newBuilder().setValue(-88.5f).build());

        recordBuilder.setData(dataBuilder);

        final LteRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testLteFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"LteRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Craxiom Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"mcc\":311,\"mnc\":480,\"tac\":52803,\"eci\":52824577,\"earfcn\":5230,\"pci\":234,\"rsrp\":-107.0,\"rsrq\":-11.0,\"ta\":27,\"servingCell\":true,\"lteBandwidth\":\"MHZ_10\",\"provider\":\"Verizon\"}}";

        final LteRecord.Builder builder = LteRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final LteRecord convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("LteRecord", convertedRecord.getMessageType());

        final LteRecordData data = convertedRecord.getData();
        assertEquals("1234", data.getDeviceSerialNumber());
        assertEquals("Craxiom Pixel", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("Survey1 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals(1, data.getGroupNumber());
        assertEquals(311, data.getMcc().getValue());
        assertEquals(480, data.getMnc().getValue());
        assertEquals(52803, data.getTac().getValue());
        assertEquals(52824577, data.getEci().getValue());
        assertEquals(5230, data.getEarfcn().getValue());
        assertEquals(234, data.getPci().getValue());
        assertEquals(-107, data.getRsrp().getValue());
        assertEquals(-11, data.getRsrq().getValue());
        assertEquals(27, data.getTa().getValue());
        assertTrue(data.getServingCell().getValue());
        assertEquals(LteBandwidth.MHZ_10, data.getLteBandwidth());
        assertEquals("Verizon", data.getProvider());
    }

    @Test
    public void testWifiBeaconToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"WifiBeaconRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"WiFi Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"sourceAddress\":\"68:7F:74:B0:14:98\",\"destinationAddress\":\"68:7F:74:B0:14:22\",\"bssid\":\"68:7F:74:B0:14:98\",\"beaconInterval\":100,\"serviceSetType\":\"BSS\",\"ssid\":\"My Wi-Fi Network\",\"supportedRates\":\"1; 2; 5.5; 11; 18; 24; 36; 54\",\"extendedSupportedRates\":\"6; 9; 12; 48\",\"cipherSuites\":[\"TKIP\",\"CCMP\"],\"akmSuites\":[\"OPEN\"],\"encryptionType\":\"WPA_WPA2\",\"wps\":true,\"channel\":2,\"frequencyMhz\":2417,\"signalStrength\":-58.5,\"snr\":26.7,\"nodeType\":\"AP\",\"standard\":\"IEEE80211N\"}}";

        final WifiBeaconRecord.Builder recordBuilder = WifiBeaconRecord.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("WifiBeaconRecord");

        final WifiBeaconRecordData.Builder dataBuilder = WifiBeaconRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("1234");
        dataBuilder.setDeviceName("WiFi Pixel");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("Survey1 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setSourceAddress("68:7F:74:B0:14:98");
        dataBuilder.setDestinationAddress("68:7F:74:B0:14:22");
        dataBuilder.setBssid("68:7F:74:B0:14:98");
        dataBuilder.setBeaconInterval(Int32Value.newBuilder().setValue(100).build());
        dataBuilder.setServiceSetType(ServiceSetType.BSS);
        dataBuilder.setSsid("My Wi-Fi Network");
        dataBuilder.setSupportedRates("1; 2; 5.5; 11; 18; 24; 36; 54");
        dataBuilder.setExtendedSupportedRates("6; 9; 12; 48");
        dataBuilder.addAllCipherSuites(Arrays.asList(CipherSuite.TKIP, CipherSuite.CCMP));
        dataBuilder.addAllAkmSuites(Collections.singletonList(AkmSuite.OPEN));
        dataBuilder.setEncryptionType(EncryptionType.WPA_WPA2);
        dataBuilder.setWps(BoolValue.newBuilder().setValue(true).build());
        dataBuilder.setChannel(Int32Value.newBuilder().setValue(2).build());
        dataBuilder.setFrequencyMhz(Int32Value.newBuilder().setValue(2417).build());
        dataBuilder.setSignalStrength(FloatValue.newBuilder().setValue(-58.5f).build());
        dataBuilder.setSnr(FloatValue.newBuilder().setValue(26.7f).build());
        dataBuilder.setNodeType(NodeType.AP);
        dataBuilder.setStandard(Standard.IEEE80211N);

        recordBuilder.setData(dataBuilder);

        final WifiBeaconRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testWifiBeaconFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"WifiBeaconRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"WiFi Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"sourceAddress\":\"68:7F:74:B0:14:98\",\"destinationAddress\":\"68:7F:74:B0:14:22\",\"bssid\":\"68:7F:74:B0:14:98\",\"beaconInterval\":100,\"serviceSetType\":\"BSS\",\"ssid\":\"My Wi-Fi Network\",\"supportedRates\":\"1; 2; 5.5; 11; 18; 24; 36; 54\",\"extendedSupportedRates\":\"6; 9; 12; 48\",\"cipherSuites\":[\"TKIP\",\"CCMP\"],\"akmSuites\":[\"OPEN\"],\"encryptionType\":\"WPA_WPA2\",\"wps\":true,\"channel\":2,\"frequencyMhz\":2417,\"signalStrength\":-58.5,\"snr\":26.7,\"nodeType\":\"AP\",\"standard\":\"IEEE80211N\"}}";

        final WifiBeaconRecord.Builder builder = WifiBeaconRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final WifiBeaconRecord convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("WifiBeaconRecord", convertedRecord.getMessageType());

        final WifiBeaconRecordData data = convertedRecord.getData();
        assertEquals("1234", data.getDeviceSerialNumber());
        assertEquals("WiFi Pixel", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("Survey1 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals("68:7F:74:B0:14:98", data.getSourceAddress());
        assertEquals("68:7F:74:B0:14:22", data.getDestinationAddress());
        assertEquals("68:7F:74:B0:14:98", data.getBssid());
        assertEquals(100, data.getBeaconInterval().getValue());
        assertEquals(ServiceSetType.BSS, data.getServiceSetType());
        assertEquals("My Wi-Fi Network", data.getSsid());
        assertEquals("1; 2; 5.5; 11; 18; 24; 36; 54", data.getSupportedRates());
        assertEquals("6; 9; 12; 48", data.getExtendedSupportedRates());
        assertEquals(Arrays.asList(CipherSuite.TKIP, CipherSuite.CCMP), data.getCipherSuitesList());
        assertEquals(Collections.singletonList(AkmSuite.OPEN), data.getAkmSuitesList());
        assertEquals(EncryptionType.WPA_WPA2, data.getEncryptionType());
        assertTrue(data.getWps().getValue());
        assertEquals(2, data.getChannel().getValue());
        assertEquals(2417, data.getFrequencyMhz().getValue());
        assertEquals(-58.5, data.getSignalStrength().getValue());
        assertEquals(26.7, data.getSnr().getValue(), FLOAT_TOLERANCE);
        assertEquals(NodeType.AP, data.getNodeType());
        assertEquals(Standard.IEEE80211N, data.getStandard());
    }

    @Test
    public void testBluetoothToJson()
    {
        final String expectedJson = "{\"version\":\"0.4.0\",\"messageType\":\"BluetoothRecord\",\"data\":{\"deviceSerialNumber\":\"ee4d453e4c6f73fa\",\"deviceName\":\"BT Pixel\",\"deviceTime\":\"2021-01-14T12:47:04.76-05:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":184.08124,\"missionId\":\"NS ee4d453e4c6f73fa 20210114-124535\",\"recordNumber\":1,\"sourceAddress\":\"E1:A1:19:A9:68:B0\",\"destinationAddress\":\"56:14:62:0D:98:01\",\"signalStrength\":-78.0,\"txPower\":8.0,\"technology\":\"LE\",\"supportedTechnologies\":\"DUAL\",\"otaDeviceName\":\"846B2162E22433AFE9\",\"channel\":6}}";

        final BluetoothRecord.Builder recordBuilder = BluetoothRecord.newBuilder();
        recordBuilder.setVersion("0.4.0");
        recordBuilder.setMessageType("BluetoothRecord");

        final BluetoothRecordData.Builder dataBuilder = BluetoothRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("ee4d453e4c6f73fa");
        dataBuilder.setDeviceName("BT Pixel");
        dataBuilder.setDeviceTime("2021-01-14T12:47:04.76-05:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(184.08124f);
        dataBuilder.setMissionId("NS ee4d453e4c6f73fa 20210114-124535");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setSourceAddress("E1:A1:19:A9:68:B0");
        dataBuilder.setDestinationAddress("56:14:62:0D:98:01");
        dataBuilder.setSignalStrength(FloatValue.newBuilder().setValue(-78f).build());
        dataBuilder.setTxPower(FloatValue.newBuilder().setValue(8f).build());
        dataBuilder.setTechnology(Technology.LE);
        dataBuilder.setSupportedTechnologies(SupportedTechnologies.DUAL);
        dataBuilder.setOtaDeviceName("846B2162E22433AFE9");
        dataBuilder.setChannel(Int32Value.newBuilder().setValue(6).build());

        recordBuilder.setData(dataBuilder);

        final BluetoothRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testBluetoothFromJson()
    {
        final String inputJson = "{\"version\":\"0.4.0\",\"messageType\":\"BluetoothRecord\",\"data\":{\"deviceSerialNumber\":\"ee4d453e4c6f73fa\",\"deviceName\":\"BT Pixel\",\"deviceTime\":\"2021-01-14T12:47:04.76-05:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":184.08124,\"missionId\":\"NS ee4d453e4c6f73fa 20210114-124535\",\"recordNumber\":1,\"sourceAddress\":\"E1:A1:19:A9:68:B0\",\"destinationAddress\":\"56:14:62:0D:98:01\",\"signalStrength\":-78.0,\"txPower\":8.0,\"technology\":\"LE\",\"supportedTechnologies\":\"DUAL\",\"otaDeviceName\":\"846B2162E22433AFE9\",\"channel\":6}}";

        final BluetoothRecord.Builder builder = BluetoothRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final BluetoothRecord convertedRecord = builder.build();

        assertEquals("0.4.0", convertedRecord.getVersion());
        assertEquals("BluetoothRecord", convertedRecord.getMessageType());

        final BluetoothRecordData data = convertedRecord.getData();
        assertEquals("ee4d453e4c6f73fa", data.getDeviceSerialNumber());
        assertEquals("BT Pixel", data.getDeviceName());
        assertEquals("2021-01-14T12:47:04.76-05:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(184.08124f, data.getAltitude());
        assertEquals("NS ee4d453e4c6f73fa 20210114-124535", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals("E1:A1:19:A9:68:B0", data.getSourceAddress());
        assertEquals("56:14:62:0D:98:01", data.getDestinationAddress());
        assertEquals(-78.0, data.getSignalStrength().getValue());
        assertEquals(8.0, data.getTxPower().getValue());
        assertEquals(Technology.LE, data.getTechnology());
        assertEquals(SupportedTechnologies.DUAL, data.getSupportedTechnologies());
        assertEquals("846B2162E22433AFE9", data.getOtaDeviceName());
        assertEquals(6, data.getChannel().getValue());
    }

    @Test
    public void testGnssToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.2\",\"messageType\":\"GnssRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Gnss Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":23,\"constellation\":\"GLONASS\",\"spaceVehicleId\":4567,\"carrierFreqHz\":\"120000000\",\"clockOffset\":0.01,\"usedInSolution\":false,\"undulationM\":2.1,\"latitudeStdDevM\":3.1,\"longitudeStdDevM\":2.2,\"altitudeStdDevM\":1.3,\"agcDb\":0.4,\"cn0DbHz\":0.05,\"hdop\":1.1,\"vdop\":2.2}}";

        final GnssRecord.Builder recordBuilder = GnssRecord.newBuilder();
        recordBuilder.setVersion("0.2.2");
        recordBuilder.setMessageType("GnssRecord");

        final GnssRecordData.Builder dataBuilder = GnssRecordData.newBuilder();
        dataBuilder.setDeviceSerialNumber("1234");
        dataBuilder.setDeviceName("Gnss Pixel");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("Survey1 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(23);
        dataBuilder.setConstellation(Constellation.GLONASS);
        dataBuilder.setSpaceVehicleId(UInt32Value.newBuilder().setValue(4567).build());
        dataBuilder.setCarrierFreqHz(UInt64Value.newBuilder().setValue(120_000_000).build());
        dataBuilder.setClockOffset(DoubleValue.newBuilder().setValue(0.01).build());
        dataBuilder.setUsedInSolution(BoolValue.newBuilder().setValue(false).build());
        dataBuilder.setUndulationM(FloatValue.newBuilder().setValue(2.1f).build());
        dataBuilder.setLatitudeStdDevM(FloatValue.newBuilder().setValue(3.1f).build());
        dataBuilder.setLongitudeStdDevM(FloatValue.newBuilder().setValue(2.2f).build());
        dataBuilder.setAltitudeStdDevM(FloatValue.newBuilder().setValue(1.3f).build());
        dataBuilder.setAgcDb(FloatValue.newBuilder().setValue(0.4f).build());
        dataBuilder.setCn0DbHz(FloatValue.newBuilder().setValue(0.05f).build());
        dataBuilder.setHdop(FloatValue.newBuilder().setValue(1.1f).build());
        dataBuilder.setVdop(FloatValue.newBuilder().setValue(2.2f).build());

        recordBuilder.setData(dataBuilder);

        final GnssRecord record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testGnssFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.2\",\"messageType\":\"GnssRecord\",\"data\":{\"deviceSerialNumber\":\"1234\",\"deviceName\":\"Gnss Pixel\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":23,\"constellation\":\"GLONASS\",\"spaceVehicleId\":4567,\"carrierFreqHz\":\"120000000\",\"clockOffset\":0.01,\"usedInSolution\":false,\"undulationM\":2.1,\"latitudeStdDevM\":3.1,\"longitudeStdDevM\":2.2,\"altitudeStdDevM\":1.3,\"agcDb\":0.4,\"cn0DbHz\":0.05,\"hdop\":1.1,\"vdop\":2.2}}";

        final GnssRecord.Builder builder = GnssRecord.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final GnssRecord convertedRecord = builder.build();

        assertEquals("0.2.2", convertedRecord.getVersion());
        assertEquals("GnssRecord", convertedRecord.getMessageType());

        final GnssRecordData data = convertedRecord.getData();
        assertEquals("1234", data.getDeviceSerialNumber());
        assertEquals("Gnss Pixel", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("Survey1 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals(23, data.getGroupNumber());
        assertEquals(Constellation.GLONASS, data.getConstellation());
        assertEquals(4567, data.getSpaceVehicleId().getValue());
        assertEquals(120_000_000, data.getCarrierFreqHz().getValue());
        assertEquals(0.01, data.getClockOffset().getValue());
        assertFalse(data.getUsedInSolution().getValue());
        assertEquals(2.1, data.getUndulationM().getValue(), FLOAT_TOLERANCE);
        assertEquals(3.1, data.getLatitudeStdDevM().getValue(), FLOAT_TOLERANCE);
        assertEquals(2.2, data.getLongitudeStdDevM().getValue(), FLOAT_TOLERANCE);
        assertEquals(1.3, data.getAltitudeStdDevM().getValue(), FLOAT_TOLERANCE);
        assertEquals(0.4, data.getAgcDb().getValue(), FLOAT_TOLERANCE);
        assertEquals(0.05, data.getCn0DbHz().getValue(), FLOAT_TOLERANCE);
        assertEquals(1.1, data.getHdop().getValue(), FLOAT_TOLERANCE);
        assertEquals(2.2, data.getVdop().getValue(), FLOAT_TOLERANCE);
    }

    @Test
    public void testEnergyDetectionToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"EnergyDetection\",\"data\":{\"deviceSerialNumber\":\"xyz\",\"deviceName\":\"My SDR\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"frequencyHz\":\"162000000\",\"bandwidthHz\":12500,\"signalStrength\":-73.0,\"snr\":19.2,\"timeUp\":\"1996-12-19T16:39:57-08:00\",\"durationSec\":4.2}}";

        final EnergyDetection.Builder recordBuilder = EnergyDetection.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("EnergyDetection");

        final EnergyDetectionData.Builder dataBuilder = EnergyDetectionData.newBuilder();
        dataBuilder.setDeviceSerialNumber("xyz");
        dataBuilder.setDeviceName("My SDR");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("Survey1 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(1);
        dataBuilder.setFrequencyHz(162000000);
        dataBuilder.setBandwidthHz(Int32Value.newBuilder().setValue(12500).build());
        dataBuilder.setSignalStrength(-73);
        dataBuilder.setSnr(FloatValue.newBuilder().setValue(19.2f).build());
        dataBuilder.setTimeUp("1996-12-19T16:39:57-08:00");
        dataBuilder.setDurationSec(FloatValue.newBuilder().setValue(4.2f).build());

        recordBuilder.setData(dataBuilder);

        final EnergyDetection record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testEnergyDetectionFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"EnergyDetection\",\"data\":{\"deviceSerialNumber\":\"xyz\",\"deviceName\":\"My SDR\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"frequencyHz\":\"162000000\",\"bandwidthHz\":12500,\"signalStrength\":-73.0,\"snr\":19.2,\"timeUp\":\"1996-12-19T16:39:57-08:00\",\"durationSec\":4.2}}";

        final EnergyDetection.Builder builder = EnergyDetection.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final EnergyDetection convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("EnergyDetection", convertedRecord.getMessageType());

        final EnergyDetectionData data = convertedRecord.getData();
        assertEquals("xyz", data.getDeviceSerialNumber());
        assertEquals("My SDR", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("Survey1 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals(1, data.getGroupNumber());
        assertEquals(162000000, data.getFrequencyHz());
        assertEquals(12500, data.getBandwidthHz().getValue());
        assertEquals(-73, data.getSignalStrength());
        assertEquals(19.2, data.getSnr().getValue(), FLOAT_TOLERANCE);
        assertEquals("1996-12-19T16:39:57-08:00", data.getTimeUp());
        assertEquals(4.2, data.getDurationSec().getValue(), FLOAT_TOLERANCE);
    }

    @Test
    public void testSignalDetectionToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"SignalDetection\",\"data\":{\"deviceSerialNumber\":\"xyz\",\"deviceName\":\"My SDR\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"frequencyHz\":\"162000000\",\"bandwidthHz\":12500,\"signalStrength\":-73.0,\"snr\":19.2,\"timeUp\":\"1996-12-19T16:39:57-08:00\",\"durationSec\":4.2,\"modulation\":\"4FSK\",\"signalName\":\"DMR\"}}";

        final SignalDetection.Builder recordBuilder = SignalDetection.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("SignalDetection");

        final SignalDetectionData.Builder dataBuilder = SignalDetectionData.newBuilder();
        dataBuilder.setDeviceSerialNumber("xyz");
        dataBuilder.setDeviceName("My SDR");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setMissionId("Survey1 20200724-154325");
        dataBuilder.setRecordNumber(1);
        dataBuilder.setGroupNumber(1);
        dataBuilder.setFrequencyHz(162000000);
        dataBuilder.setBandwidthHz(Int32Value.newBuilder().setValue(12500).build());
        dataBuilder.setSignalStrength(-73);
        dataBuilder.setSnr(FloatValue.newBuilder().setValue(19.2f).build());
        dataBuilder.setTimeUp("1996-12-19T16:39:57-08:00");
        dataBuilder.setDurationSec(FloatValue.newBuilder().setValue(4.2f).build());
        dataBuilder.setModulation("4FSK");
        dataBuilder.setSignalName("DMR");

        recordBuilder.setData(dataBuilder);

        final SignalDetection record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testSignalDetectionFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"SignalDetection\",\"data\":{\"deviceSerialNumber\":\"xyz\",\"deviceName\":\"My SDR\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"missionId\":\"Survey1 20200724-154325\",\"recordNumber\":1,\"groupNumber\":1,\"frequencyHz\":\"162000000\",\"bandwidthHz\":12500,\"signalStrength\":-73.0,\"snr\":19.2,\"timeUp\":\"1996-12-19T16:39:57-08:00\",\"durationSec\":4.2,\"modulation\":\"4FSK\",\"signalName\":\"DMR\"}}";

        final SignalDetection.Builder builder = SignalDetection.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final SignalDetection convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("SignalDetection", convertedRecord.getMessageType());

        final SignalDetectionData data = convertedRecord.getData();
        assertEquals("xyz", data.getDeviceSerialNumber());
        assertEquals("My SDR", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals("Survey1 20200724-154325", data.getMissionId());
        assertEquals(1, data.getRecordNumber());
        assertEquals(1, data.getGroupNumber());
        assertEquals(162000000, data.getFrequencyHz());
        assertEquals(12500, data.getBandwidthHz().getValue());
        assertEquals(-73, data.getSignalStrength());
        assertEquals(19.2, data.getSnr().getValue(), FLOAT_TOLERANCE);
        assertEquals("1996-12-19T16:39:57-08:00", data.getTimeUp());
        assertEquals(4.2, data.getDurationSec().getValue(), FLOAT_TOLERANCE);
        assertEquals("4FSK", data.getModulation());
        assertEquals("DMR", data.getSignalName());
    }

    @Test
    public void testDeviceStatusToJson()
    {
        final String expectedJson = "{\"version\":\"0.2.0\",\"messageType\":\"DeviceStatus\",\"data\":{\"deviceSerialNumber\":\"IMEI: 1\",\"deviceName\":\"My Phone\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"batteryLevelPercent\":38,\"error\":{\"errorMessage\":\"The scan stopped unexpectedly\"}}}";

        final DeviceStatus.Builder recordBuilder = DeviceStatus.newBuilder();
        recordBuilder.setVersion("0.2.0");
        recordBuilder.setMessageType("DeviceStatus");

        final DeviceStatusData.Builder dataBuilder = DeviceStatusData.newBuilder();
        dataBuilder.setDeviceSerialNumber("IMEI: 1");
        dataBuilder.setDeviceName("My Phone");
        dataBuilder.setDeviceTime("1996-12-19T16:39:57-08:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setBatteryLevelPercent(Int32Value.newBuilder().setValue(38).build());
        dataBuilder.setError(Error.newBuilder().setErrorMessage("The scan stopped unexpectedly").build());

        recordBuilder.setData(dataBuilder);

        final DeviceStatus record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testDeviceStatusFromJson()
    {
        final String inputJson = "{\"version\":\"0.2.0\",\"messageType\":\"DeviceStatus\",\"data\":{\"deviceSerialNumber\":\"IMEI: 1\",\"deviceName\":\"My Phone\",\"deviceTime\":\"1996-12-19T16:39:57-08:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"batteryLevelPercent\":38,\"error\":{\"errorMessage\":\"The scan stopped unexpectedly\"}}}";

        final DeviceStatus.Builder builder = DeviceStatus.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final DeviceStatus convertedRecord = builder.build();

        assertEquals("0.2.0", convertedRecord.getVersion());
        assertEquals("DeviceStatus", convertedRecord.getMessageType());

        final DeviceStatusData data = convertedRecord.getData();
        assertEquals("IMEI: 1", data.getDeviceSerialNumber());
        assertEquals("My Phone", data.getDeviceName());
        assertEquals("1996-12-19T16:39:57-08:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals(38, data.getBatteryLevelPercent().getValue());
        assertEquals("The scan stopped unexpectedly", data.getError().getErrorMessage());
    }

    @Test
    public void testLteRrcToJson()
    {
        final String expectedJson = "{\"version\":\"0.3.0\",\"messageType\":\"LteRrc\",\"data\":{\"deviceSerialNumber\":\"Device 5\",\"deviceName\":\"My Phone\",\"deviceTime\":\"2020-12-17T16:21:42.982-05:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"channelType\":\"BCCH_BCH\",\"rawMessage\":\"FA4wAO0BawMAAFk5BQAAAAAJAEABfGtfkSAAAA==\"}}";

        final LteRrc.Builder recordBuilder = LteRrc.newBuilder();
        recordBuilder.setVersion("0.3.0");
        recordBuilder.setMessageType("LteRrc");

        final LteRrcData.Builder dataBuilder = LteRrcData.newBuilder();
        dataBuilder.setDeviceSerialNumber("Device 5");
        dataBuilder.setDeviceName("My Phone");
        dataBuilder.setDeviceTime("2020-12-17T16:21:42.982-05:00");
        dataBuilder.setLatitude(51.470334);
        dataBuilder.setLongitude(-0.486594);
        dataBuilder.setAltitude(13.3f);
        dataBuilder.setChannelType(LteRrcChannelType.BCCH_BCH);
        dataBuilder.setRawMessage(ByteString.copyFrom(new byte[]{(byte) 0x14, (byte) 0x0e, (byte) 0x30, (byte) 0x00, (byte) 0xed, (byte) 0x01, (byte) 0x6b, (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x59, (byte) 0x39, (byte) 0x05, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x00, (byte) 0x40, (byte) 0x01, (byte) 0x7c, (byte) 0x6b, (byte) 0x5f, (byte) 0x91, (byte) 0x20, (byte) 0x00, (byte) 0x00}));

        recordBuilder.setData(dataBuilder);

        final LteRrc record = recordBuilder.build();

        try
        {
            final String recordJson = jsonFormatter.print(record);
            assertEquals(expectedJson, recordJson);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a protobuf object to a JSON string.", e);
        }
    }

    @Test
    public void testLteRrcFromJson()
    {
        final String inputJson = "{\"version\":\"0.3.0\",\"messageType\":\"LteRrc\",\"data\":{\"deviceSerialNumber\":\"Device 1\",\"deviceName\":\"My Phone\",\"deviceTime\":\"2020-12-17T16:21:42.982-05:00\",\"latitude\":51.470334,\"longitude\":-0.486594,\"altitude\":13.3,\"channelType\":\"BCCH_BCH\",\"rawMessage\":\"FA4wAO0BawMAAFk5BQAAAAAJAEABfGtfkSAAAA==\"}}";

        final LteRrc.Builder builder = LteRrc.newBuilder();
        try
        {
            jsonParser.merge(inputJson, builder);
        } catch (InvalidProtocolBufferException e)
        {
            Assertions.fail("Could not convert a JSON string to a protobuf object", e);
        }

        final LteRrc convertedRecord = builder.build();

        assertEquals("0.3.0", convertedRecord.getVersion());
        assertEquals("LteRrc", convertedRecord.getMessageType());

        final LteRrcData data = convertedRecord.getData();
        assertEquals("Device 1", data.getDeviceSerialNumber());
        assertEquals("My Phone", data.getDeviceName());
        assertEquals("2020-12-17T16:21:42.982-05:00", data.getDeviceTime());
        assertEquals(51.470334, data.getLatitude());
        assertEquals(-0.486594, data.getLongitude());
        assertEquals(13.3f, data.getAltitude());
        assertEquals(LteRrcChannelType.BCCH_BCH, data.getChannelType());
        assertArrayEquals(Base64.getDecoder().decode("FA4wAO0BawMAAFk5BQAAAAAJAEABfGtfkSAAAA=="), data.getRawMessage().toByteArray());
    }
}
