package autonomouscar.mapek.lite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.devices.interfaces.IRoadSensor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;

public class RoadProbe extends Probe implements ServiceListener {
	
	public static String id = "RoadTypeProbe";

	public RoadProbe(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
		try {
			IRoadSensor roadSensor = OSGiUtils.getService(context, IRoadSensor.class);
			context.addServiceListener(this, "(objectclass="+ IRoadSensor.class.getName()+")");
		} catch (Exception e) {
			this.logger.error("ERROR: " + id);
		}
		
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		int type = event.getType();
		switch (type) {
	        case ServiceEvent.REGISTERED:
	            logger.info("RoadSensor registered");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.MODIFIED:
	            logger.info("RoadSensor modified");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.UNREGISTERING:
	            logger.info("RoadSensor unregistering");	            
	            break;
	
	        default:
	            logger.debug("Unhandled ServiceEvent type: " + type);
		}
	}
	
	private void reportFromEvent(ServiceEvent event) {
        ServiceReference<?> ref = event.getServiceReference();
        Object roadType = ref.getProperty("road-type");
        Object roadStatus = ref.getProperty("road-status");
 
        if (roadType != null) {
            this.reportMeasure(roadType);
        } else {
            logger.warn("Property 'road-type' not found on RoadSensor");
        }
 
        if (roadStatus != null) {
            this.reportMeasure(roadStatus);
        } else {
            logger.warn("Property 'road-status' not found on RoadSensor");
        }
    }

}
