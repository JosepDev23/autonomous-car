package autonomouscar.mapek.lite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;

public class EngineHealthMonitor extends Monitor {
	public static String id = "EngineHealthMonitor";
	
	public EngineHealthMonitor(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
	}
	
	@Override
	public IMonitor report(Object measure) {
		
		this.logger.info(String.format("%s: Received measure: %s", id, measure));
		

		IKnowledgeProperty kp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("EngineHealth");
		if (!kp.equals(measure)) {
			kp.setValue(measure);
		}

		return this;
		
	}

}
