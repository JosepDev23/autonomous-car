package autonomouscar.mapek.lite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

public class DriverSeatedMonitor extends Monitor{
	public static String id = "DriverSeatedMonitor";
	
	public DriverSeatedMonitor(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
	}

	@Override
	public IMonitor report(Object measure) {

		this.logger.debug(String.format("Received measure: %s", measure.toString()));

		IKnowledgeProperty kp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DriverSeated");
		if (!kp.equals(measure)) {
			kp.setValue(measure);
		}

		return this;
	}

}
