package autonomouscar.mapek.lite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import sua.autonomouscar.devices.interfaces.IFaceMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;

public class DriverFaceProbe extends Probe implements ServiceListener {
	
	public static String id = "DriverFaceProbe";
	
	public DriverFaceProbe(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
		try {
			IFaceMonitor driverFaceMonitor = OSGiUtils.getService(context, IFaceMonitor.class);
			context.addServiceListener(this, "(objectclass="+ IFaceMonitor.class.getName()+")");
		} catch (Exception e) {
			this.logger.error("ERROR: " + id);
		}
	}

	@Override
	public void serviceChanged(ServiceEvent event) {
		int type = event.getType();
		switch (type) {
	        case ServiceEvent.REGISTERED:
	            logger.info("FaceMonitor registered");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.MODIFIED:
	            logger.info("FaceMonitor modified");
	            reportFromEvent(event);
	            break;
	
	        case ServiceEvent.UNREGISTERING:
	            logger.info("FaceMonitor unregistering");	            
	            break;
	
	        default:
	            logger.debug("Unhandled ServiceEvent type: " + type);
		}
	}
	
	private void reportFromEvent(ServiceEvent event) {
        ServiceReference<?> ref = event.getServiceReference();
        Object faceStatus = ref.getProperty("face-status");
 
        if (faceStatus != null) {
            this.reportMeasure(faceStatus);
        } else {
            logger.warn("Property 'face-status' not found on FaceMonitor");
        }
    }

}
