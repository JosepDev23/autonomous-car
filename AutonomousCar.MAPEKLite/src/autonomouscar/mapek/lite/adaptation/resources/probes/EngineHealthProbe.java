package autonomouscar.mapek.lite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;
import sua.autonomouscar.devices.interfaces.IEngine;

public class EngineHealthProbe extends Probe implements ServiceListener {
	public static String id = "EngineHealthProbe";

	public EngineHealthProbe(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
		try {
			IEngine engine = OSGiUtils.getService(context, IEngine.class);
			context.addServiceListener(this, "(objectclass="+ IEngine.class.getName()+")");
		} catch (Exception e) {
			this.logger.error("ERROR: " + id);
		}
	}
	
	@Override
	public void serviceChanged(ServiceEvent event) {
		int type = event.getType();
		switch (type) {
	        case ServiceEvent.REGISTERED:
	            logger.info("Engine registered");
	            this.reportMeasure(true);
	            break;
	
	        case ServiceEvent.MODIFIED:
	            logger.info("Engine modified");
	            break;
	
	        case ServiceEvent.UNREGISTERING:
	            logger.info("Engine unregistering");
	            this.reportMeasure(false);
	            break;
	
	        default:
	            logger.debug("Unhandled ServiceEvent type: " + type);
		}
	}
}
