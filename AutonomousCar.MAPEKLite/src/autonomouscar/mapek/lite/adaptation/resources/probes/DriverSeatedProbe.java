package autonomouscar.mapek.lite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;
import sua.autonomouscar.devices.interfaces.ISeatSensor;

public class DriverSeatedProbe extends Probe implements ServiceListener {
	public static String id = "DriverSeatedProbe";
	
	public DriverSeatedProbe(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
		try {
			ISeatSensor seatSensor = OSGiUtils.getService(context, ISeatSensor.class);
			context.addServiceListener(this, "(objectclass="+ ISeatSensor.class.getName()+")");
		} catch (Exception e) {
			this.logger.error("ERROR: " + id);
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		int type = event.getType();
		switch (type) {
	        case ServiceEvent.REGISTERED:
	            logger.info("SeatSensor registered");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.MODIFIED:
	            logger.info("SeatSensor modified");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.UNREGISTERING:
	            logger.info("SeatSensor unregistering");	            
	            break;
	
	        default:
	            logger.debug("Unhandled ServiceEvent type: " + type);
		}
	}
	
	private void reportFromEvent(ServiceEvent event) {
        ServiceReference<?> ref = event.getServiceReference();
        Object occupied = ref.getProperty("occupied");
 
        if (occupied != null) {
            this.reportMeasure(occupied);
        } else {
            logger.warn("Property 'occupied' not found on FaceMonitor");
        }
    }

}
