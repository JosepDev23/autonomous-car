package autonomouscar.mapek.lite.adaptation.resources.rules;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import sua.autonomouscar.interfaces.EDrivingLevel;

public class ADS_2_AdaptationRule extends AdaptationRule {
	public static String id = "ADS_2_AdaptationRule";
	
	IKnowledgeProperty kp_EngineHealth = null;
	IKnowledgeProperty kp_DrivingLevel = null;
	
	public ADS_2_AdaptationRule(BundleContext context) {
		super(context, id);
		
		this.setListenToKnowledgePropertyChanges("EngineHealth");
		
		kp_EngineHealth = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("EngineHealth");
		kp_DrivingLevel = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DrivingLevel");		
	}
	
	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty arg0) {
		return kp_EngineHealth.getValue().equals(false); 
	}
	
	@Override()
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) {
		IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration = SystemConfigurationHelper
				.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		kp_DrivingLevel.setValue(EDrivingLevel.L0_ManualDriving);
		
		SystemConfigurationHelper.componentToRemove(ruleComponentsSystemConfiguration, "driving.L1.AssistedDriving", "1.0.0");
		SystemConfigurationHelper.componentToRemove(ruleComponentsSystemConfiguration, "driving.L2.AdaptiveCruiseControl", "1.0.0");
		SystemConfigurationHelper.componentToRemove(ruleComponentsSystemConfiguration, "driving.L2.LaneKeepingAssist", "1.0.0");
		SystemConfigurationHelper.componentToRemove(ruleComponentsSystemConfiguration, "driving.L3.CityChauffer", "1.0.0");
		SystemConfigurationHelper.componentToRemove(ruleComponentsSystemConfiguration, "driving.L3.TrafficJamChauffer", "1.0.0");
		SystemConfigurationHelper.componentToRemove(ruleComponentsSystemConfiguration, "driving.L3.HighwayChauffer", "1.0.0");

		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "driving.L0.ManualDriving", "1.0.0");

		return ruleComponentsSystemConfiguration;
	}
}
