/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

package programmingtheiot.data;

import java.io.Serializable;

import programmingtheiot.common.ConfigConst;

/**
 * Shell representation of class for student implementation.
 *
 */
public class ActuatorData extends BaseIotData implements Serializable
{

	// static
	private int command      =ConfigConst.DEFAULT_COMMAND;
	private float value        =ConfigConst.DEFAULT_VAL;
	private boolean isResponse   =false;
	private String stateData    = "" ;
	
	// private var's
	
    
    
	// constructors
	
	/**
	 * Default.
	 * 
	 */
	public ActuatorData()
	{
		super();
	}
	
	
	// public methods
	
	public int getCommand()
	{
		return this.command;
	}
	
	public float getValue()
	{
		return this.value;
	}

	public String getStateData()
	{
		return this.stateData;
	}
	
	public boolean isResponseFlagEnabled()
	{
	return this.isResponse;	
	}
	
	public void setAsResponse()
	{
		updateTimeStamp();
		this.isResponse =true;
	}
	
	public void setCommand(int command)
	{
		updateTimeStamp();
		this.command =command;
	}
	public void setStateData(String stateData)
	{
		updateTimeStamp();

		if (stateData !=null) {
			this.stateData =stateData;
		}
	}

	public void setValue(float val)
	{
		updateTimeStamp();
		this.value =val;
	}
	
	/**
	 * Returns a string representation of this instance. This will invoke the base class
	 * {@link #toString()} method, then append the output from this call.
	 * 
	 * @return String The string representing this instance, returned in CSV 'key=value' format.
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder(super.toString());
		
		sb.append(',');
		sb.append(ConfigConst.COMMAND_PROP).append('=').append(this.getCommand()).append(',');
		sb.append(ConfigConst.IS_RESPONSE_PROP).append('=').append(this.isResponseFlagEnabled()).append(',');
		sb.append(ConfigConst.VALUE_PROP).append('=').append(this.getValue());
		
		return sb.toString();
	}
	
	
	// protected methods
	
	/* (non-Javadoc)
	 * @see programmingtheiot.data.BaseIotData#handleUpdateData(programmingtheiot.data.BaseIotData)
	 */
	protected void handleUpdateData(BaseIotData data)
	{
		if (data instanceof ActuatorData) {
			ActuatorData aData = (ActuatorData)data;
			this.setCommand(aData.getCommand());
			this.setValue(aData.getValue());
			this.setStateData(aData.getStateData());
			
			if (aData.isResponseFlagEnabled()) {
			this.isResponse =true;
						}
					}
	}
	
}
