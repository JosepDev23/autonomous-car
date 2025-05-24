package autonomouscar.mapek.lite.adaptation.resources.rules;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import sua.autonomouscar.infraestructure.devices.ARC.DistanceSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.interfaces.ERoadType;

public class ADS_L3_1_AdaptationRule extends AdaptationRule {
	public static String id = "ADS_L3_1_AdaptationRule";
	
	IKnowledgeProperty kp_RoadType = null;
	IKnowledgeProperty kp_DrivingLevel = null;
	
	public ADS_L3_1_AdaptationRule(BundleContext context) {
		super(context, id);
		
		this.setListenToKnowledgePropertyChanges("RoadType");

		kp_RoadType = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("RoadType");
		kp_DrivingLevel = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DrivingLevel");
	}

	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty arg0) {
		String level = kp_DrivingLevel.getValue().toString();

		return level.toString().contains("L3");
	}
	
	@Override()
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) {
		if (kp_RoadType.getValue().equals(ERoadType.STD_ROAD) 
		|| kp_RoadType.getValue().equals(ERoadType.OFF_ROAD)) {
			IRuleComponentsSystemConfiguration theNextSystemConfiguration = SystemConfigurationHelper
					.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
			
			if (checkL2ACAvailability()) {
				SystemConfigurationHelper.componentToAdd(theNextSystemConfiguration, "device.engine", EngineARC.PROVIDED_DEVICE);
			}
			
		}
		
		return SystemConfigurationHelper
				.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
	}
	
	private boolean checkL2ACAvailability() {
		ServiceReference<?> ref_EngineARC = context.getServiceReference(EngineARC.class.getName());
		ServiceReference<?> ref_DistanceSensorARC = context.getServiceReference(DistanceSensorARC.class.getName() +".FrontDistanceSensor");
		
		return ref_EngineARC != null && ref_DistanceSensorARC != null;
    }
	
}
