package autonomouscar.mapek.lite.adaptation.resources.probes;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import sua.autonomouscar.devices.interfaces.IRoadSensor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Probe;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;

public class RoadTypeProbe extends Probe implements ServiceListener {
	
	public static String id = "RoadTypeProbe";

	public RoadTypeProbe(BundleContext context) {
		super(context, id);
	}
	
	@Override
	public void serviceChanged(ServiceEvent event) {
		IRoadSensor roadSensor = OSGiUtils.getService(context, IRoadSensor.class);
		System.out.println("El ROAD SENSOR: " + roadSensor.getRoadType());
	}

}
