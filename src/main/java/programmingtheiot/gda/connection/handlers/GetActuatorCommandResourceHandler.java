package programmingtheiot.gda.connection.handlers;

import org.eclipse.californium.core.CoapResource;

import programmingtheiot.common.IActuatorDataListener;
import programmingtheiot.data.ActuatorData;
import programmingtheiot.data.DataUtil;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;



public class GetActuatorCommandResourceHandler extends CoapResource implements IActuatorDataListener {
    // static

	// logging infrastructure - should already be defined, although you'll need
	// to update the class name as shown below
	private static final Logger _Logger =
		Logger.getLogger(GetActuatorCommandResourceHandler.class.getName());

	// params

	private ActuatorData actuatorData = null;

	// constructors
    public GetActuatorCommandResourceHandler(String resourceName)
    {
	super(resourceName);

	// set the resource to be observable
	super.setObservable(true);
    }

    public boolean onActuatorDataUpdate(ActuatorData data)
    {
	if (data != null && this.actuatorData != null) {
		this.actuatorData.updateData(data);

		// notify all connected clients
		super.changed();

		_Logger.fine("Actuator data updated for URI: " + super.getURI() + ": Data value = " + this.actuatorData.getValue());

		return true;
	}

	return false;
    }

    @Override
    public void handleGET(CoapExchange context)
    {
    // TODO: validate 'context'

    // accept the request
    context.accept();

    // TODO: convert the locally stored ActuatorData to JSON using DataUtil
    String jsonData = DataUtil.getInstance().actuatorDataToJson(this.actuatorData);

    // TODO: generate a response message, set the content type, and set the response code

    // send an appropriate response
    context.respond(ResponseCode.CONTENT, jsonData);
    }
}
