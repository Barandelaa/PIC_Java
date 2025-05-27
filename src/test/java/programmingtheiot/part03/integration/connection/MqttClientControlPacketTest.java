/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

 package programmingtheiot.part03.integration.connection;

 import static org.junit.Assert.assertTrue;
 
 import java.util.logging.Logger;
 
 import org.junit.After;
 import org.junit.Before;
 import org.junit.Test;
 
 import programmingtheiot.common.ConfigConst;
 import programmingtheiot.common.ResourceNameEnum;
 import programmingtheiot.gda.connection.MqttClientConnector;
 
 /**
  * This test case class contains very basic integration tests for
  * MqttClientControlPacketTest. It should not be considered complete,
  * but serve as a starting point for the student implementing
  * additional functionality within their Programming the IoT
  * environment.
  *
  */
 public class MqttClientControlPacketTest
 {
	 // static
	 
	 private static final Logger _Logger =
		 Logger.getLogger(MqttClientControlPacketTest.class.getName());
	 
	 
	 // member var's
	 
	 private MqttClientConnector mqttClient = null;
	 
	 
	 // test setup methods
	 
	 @Before
	 public void setUp() throws Exception
	 {
		 this.mqttClient = new MqttClientConnector();
	 }
	 
	 @After
	 public void tearDown() throws Exception
	 {
	 }
	 
	 // test methods
	 
	 @Test
	 public void testConnectAndDisconnect()
	 {
		 boolean isConnected = this.mqttClient.connectClient();
		 assertTrue("Client failed to connect", isConnected);
 
		 // Wait to ensure Wireshark can capture CONNECT / CONNACK
		 try {
			 Thread.sleep(2000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
 
		 boolean isDisconnected = this.mqttClient.disconnectClient();
		 assertTrue("Client failed to disconnect", isDisconnected);
 
		 // Wait again for DISCONNECT capture
		 try {
			 Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
	 }
 
	 
	 @Test
	 public void testServerPing()
	 {
		 this.mqttClient.connectClient();
 
		 // Wait longer than keep-alive to trigger PINGREQ / PINGRESP
		 int waitTimeSec = ConfigConst.DEFAULT_KEEP_ALIVE + 10;
 
		 try {
			 Thread.sleep(waitTimeSec * 1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
 
		 this.mqttClient.disconnectClient();
	 }
 
	 
	 @Test
	 public void testPubSub()
	 {
		 this.mqttClient.connectClient();
 
		 try {
			 Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
 
		 ResourceNameEnum topic = ResourceNameEnum.CDA_SENSOR_MSG_RESOURCE;
 
		 // Subscribe (QoS 2)
		 assertTrue("Subscribe failed", this.mqttClient.subscribeToTopic(topic, 2));
 
		 // Wait for SUBSCRIBE/SUBACK
		 try {
			 Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
 
		 // Publish with QoS 1 (for PUBACK)
		 assertTrue("QoS 1 publish failed", this.mqttClient.publishMessage(topic, "QoS 1 message", 1));
 
		 // Publish with QoS 2 (for PUBREC, PUBREL, PUBCOMP)
		 assertTrue("QoS 2 publish failed", this.mqttClient.publishMessage(topic, "QoS 2 message", 2));
 
		 try {
			 Thread.sleep(3000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
 
		 // Unsubscribe
		 assertTrue("Unsubscribe failed", this.mqttClient.unsubscribeFromTopic(topic));
 
		 // Wait for UNSUBSCRIBE/UNSUBACK
		 try {
			 Thread.sleep(1000);
		 } catch (InterruptedException e) {
			 e.printStackTrace();
		 }
 
		 this.mqttClient.disconnectClient();
	 }
 
	 
 }