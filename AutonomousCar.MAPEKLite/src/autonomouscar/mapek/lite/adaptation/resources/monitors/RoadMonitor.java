package autonomouscar.mapek.lite.adaptation.resources.monitors;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.Monitor;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IMonitor;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;

public class RoadMonitor extends Monitor {
	public static String id = "RoadMonitor";
	
	public RoadMonitor(BundleContext context) {
		super(context, id);
		this.logger.info(id + " Starting");
	}
	
	@Override
	public IMonitor report(Object measure) {

		this.logger.info(String.format("%s: Received measure: %s", id, measure.toString()));

		try {
			String value = measure.toString();

			if (value.equals("HIGHWAY") 
			|| value.equals("OFF_ROAD") 
			|| value.equals("STD_ROAD")
			|| value.equals("CITY")) {
				IKnowledgeProperty roadTypeKnowledgeProperty = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("RoadType");
				if (roadTypeKnowledgeProperty.getValue() == null || roadTypeKnowledgeProperty.getValue() != value) {
					this.logger.info(String.format("%s: Updating Knowledge Property %s to %s", id, roadTypeKnowledgeProperty.getId(), value));
					roadTypeKnowledgeProperty.setValue(ERoadType.valueOf(value));
				}
			} else if (value.equals("FLUID") 
			|| value.equals("JAM") 
			|| value.equals("COLLAPSED")) {
				IKnowledgeProperty roadStatusKnowledgeProperty = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("RoadStatus");
				if (roadStatusKnowledgeProperty.getValue() == null || roadStatusKnowledgeProperty.getValue() != value) {
					this.logger.info(String.format("%s: Updating Knowledge Property %s to %s", id, roadStatusKnowledgeProperty.getId(), value));
					roadStatusKnowledgeProperty.setValue(ERoadStatus.valueOf(value));
				}
			}

		} catch (Exception e) {
			return this;
		}
		
		return this;
	}
}
