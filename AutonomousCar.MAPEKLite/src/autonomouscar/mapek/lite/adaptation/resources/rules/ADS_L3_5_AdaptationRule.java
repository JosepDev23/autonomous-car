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
import sua.autonomouscar.infraestructure.devices.ARC.DriverFaceMonitorARC;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.HandsOnWheelSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.HumanSensorsARC;
import sua.autonomouscar.infraestructure.devices.ARC.LineSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.RoadSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SeatSensorARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.AuditorySoundARC;
import sua.autonomouscar.infraestructure.interaction.ARC.NotificationServiceARC;
import sua.autonomouscar.infraestructure.interaction.ARC.VisualIconARC;
import sua.autonomouscar.infraestructure.interaction.ARC.VisualTextARC;
import sua.autonomouscar.interfaces.EDrivingLevel;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;

public class ADS_L3_5_AdaptationRule extends AdaptationRule {
	public static String id = "ADS_L3_5_AdaptationRule";
	
	IKnowledgeProperty kp_RoadType = null;	
	IKnowledgeProperty kp_RoadStatus = null;
	IKnowledgeProperty kp_DrivingLevel = null;

	public ADS_L3_5_AdaptationRule(BundleContext context) {
		super(context, id);
		
		this.setListenToKnowledgePropertyChanges("RoadType");

		kp_RoadType = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("RoadType");
		kp_RoadStatus = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("RoadStatus");
		kp_DrivingLevel = BasicMAPEKLiteLoopHelper.getKnowledgeProperty("DrivingLevel");		
	}
	
	@Override
	public boolean checkAffectedByChange(IKnowledgeProperty arg0) {
		return kp_DrivingLevel.getValue().equals(EDrivingLevel.L3_TrafficJamChauffer) 
				&& kp_RoadType.getValue().equals(ERoadType.CITY)
				&& (kp_RoadStatus.getValue().equals(ERoadStatus.JAM) || kp_RoadStatus.getValue().equals(ERoadStatus.COLLAPSED));
	}

	@Override()
	public IRuleSystemConfiguration onExecute(IKnowledgeProperty property) {
		IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration = SystemConfigurationHelper
				.createPartialSystemConfiguration(this.getId() + "_" + ITimeStamped.getCurrentTimeStamp());
		
		kp_DrivingLevel.setValue(EDrivingLevel.L3_CityChauffer);
		
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.RoadSensor", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"driving.L3.CityChauffer", 
				"1.0.0", 
				L3_DrivingServiceARC.REQUIRED_ROADSENSOR, 
				"device.RoadSensor", 
				"1.0.0", 
				RoadSensorARC.PROVIDED_SENSOR);
		
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.Steering", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"driving.L3.CityChauffer", 
				"1.0.0", 
				L3_DrivingServiceARC.REQUIRED_STEERING, 
				"device.Steering", 
				"1.0.0", 
				SteeringARC.PROVIDED_DEVICE);
		
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.Engine", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"driving.L3.CityChauffer", 
				"1.0.0", 
				L3_DrivingServiceARC.REQUIRED_ENGINE, 
				"device.Engine", 
				"1.0.0", 
				EngineARC.PROVIDED_DEVICE);
		
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "driving.FallbackPlan.ParkInTheRoadShoulder", "1.0.0");
		SystemConfigurationHelper.bindingToAdd(
				ruleComponentsSystemConfiguration, 
				"driving.L3.CityChauffer", 
				"1.0.0", 
				L3_DrivingServiceARC.REQUIRED_FALLBACKPLAN, 
				"driving.FallbackPlan.ParkInTheRoadShoulder", 
				"1.0.0", 
				FallbackPlanARC.PROVIDED_DRIVINGSERVICE);

		bindHumanSensors(ruleComponentsSystemConfiguration);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "driving.L3.CityChauffer", "1.0.0",
				L3_DrivingServiceARC.REQUIRED_HUMANSENSORS, "device.HumanSensors", "1.0.0",
				HumanSensorsARC.PROVIDED_SENSOR);
		
		bindNotifications(ruleComponentsSystemConfiguration);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "driving.L3.CityChauffer", "1.0.0",
				L3_DrivingServiceARC.REQUIRED_NOTIFICATIONSERVICE, "interaction.NotificationService", "1.0.0",
				NotificationServiceARC.PROVIDED_SERVICE);
		
		bindDistanceSensors(ruleComponentsSystemConfiguration, "driving.L3.CityChauffer");
		
		bindLineSensors(ruleComponentsSystemConfiguration, "driving.L3.CityChauffer");
	
		return ruleComponentsSystemConfiguration;
	}
	
	private void bindHumanSensors(IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration) {
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.HumanSensors", "1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.DriverFaceMonitor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.DriverSeatSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.CopilotSeatSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.HandsOnWheelSensor", "1.0.0");

		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "device.HumanSensors", "1.0.0",
				HumanSensorsARC.REQUIRED_FACEMONITOR, "device.DriverFaceMonitor", "1.0.0",
				DriverFaceMonitorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "device.HumanSensors", "1.0.0",
				HumanSensorsARC.REQUIRED_DRIVERSEATSENSOR, "device.DriverSeatSensor", "1.0.0",
				SeatSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "device.HumanSensors", "1.0.0",
				HumanSensorsARC.REQUIRED_COPILOTSEATSENSOR, "device.CopilotSeatSensor", "1.0.0",
				SeatSensorARC.PROVIDED_SENSOR);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "device.HumanSensors", "1.0.0",
				HumanSensorsARC.REQUIRED_HANDSONWHEELSENSOR, "device.HandsOnWheelSensor", "1.0.0",
				HandsOnWheelSensorARC.PROVIDED_SENSOR);
	}
	
	private void bindNotifications(IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration) {
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.NotificationService",
				"1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.DriverDisplay.VisualText",
				"1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.DriverDisplay.VisualIcon",
				"1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.Speakers.AuditorySound",
				"1.0.0");
		SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "interaction.DashboardDisplay.VisualText",
				"1.0.0");

		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "interaction.NotificationService", "1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, "interaction.DriverDisplay.VisualText", "1.0.0",
				VisualTextARC.PROVIDED_MECHANISM);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "interaction.NotificationService", "1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, "interaction.DriverDisplay.VisualIcon", "1.0.0",
				VisualIconARC.PROVIDED_MECHANISM);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "interaction.NotificationService", "1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, "interaction.Speakers.AuditorySound", "1.0.0",
				AuditorySoundARC.PROVIDED_MECHANISM);
		SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, "interaction.NotificationService", "1.0.0",
				NotificationServiceARC.REQUIRED_SERVICE, "interaction.DashboardDisplay.VisualText", "1.0.0",
				VisualIconARC.PROVIDED_MECHANISM);
	}
	
	private void bindDistanceSensors(IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration,
			String drivingservice) {
		ServiceReference<?> ref_FrontDistanceSensorARC = context.getServiceReference(DistanceSensorARC.class.getName() +".FrontDistanceSensor");
		if (ref_FrontDistanceSensorARC != null) {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.FrontDistanceSensor", "1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR, "device.FrontDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		} else {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.LIDAR.FrontDistanceSensor",
					"1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_FRONTDISTANCESENSOR, "device.LIDAR.FrontDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		}
		ServiceReference<?> ref_RearDistanceSensorARC = context.getServiceReference(DistanceSensorARC.class.getName() +".RearDistanceSensor");
		if (ref_RearDistanceSensorARC != null) {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.RearDistanceSensor", "1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR, "device.RearDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		} else {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.LIDAR.RearDistanceSensor",
					"1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_REARDISTANCESENSOR, "device.LIDAR.RearDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		}
		ServiceReference<?> ref_LeftDistanceSensorARC = context.getServiceReference(DistanceSensorARC.class.getName() +".LeftDistanceSensor");
		if (ref_LeftDistanceSensorARC != null) {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.LeftDistanceSensor", "1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR, "device.LeftDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		} else {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.LIDAR.LeftDistanceSensor",
					"1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_LEFTDISTANCESENSOR, "device.LIDAR.LeftDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		}
		ServiceReference<?> ref_RightDistanceSensorARC = context.getServiceReference(DistanceSensorARC.class.getName() +".RightDistanceSensor");
		if (ref_RightDistanceSensorARC != null) {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.RightDistanceSensor", "1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR, "device.RightDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		} else {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.LIDAR.RightDistanceSensor",
					"1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_RIGHTDISTANCESENSOR, "device.LIDAR.RightDistanceSensor", "1.0.0",
					DistanceSensorARC.PROVIDED_SENSOR);
		}
	}
	
	private void bindLineSensors(IRuleComponentsSystemConfiguration ruleComponentsSystemConfiguration,
			String drivingservice) {		
		ServiceReference<?> ref_LeftLineSensor = context.getServiceReference(DistanceSensorARC.class.getName() +".LeftLineSensor");
		if (ref_LeftLineSensor != null) {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.LeftLineSensor", "1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_LEFTLINESENSOR, "device.LeftLineSensor", "1.0.0",
					LineSensorARC.PROVIDED_SENSOR);
		}
		ServiceReference<?> ref_RightLineSensor = context.getServiceReference(DistanceSensorARC.class.getName() +".RightLineSensor");
		if (ref_RightLineSensor != null) {
			SystemConfigurationHelper.componentToAdd(ruleComponentsSystemConfiguration, "device.RightLineSensor", "1.0.0");
			SystemConfigurationHelper.bindingToAdd(ruleComponentsSystemConfiguration, drivingservice, "1.0.0",
					L3_DrivingServiceARC.REQUIRED_RIGHTLINESENSOR, "device.RightLineSensor", "1.0.0",
					LineSensorARC.PROVIDED_SENSOR);
		}
	}
}
