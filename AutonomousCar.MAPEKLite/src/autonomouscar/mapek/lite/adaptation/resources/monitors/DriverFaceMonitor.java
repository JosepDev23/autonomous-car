package autonomouscar.mapek.lite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.interfaces.EFaceStatus;

public class DriverFaceMonitor extends Monitor {
	public static String id = "DriverFaceMonitor";
	
	public DriverFaceMonitor(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
	}
	

	@Override
	public IMonitor report(Object measure) {

		this.logger.info(String.format("%s: Received measure: %s", id, measure.toString()));

		try {
			IKnowledgeProperty kp = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DriverFace");
			EFaceStatus value = EFaceStatus.valueOf(measure.toString());

			if (!kp.getValue().equals(value)) {
				kp.setValue(value);
				this.logger.info(String.format("%s: Updating Knowledge Property %s to %s", id, kp.getId(), value));
			}

		} catch (Exception e) {
			return this;
		}

		return this;
	}

}
