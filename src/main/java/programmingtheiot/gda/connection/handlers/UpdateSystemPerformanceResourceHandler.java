package programmingtheiot.gda.connection.handlers;

import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;
import programmingtheiot.data.DataUtil;
import programmingtheiot.data.SystemPerformanceData;
import programmingtheiot.gda.connection.handlers.GenericCoapResourceHandler;

import java.util.logging.Logger;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;
import programmingtheiot.common.IDataMessageListener;
import programmingtheiot.common.ResourceNameEnum;

public class UpdateSystemPerformanceResourceHandler extends CoapResource {

	private static final Logger _Logger = Logger.getLogger(UpdateSystemPerformanceResourceHandler.class.getName());

    private IDataMessageListener dataMsgListener = null;

    public UpdateSystemPerformanceResourceHandler(String resourceName)
    { 
	super(resourceName);
    }
    public void setDataMessageListener(IDataMessageListener listener)
    {
        if (listener != null) {
            this.dataMsgListener = listener;
        }
    }

    @Override
    public void handlePUT(CoapExchange context)
    {
        ResponseCode code = ResponseCode.NOT_ACCEPTABLE;

	context.accept();

	if (this.dataMsgListener != null) {
		try {
			String jsonData = new String(context.getRequestPayload());

			SystemPerformanceData sysPerfData =
				DataUtil.getInstance().jsonToSystemPerformanceData(jsonData);

			// TODO: Choose the following (but keep it idempotent!)
			//   1) Check MID to see if it’s repeated for some reason
			//      - optional, as the underlying lib should handle this
			//   2) Cache the previous update – is the PAYLOAD repeated?
			//   2) Delegate the data check to this.dataMsgListener

			this.dataMsgListener.handleSystemPerformanceMessage(
				ResourceNameEnum.CDA_SYSTEM_PERF_MSG_RESOURCE, sysPerfData);

			code = ResponseCode.CHANGED;
		} catch (Exception e) {
			_Logger.warning(
				"Failed to handle PUT request. Message: " +
					e.getMessage());

			code = ResponseCode.BAD_REQUEST;
		}
	} else {
		_Logger.info(
			"No callback listener for request. Ignoring PUT.");

		code = ResponseCode.CONTINUE;
	}

	String msg =
		"Update system perf data request handled: " + super.getName();

	context.respond(code, msg);
    }

    @Override
	public void handleDELETE(CoapExchange context)
	{
		try {
			_Logger.info("DELETE request received for resource: " + super.getName());
			context.accept();
			context.respond(ResponseCode.DELETED, "DELETE request handled.");
		} catch (Exception e) {
			_Logger.severe("Exception occurred while handling DELETE request: " + e.getMessage());
			context.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Error handling DELETE request.");
		}
	}
	
	@Override
	public void handleGET(CoapExchange context)
	{
		try {
			_Logger.info("GET request received for resource: " + super.getName());
			context.accept();
			context.respond(ResponseCode.CONTENT, "GET request handled.");
		} catch (Exception e) {
			_Logger.severe("Exception occurred while handling GET request: " + e.getMessage());
			context.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Error handling GET request.");
		}
	}
	
	@Override
	public void handlePOST(CoapExchange context)
	{
		try {
			_Logger.info("POST request received for resource: " + super.getName());
			context.accept();
			context.respond(ResponseCode.CHANGED, "POST request handled.");
		} catch (Exception e) {
			_Logger.severe("Exception occurred while handling POST request: " + e.getMessage());
			context.respond(ResponseCode.INTERNAL_SERVER_ERROR, "Error handling POST request.");
		}
	}

    
}
