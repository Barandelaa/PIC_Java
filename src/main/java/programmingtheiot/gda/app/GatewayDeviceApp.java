/**
 * This class is part of the Programming the Internet of Things
 * project, and is available via the MIT License, which can be
 * found in the LICENSE file at the top level of this repository.
 * 
 * You may find it more helpful to your design to adjust the
 * functionality, constants and interfaces (if there are any)
 * provided within in order to meet the needs of your specific
 * Programming the Internet of Things project.
 */ 

package programmingtheiot.gda.app;


import programmingtheiot.gda.system.SystemPerformanceManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import programmingtheiot.common.ConfigConst;
import programmingtheiot.common.ConfigUtil;

/**
 * Main GDA application.
 * 
 */
public class GatewayDeviceApp
{
	// static
	
	private static final Logger _Logger =
		Logger.getLogger(GatewayDeviceApp.class.getName());
	
	public static final long DEFAULT_TEST_RUNTIME = 60000L;
	
	// private var's
	
	private SystemPerformanceManager sysPerfMgr =null;
	private DeviceDataManager dataMgr =null;
	
	// constructors
	
	/**
	 * Constructor.
	 * 
	 * @param args
	 */
	public GatewayDeviceApp(String[] args)
	{
		super();
		
		_Logger.info("Initializing GDA...");
		
		
		this.sysPerfMgr =new SystemPerformanceManager();
	}
	
	
	// static
	
	/**
	 * Main application entry point.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		GatewayDeviceApp gwApp = new GatewayDeviceApp(args);
		
		gwApp.startApp();
		
		// TODO: custom add to ConfigConst for convenience
		boolean runForever =
		ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE,ConfigConst.ENABLE_RUN_FOREVER_KEY);

		if (runForever) {
		try {
		// TODO: make the 2000L configurable
		while (true) {
		Thread.sleep(2000L);
						}
					}catch (InterruptedException e) {
		// ignore
					}

		gwApp.stopApp(0);
				}else {
		try {
		Thread.sleep(DEFAULT_TEST_RUNTIME);
					}catch (InterruptedException e) {
		// ignore
					}

		gwApp.stopApp(0);
	}
}
	
	
	// public methods
	
	/**
	 * Initializes and starts the application.
	 * 
	 */
	public void startApp()
	{
		_Logger.info("Starting GDA...");

		try {
		if (!ConfigUtil.getInstance().getBoolean(ConfigConst.GATEWAY_DEVICE,ConfigConst.TEST_EMPTY_APP_KEY)) {
		this.dataMgr =new DeviceDataManager();
					}
		
		if (this.dataMgr !=null) {
		this.dataMgr.startManager();
					}
		
		_Logger.info("GDA started successfully.");
				}catch (Exception e) {
		_Logger.log(Level.SEVERE,"Failed to start GDA. Exiting.",e);
		
		stopApp(-1);
				}
	}
	
	/**
	 * Stops the application.
	 * 
	 * @param code The exit code to pass to {@link System.exit()}
	 */
	public void stopApp(int code)
	{
		_Logger.info("Stopping GDA...");

		try {
		if (this.dataMgr !=null) {
		this.dataMgr.stopManager();
					}
		
		_Logger.log(Level.INFO,"GDA stopped successfully with exit code {0}.",code);
				}catch (Exception e) {
		_Logger.log(Level.SEVERE,"Failed to cleanly stop GDA. Exiting.",e);
				}
		
		System.exit(code);
			}
	
	
	// private methods
	
	/**
	 * Load the config file.
	 * 
	 * NOTE: This will be added later.
	 * 
	 * @param configFile The name of the config file to load.
	 */
	private void initConfig(String configFile)
	{
		_Logger.log(Level.INFO, "Attempting to load configuration: {0}", (configFile != null ? configFile : "Default."));
		
		// TODO: Your code here
	}
	
	/**
	 * Parse any arguments passed in on app startup.
	 * <p>
	 * This method should be written to check if any valid command line args are provided,
	 * including the name of the config file. Once parsed, call {@link #initConfig(String)}
	 * with the name of the config file, or null if the default should be used.
	 * <p>
	 * If any command line args conflict with the config file, the config file
	 * in-memory content should be overridden with the command line argument(s).
	 * 
	 * @param args The non-null and non-empty args array.
	 */
	private void parseArgs(String[] args)
	{
		String configFile = null;
		
		if (args != null) {
			_Logger.log(Level.INFO, "Parsing {0} command line args.", args.length);
			
			for (String arg : args) {
				if (arg != null) {
					arg = arg.trim();
					
					// TODO: Your code here
				}
			}
		} else {
			_Logger.info("No command line args to parse.");
		}
		
		initConfig(configFile);
	}

}
