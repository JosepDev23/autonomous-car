package autonomouscar.mapek.lite.adaptation.resources.rules;

import org.osgi.framework.BundleContext;

import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.components.AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.structures.systemconfiguration.interfaces.IRuleSystemConfiguration;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.AuditorySoundARC;
import sua.autonomouscar.infraestructure.interaction.ARC.HapticVibrationARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.VisualTextARC;
import sua.autonomouscar.interfaces.EFaceStatus;

public class Interact_1_3_AdaptationRule extends AdaptationRule {
	public static String id = "Interact_1_3_AdaptationRule";

	IKnowledgeProperty kp_DrivingLevel = null;
	IKnowledgeProperty kp_DriverFace = null;
	
	public Interact_1_3_AdaptationRule(BundleContext context) {
		super(context, id);

		this.setListenToKnowledgePropertyChanges("DriverFace");
		this.setListenToKnowledgePropertyChanges("DriverSeated");

		kp_DrivingLevel = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DrivingLevel");
		kp_DriverFace = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DriverFace");
	}
	
	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty property) {
		return kp_DrivingLevel.getValue().toString().contains("L3")
			&& kp_DriverFace.getValue().equals(EFaceStatus.DISTRACTED) || kp_DriverFace.getValue().equals(EFaceStatus.UNKNOWN);
	}
	
	@Override
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) {
		IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration = SystemConfigurationHelper
				.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());

		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.NotificationService", "1.0.0");
		
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.Speakers.AuditorySound", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"interaction.NotificationService", 
				"1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, 
				"interaction.Speakers.AuditorySound", 
				"1.0.0",
				AuditorySoundARC.PROVIDED_MECHANISM);
		

		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.DriverDisplay.VisualText", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"interaction.NotificationService", "1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, 
				"interaction.DriverDisplay.VisualText", "1.0.0",
				VisualTextARC.PROVIDED_MECHANISM);
		
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.SteeringWheel", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"interaction.NotificationService", "1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, 
				"interaction.SteeringWheel", "1.0.0",
				HapticVibrationARC.PROVIDED_MECHANISM);
		
		String l3 = "";
		String drivinglevel = kp_DrivingLevel.getValue().toString();
		switch (drivinglevel) {
		case "L3_HighwayChauffer":
			l3 = "L3.HighwayChauffer";
			break;
		case "L3_TrafficJamChauffer":
			l3 = "L3.TrafficJamChauffer";
			break;
		case "L3_CityChauffer":
			l3 = "L3.CityChauffer";
			break;
		}
		String drivingservice = "driving." + l3;
		
		SystemConfigurationHelper.bindingToAdd(
			ruleComponentsSystemConfiguration, 
			drivingservice, "1.0.0",
			L3_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE, 
			"interaction.NotificationService", "1.0.0",
			NotificationServiceARC.PROVIDED_SERVICE);
		
		return ruleComponentsSystemConfiguration;
	}
}
