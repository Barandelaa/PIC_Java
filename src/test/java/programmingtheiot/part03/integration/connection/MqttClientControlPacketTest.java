/**
 * 
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * Copyright (c) 2020 by Andrew D. King
 */ 

package programmingtheiot.part03.integration.connection;

import static org.junit.Assert.*;

import java.util.logging.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.*;
import programmingtheiot.gda.connection.*;

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
		// Connect to the broker to generate CONNECT and CONNACK packets
		assertTrue(this.mqttClient.connectClient());

		// Disconnect from the broker to generate DISCONNECT packet
		assertTrue(this.mqttClient.disconnectClient());
	}
	
	@Test
	public void testServerPing()
	{
		// Connect to the broker
		assertTrue(this.mqttClient.connectClient());

		// Wait for the keep-alive interval to generate PINGREQ and PINGRESP packets
		try {
			Thread.sleep(5000); // Adjust this based on the keep-alive interval
		} catch (InterruptedException e) {
			_Logger.warning("Interrupted while waiting for PINGREQ and PINGRESP packets.");
		}

		// Disconnect from the broker
		assertTrue(this.mqttClient.disconnectClient());
	}
	
	@Test
	public void testPubSub()
	{
		// Connect to the broker
		assertTrue(this.mqttClient.connectClient());

		// Subscribe to a topic to generate SUBSCRIBE and SUBACK packets
		assertTrue(this.mqttClient.subscribeToTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, 1));

		// Publish a message with QoS 1 to generate PUBLISH and PUBACK packets
		assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "Test message QoS 1", 1));

		// Publish a message with QoS 2 to generate PUBLISH, PUBREC, PUBREL, and PUBCOMP packets
		assertTrue(this.mqttClient.publishMessage(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE, "Test message QoS 2", 2));

		// Unsubscribe from the topic to generate UNSUBSCRIBE and UNSUBACK packets
		assertTrue(this.mqttClient.unsubscribeFromTopic(ResourceNameEnum.GDA_MGMT_STATUS_MSG_RESOURCE));

		// Disconnect from the broker
		assertTrue(this.mqttClient.disconnectClient());
	}
	
}
