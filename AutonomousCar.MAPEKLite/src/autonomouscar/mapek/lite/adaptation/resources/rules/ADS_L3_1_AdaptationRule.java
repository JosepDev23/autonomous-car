package autonomouscar.mapek.lite.adaptation.resources.rules;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.components.OSGiUtils;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
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
			ServiceReference<?> ref_RoadSensorARC = context.getServiceReference(RoadSensorARC.class.getName());
			if (ref_RoadSensorARC != null) {
				
			}
			
		}
		
		return SystemConfigurationHelper
				.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
	}
}
