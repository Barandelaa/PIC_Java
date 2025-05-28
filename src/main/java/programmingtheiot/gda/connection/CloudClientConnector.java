/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.gda.connection;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SensorData;
import programmingtheiot.data.SystemPerformanceData;

/**
 * Shell representation of class for student implementation.
 *
 */
public class CloudClientConnector implements ICloudClient, IConnectionListener
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(CloudClientConnector.class.getName());
	
	// private var's
	private String topicPrefix = "";
	private MqttClientConnector mqttClient = null;
	private IDataMessageListener dataMsgListener = null;

	// TODO: set to either 0 or 1, depending on which is preferred for your implementation
	private int qosLevel = 1;
	
	

	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public CloudClientConnector()
	{
		ConfigUtil configUtil = ConfigUtil.getInstance();

		this.topicPrefix = configUtil.getProperty(
			ConfigConst.CLOUD_GATEWAY_SERVICE,
			ConfigConst.BASE_TOPIC_KEY
		);

		// Depending on the cloud service, the topic names may or may not begin with a "/", 
		// so this code should be updated according to the cloud service provider's topic naming conventions
		if (topicPrefix == null) {
			topicPrefix = "/";
		} else {
			if (!topicPrefix.endsWith("/")) {
				topicPrefix += "/";
			}
		}
		
	}
	
	
	// public methods
	
	@Override
	public boolean connectClient()
	{
		if (this.mqttClient == null) {
			this.mqttClient = new MqttClientConnector(true);
			this.mqttClient.setConnectionListener(this);
		}

		return this.mqttClient.connectClient();
	}

	@Override
	public boolean disconnectClient()
	{
		if (this.mqttClient != null && this.mqttClient.isConnected()) {
			return this.mqttClient.disconnectClient();
		}
	
		return false;
	}

	
	public void onConnect()
	{
		_Logger.info("Handling CSP subscriptions and device topic provisioninig...");

		LedEnablementMessageListener ledListener = new LedEnablementMessageListener(this.dataMsgListener);
		AlarmEnablementMessageListener alarmListener = new AlarmEnablementMessageListener(this.dataMsgListener);

		// topic may not exist yet, so create a 'response' actuation event with invalid value -
		// this will create the relevant topic if it doesn't yet exist, which ensures
		// the message listener (if coded correctly) will log a message but ignore the
		// actuation command and NOT pass it onto the IDataMessageListener instance
		ActuatorData ad = new ActuatorData();
		ad.setAsResponse();
		ad.setName(ConfigConst.LED_ACTUATOR_NAME);
		ad.setValue((float) -1.0); // NOTE: this just needs to be an invalid actuation value

		String ledTopic = createTopicName(ledListener.getResource().getDeviceName(), ad.getName());
		String adJson = DataUtil.getInstance().actuatorDataToJson(ad);

		this.publishMessageToCloud(ledTopic, adJson);

		this.mqttClient.subscribeToTopic(ledTopic, this.qosLevel, ledListener);

		// Configurar la alarma de manera similar
		ActuatorData alarmData = new ActuatorData();
		alarmData.setAsResponse();
		alarmData.setName(ConfigConst.ALARM_ACTUATOR_NAME);
		alarmData.setValue((float) -1.0);

		String alarmTopic = createTopicName(alarmListener.getResource().getDeviceName(), alarmData.getName());
		String alarmJson = DataUtil.getInstance().actuatorDataToJson(alarmData);

		this.publishMessageToCloud(alarmTopic, alarmJson);

		this.mqttClient.subscribeToTopic(alarmTopic, this.qosLevel, alarmListener);
	}

	public void onDisconnect()
	{
		_Logger.info("MQTT client disconnected. Nothing else to do.");
	}

	@Override
	public boolean setDataMessageListener(IDataMessageListener listener)
	{
		if (this.mqttClient != null) {
			this.dataMsgListener = listener;
			this.mqttClient.setDataMessageListener(listener);
			return true;
		}
	
		return false;
	}

	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SensorData data)
	{
		if (resource != null && data != null) {
			// Crear un objeto JSON en el formato que espera Ubidots
			StringBuilder jsonBuilder = new StringBuilder();
			jsonBuilder.append("{\"value\":");
			jsonBuilder.append(data.getValue());
			jsonBuilder.append(",\"context\":{\"lat\":");
			jsonBuilder.append(data.getLatitude());
			jsonBuilder.append(",\"lng\":");
			jsonBuilder.append(data.getLongitude());
			jsonBuilder.append(",\"sensorType\":\"");
			jsonBuilder.append(data.getSensorType());
			jsonBuilder.append("\",\"description\":\"");
			jsonBuilder.append(data.getDescription());
			jsonBuilder.append("\",\"name\":\"");
			jsonBuilder.append(data.getName());
			jsonBuilder.append("\",\"typeID\":");
			jsonBuilder.append(data.getTypeID());
			jsonBuilder.append("}}");
			
			String payload = jsonBuilder.toString();
			_Logger.info("Sending sensor data to Ubidots: " + payload);
			return publishMessageToCloud(resource, data.getName(), payload);
		}
		return false;
	}

	@Override
	public boolean sendEdgeDataToCloud(ResourceNameEnum resource, SystemPerformanceData data)
	{
		if (resource != null && data != null) {
			// Enviar CPU utilization
			StringBuilder cpuJsonBuilder = new StringBuilder();
			cpuJsonBuilder.append("{\"value\":");
			cpuJsonBuilder.append(data.getCpuUtilization());
			cpuJsonBuilder.append("}");
			
			boolean cpuDataSuccess = publishMessageToCloud(resource, ConfigConst.CPU_UTIL_NAME, cpuJsonBuilder.toString());
			
			if (!cpuDataSuccess) {
				_Logger.warning("Failed to send CPU utilization data to cloud service.");
			}
			
			// Enviar Memory utilization
			StringBuilder memJsonBuilder = new StringBuilder();
			memJsonBuilder.append("{\"value\":");
			memJsonBuilder.append(data.getMemoryUtilization());
			memJsonBuilder.append("}");
			
			boolean memDataSuccess = publishMessageToCloud(resource, ConfigConst.MEM_UTIL_NAME, memJsonBuilder.toString());
			
			if (!memDataSuccess) {
				_Logger.warning("Failed to send memory utilization data to cloud service.");
			}
			
			return (cpuDataSuccess && memDataSuccess);
		}
		return false;
	}

	@Override
	public boolean subscribeToCloudEvents(ResourceNameEnum resource)
	{
		boolean success = false;

	String topicName = null;

	if (this.mqttClient != null && this.mqttClient.isConnected()) {
		topicName = createTopicName(resource);

		// NOTE: This is a generic subscribe call - if you use this approach,
		// you will need to update this.mqttClient.messageReceived() to
		//   (1) identify the message source (e.g., CDA or Cloud),
		//   (2) determine the message type (e.g., actuator command), and
		//   (3) convert the payload into a data container (e.g., ActuatorData)
		//
		// Once you determine the message source and type, and convert the
		// payload to its appropriate data container, you can then determine
		// where to route the message (e.g., send to the IDataMessageListener
		// instance (which will be DeviceDataManager).
		this.mqttClient.subscribeToTopic(topicName, this.qosLevel);

			success = true;
		} else {
			_Logger.warning("Subscription methods only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}

		return success;
	}

	@Override
	public boolean unsubscribeFromCloudEvents(ResourceNameEnum resource)
	{
		boolean success = false;

		String topicName = null;

		if (this.mqttClient != null && this.mqttClient.isConnected()) {
			topicName = createTopicName(resource);

			this.mqttClient.unsubscribeFromTopic(topicName);

			success = true;
		} else {
			_Logger.warning("Unsubscribe method only available for MQTT. No MQTT connection to broker. Ignoring. Topic: " + topicName);
		}

		return success;
	}
	
	
	// private methods
	private String createTopicName(ResourceNameEnum resource)
	{
		return createTopicName(resource.getDeviceName(), resource.getResourceType());
	}

	private String createTopicName(ResourceNameEnum resource, String itemName)
	{
		return (topicPrefix + ConfigConst.GATEWAY_DEVICE + "/" + itemName).toLowerCase();
	}

	private String createTopicName(String deviceName, String resourceTypeName)
	{
		StringBuilder buf = new StringBuilder();

		if (deviceName != null && deviceName.trim().length() > 0) {
			buf.append(topicPrefix).append(deviceName);
		}

		if (resourceTypeName != null && resourceTypeName.trim().length() > 0) {
			buf.append('/').append(resourceTypeName);
		}

		return buf.toString().toLowerCase();
	}


	private boolean publishMessageToCloud(ResourceNameEnum resource, String itemName, String payload) {
		String topicName = createTopicName(resource, itemName);
		return publishMessageToCloud(topicName, payload);
	}
	
	private boolean publishMessageToCloud(String topicName, String payload) {
		try {
			_Logger.info("Publishing payload to topic: " + topicName);
			_Logger.info("Payload content: " + payload);

			this.mqttClient.publishMessage(topicName, payload.getBytes(), this.qosLevel);

			return true;
		} catch (Exception e) {
			_Logger.warning("Failed to publish message to CSP: " + topicName);
			_Logger.warning("Error details: " + e.getMessage());
		}

		return false;
	}

	
	
	private class LedEnablementMessageListener implements IMqttMessageListener
{
	private IDataMessageListener dataMsgListener = null;

	private ResourceNameEnum resource = ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE;

	private int    typeID   = ConfigConst.LED_ACTUATOR_TYPE;
	private String itemName = ConfigConst.LED_ACTUATOR_NAME;

	LedEnablementMessageListener(IDataMessageListener dataMsgListener)
	{
		this.dataMsgListener = dataMsgListener;
	}

	public ResourceNameEnum getResource()
	{
		return this.resource;
	}


	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception
	{
		try {
			String jsonData = new String(message.getPayload());

			ActuatorData actuatorData =
				DataUtil.getInstance().jsonToActuatorData(jsonData);

			// TODO: This will have to match the CDA's location ID, depending on the
			// validation logic implemented within the CDA's ActuatorAdapterManager
			actuatorData.setLocationID(ConfigConst.CONSTRAINED_DEVICE);
			actuatorData.setTypeID(this.typeID);
			actuatorData.setName(this.itemName);

			int val = (int) actuatorData.getValue();

			switch (val) {
				case ConfigConst.ON_COMMAND:
					_Logger.info("Received LED enablement message [ON].");
					actuatorData.setStateData("LED switching ON");
					break;

				case ConfigConst.OFF_COMMAND:
					_Logger.info("Received LED enablement message [OFF].");
					actuatorData.setStateData("LED switching OFF");
					break;

				default:
					return;
			}

			// There are two ways to handle passing of ActuatorData messages
			// from this method to IDataMessageListener (DeviceDataManager):
			//
			// Option 1: Pass the JSON payload (which will likely be ActuatorData).
			// Option 2: Pass the ActuatorData instance directly.
			//
			// The latest version of java-components contains a shell definition
			// for Option 2 (using Actuator Data via handleActuatorCommandRequest()).
			// If you do not have this method defined in IDataMessageListener and
			// DeviceDataManager, you can add it in, or just use Option 1.
			//
			// Choose which you'd like to use and comment out the other,
			// but DO NOT USE BOTH!

			//
			// Option 1: using JSON
			//
			if (this.dataMsgListener != null) {
				// NOTE: This conversion is useful for validation purposes and
				// to support the next line of code. You can bypass this if
				// your IDataMessageListener and DeviceDataManager implement:
				// handleActuatorCommandRequest(ActuatorData).

				jsonData = DataUtil.getInstance().actuatorDataToJson(actuatorData);

				// NOTE: The implementation of IDataMessageListener, which will be
				// DeviceDataManager, will need to parse the JSON data to handle
				// the actuator command via the handleIncomingMessage() method.
				// The implementation of handleIncomingMessage() will then
				// convert the data back into an ActuatorData instance and
				// send it to the CDA via CoAP or MQTT.
				//
				// It may seem odd to convert the payload JSON to ActuatorData
				// and then back again to JSON, only to be converted once again
				// to an ActuatorData instance. The purpose of this was originally
				// to support multiple payload types.
				this.dataMsgListener.handleIncomingMessage(
					ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, jsonData);
			}

			//
			// Option 1: using ActuatorData
			//
			//if (this.dataMsgListener != null) {
			//	this.dataMsgListener.handleActuatorCommandRequest(
			//		ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE, actuatorData);
			//}
		} catch (Exception e) {
			_Logger.warning("Failed to convert message payload to ActuatorData.");
		}
	}
	}

	private class AlarmEnablementMessageListener implements IMqttMessageListener
	{
		private IDataMessageListener dataMsgListener = null;
		private ResourceNameEnum resource = ResourceNameEnum.CDA_ACTUATOR_CMD_RESOURCE;
		private int    typeID   = ConfigConst.ALARM_ACTUATOR_TYPE;
		private String itemName = ConfigConst.ALARM_ACTUATOR_NAME;

		AlarmEnablementMessageListener(IDataMessageListener dataMsgListener)
		{
			this.dataMsgListener = dataMsgListener;
		}

		public ResourceNameEnum getResource()
		{
			return this.resource;
		}

		@Override
		public void messageArrived(String topic, MqttMessage message) throws Exception
		{
			if (message != null) {
				String payload = new String(message.getPayload());
				_Logger.info("Received message payload: " + payload);

				ActuatorData actuatorData = DataUtil.getInstance().jsonToActuatorData(payload);

				if (actuatorData != null) {
					_Logger.info("ActuatorData object created from JSON payload: " + actuatorData.getName());

					if (actuatorData.getTypeID() == this.typeID) {
						_Logger.info("Processing actuator command for " + this.itemName);

						if (this.dataMsgListener != null) {
							this.dataMsgListener.handleActuatorCommandRequest(this.resource, actuatorData);
						} else {
							_Logger.warning("No listener available for processing actuator command. Ignoring.");
						}
					} else {
						_Logger.warning("Actuator command type ID doesn't match " + this.itemName + ". Ignoring: " + actuatorData.getTypeID());
					}
				} else {
					_Logger.warning("Failed to create ActuatorData object from JSON payload: " + payload);
				}
			} else {
				_Logger.warning("Received null message. Ignoring.");
			}
		}
	}

}


