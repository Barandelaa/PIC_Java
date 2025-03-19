/**
 * This class is part of the Programming the Internet of Things project.
 * 
 * It is provided as a simple shell to guide the student and assist with
 * implementation for the Programming the Internet of Things exercises,
 * and designed to be modified by the student as needed.
 */ 

 package programmingtheiot.gda.system;

 import static programmingtheiot.gda.system.BaseSystemUtilTask._Logger;
 
 import java.lang.management.ManagementFactory;
 import java.lang.management.MemoryUsage;
 import java.util.logging.Logger;

import java.io.File;
import programmingtheiot.common.ConfigConst;
 
 
 /**
  * Shell representation of class for student implementation.
  * 
  */
 public class SystemDiskUtilTask extends BaseSystemUtilTask
 {
     // constructors
     
     /**
      * Default.
      * 
      */
     public SystemDiskUtilTask()
     {
         super(ConfigConst.NOT_SET, ConfigConst.DEFAULT_TYPE_ID);
     }
     
     
     // public methods
     

        public float getTelemetryValue()
        {
            File root = new File("/");
            long totalSpace = root.getTotalSpace();
            long freeSpace = root.getFreeSpace();
            long usedSpace = totalSpace - freeSpace;
            return (float) (usedSpace / totalSpace) * 100.0f;
        }
     
 }
 