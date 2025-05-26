package autonomouscar.mapek.lite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;
import sua.autonomouscar.devices.interfaces.IHandsOnWheelSensor;

public class DriverHandsProbe extends Probe implements ServiceListener {
	public static String id = "DriverHandsProbe";
	
	public DriverHandsProbe(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
		try {
			IHandsOnWheelSensor handsOnWheelSensor = OSGiUtils.getService(context, IHandsOnWheelSensor.class);
			context.addServiceListener(this, "(objectclass="+ IHandsOnWheelSensor.class.getName()+")");
		} catch (Exception e) {
			this.logger.error("ERROR: " + id);
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		int type = event.getType();
		switch (type) {
	        case ServiceEvent.REGISTERED:
	            logger.info("HandsOnWheelSensor registered");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.MODIFIED:
	            logger.info("HandsOnWheelSensor modified");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.UNREGISTERING:
	            logger.info("HandsOnWheelSensor unregistering");	            
	            break;
	
	        default:
	            logger.debug("Unhandled ServiceEvent type: " + type);
		}
	}
	
	private void reportFromEvent(ServiceEvent event) {
        ServiceReference<?> ref = event.getServiceReference();
        Object handsOnWheel = ref.getProperty("hands-on-wheel");
 
        if (handsOnWheel != null) {
            this.reportMeasure(handsOnWheel);
        } else {
            logger.warn("Property 'hands-on-wheel' not found on FaceMonitor");
        }
    }

}
