/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public class DataUtil
{
	// static
	
	private static final DataUtil _Instance = new DataUtil();
	private static final Logger _Logger   =
	Logger.getLogger(DataUtil.class.getName());


	/**
	 * Returns the Singleton instance of this class.
	 * 
	 * @return ConfigUtil
	 */
	public static final DataUtil getInstance()
	{
		return _Instance;
	}
	
	
	// private var's
	

	
	// constructors
	
	/**
	 * Default (private).
	 * 
	 */
	private DataUtil()
	{
		super();
	}
	
	
	// public methods

	
	public String actuatorDataToJson(ActuatorData actuatorData)
	{
		String jsonData =null;

		if (actuatorData != null) {
		Gson gson =new Gson();
		jsonData =gson.toJson(actuatorData);
				}

		return jsonData;
	}
	
	public String sensorDataToJson(SensorData sensorData)
	{
		String jsonData =null;

		if (sensorData != null) {
		Gson gson =new Gson();
		jsonData =gson.toJson(sensorData);
				}

		return jsonData;
	}
	
	public String systemPerformanceDataToJson(SystemPerformanceData sysPerfData)
	{
		String jsonData =null;

		if (sysPerfData != null) {
		Gson gson =new Gson();
		jsonData =gson.toJson(sysPerfData);
				}

		return jsonData;
	}
	
	public String systemStateDataToJson(SystemStateData sysStateData)
	{
		String jsonData =null;

		if (sysStateData != null) {
		Gson gson =new Gson();
		jsonData =gson.toJson(sysStateData);
				}

		return jsonData;
	}
	
	public ActuatorData jsonToActuatorData(String jsonData)
	{
		ActuatorData data =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
		Gson gson =new Gson();
		data =gson.fromJson(jsonData,ActuatorData.class);
				}

		return data;
			}
	
	
	public SensorData jsonToSensorData(String jsonData)
	{
		SensorData data =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
		Gson gson =new Gson();
		data =gson.fromJson(jsonData,SensorData.class);
				}

		return data;
	}
	
	public SystemPerformanceData jsonToSystemPerformanceData(String jsonData)
	{
		SystemPerformanceData data =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
		Gson gson =new Gson();
		data =gson.fromJson(jsonData,SystemPerformanceData.class);
				}

		return data;
	}
	
	public SystemStateData jsonToSystemStateData(String jsonData)
	{
		SystemStateData data =null;

		if (jsonData !=null &&jsonData.trim().length() >0) {
		Gson gson =new Gson();
		data =gson.fromJson(jsonData,SystemStateData.class);
				}

		return data;
	}
	
}
