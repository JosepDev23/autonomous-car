package autonomouscar.mapek.lite.adaptation.starter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import autonomouscar.mapek.lite.adaptation.resources.monitors.DriverFaceMonitor;
import autonomouscar.mapek.lite.adaptation.resources.monitors.DriverHandsMonitor;
import autonomouscar.mapek.lite.adaptation.resources.monitors.DriverSeatedMonitor;
import autonomouscar.mapek.lite.adaptation.resources.monitors.EngineHealthMonitor;
import autonomouscar.mapek.lite.adaptation.resources.monitors.RoadMonitor;
import autonomouscar.mapek.lite.adaptation.resources.probes.DriverFaceProbe;
import autonomouscar.mapek.lite.adaptation.resources.probes.DriverHandsProbe;
import autonomouscar.mapek.lite.adaptation.resources.probes.DriverSeatedProbe;
import autonomouscar.mapek.lite.adaptation.resources.probes.EngineHealthProbe;
import autonomouscar.mapek.lite.adaptation.resources.probes.RoadProbe;
import autonomouscar.mapek.lite.adaptation.resources.rules.ADS_2_AdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.rules.ADS_L3_2_AdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.rules.ADS_L3_3_AdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.rules.ADS_L3_4_AdaptationRule;
import autonomouscar.mapek.lite.adaptation.resources.rules.ADS_L3_5_AdaptationRule;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.artifacts.interfaces.IAdaptiveReadyComponent;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.ARC.structures.systemconfiguration.interfaces.IRuleComponentsSystemConfiguration;
import es.upv.pros.tatami.adaptation.mapek.lite.artifacts.interfaces.IKnowledgeProperty;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.BasicMAPEKLiteLoopHelper;
import es.upv.pros.tatami.adaptation.mapek.lite.helpers.SystemConfigurationHelper;
import es.upv.pros.tatami.osgi.utils.interfaces.ITimeStamped;
import sua.autonomouscar.infraestructure.devices.ARC.EngineARC;
import sua.autonomouscar.infraestructure.devices.ARC.SteeringARC;
import sua.autonomouscar.infraestructure.driving.ARC.FallbackPlanARC;
import sua.autonomouscar.infraestructure.driving.ARC.L3_DrivingServiceARC;
import sua.autonomouscar.interfaces.EDrivingLevel;
import sua.autonomouscar.interfaces.EFaceStatus;
import sua.autonomouscar.interfaces.ERoadStatus;
import sua.autonomouscar.interfaces.ERoadType;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		BasicMAPEKLiteLoopHelper.BUNDLECONTEXT = bundleContext;
		BasicMAPEKLiteLoopHelper.REFERENCE_MODEL = "AutonomousCar"; //System.getProperty("model", "default-model");

		// ... adding the initial system configuration
		IComponentsSystemConfiguration theInitialSystemConfiguration = 
				SystemConfigurationHelper.createSystemConfiguration("InititalConfiguration");
		SystemConfigurationHelper.addComponent(theInitialSystemConfiguration, "device.RoadSensor", "1.0.0");
		
		BasicMAPEKLiteLoopHelper.INITIAL_SYSTEMCONFIGURATION = theInitialSystemConfiguration;

		BasicMAPEKLiteLoopHelper.MODELSREPOSITORY_FOLDER = System.getProperty("modelsrepository.folder");
		BasicMAPEKLiteLoopHelper.ADAPTATIONREPORTS_FOLDER = System.getProperty("adaptationreports.folder");
		// STARTING THE MAPE-K LOOP
		
		BasicMAPEKLiteLoopHelper.startLoopModules();
		
		BasicMAPEKLiteLoopHelper.addInitialSelfConfigurationCapabilities(createInitialSystemConfiguration());
		
		// ADAPTATION PROPERTIES
		IKnowledgeProperty kp_DrivingLevel = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("DrivingLevel");
		IKnowledgeProperty kp_RoadType = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("RoadType");
		IKnowledgeProperty kp_RoadStatus = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("RoadStatus");
		IKnowledgeProperty kp_EngineHealth = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("EngineHealth");
		IKnowledgeProperty kp_DriverFace = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("DriverFace");
		IKnowledgeProperty kp_DriverHands = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("DriverHands");
		IKnowledgeProperty kp_DriverSeated = BasicMAPEKLiteLoopHelper.createKnowledgeProperty("DriverSeated");

		kp_DrivingLevel.setValue(EDrivingLevel.L3_TrafficJamChauffer);
		kp_RoadStatus.setValue(ERoadStatus.COLLAPSED);
		kp_RoadType.setValue(ERoadType.HIGHWAY);
		kp_EngineHealth.setValue(true);
		kp_DriverFace.setValue(EFaceStatus.LOOKING_FORWARD);
		kp_DriverHands.setValue(true);
		kp_DriverSeated.setValue(true);
		
		// ADAPTATION RULES
 		IAdaptiveReadyComponent ads_L3_2_AdaptationRuleARC = BasicMAPEKLiteLoopHelper.deployAdaptationRule(new ADS_L3_2_AdaptationRule(bundleContext));
 		IAdaptiveReadyComponent ads_L3_3_AdaptationRuleARC = BasicMAPEKLiteLoopHelper.deployAdaptationRule(new ADS_L3_3_AdaptationRule(bundleContext));
 		IAdaptiveReadyComponent ads_L3_4_AdaptationRuleARC = BasicMAPEKLiteLoopHelper.deployAdaptationRule(new ADS_L3_4_AdaptationRule(bundleContext));
 		IAdaptiveReadyComponent ads_L3_5_AdaptationRuleARC = BasicMAPEKLiteLoopHelper.deployAdaptationRule(new ADS_L3_5_AdaptationRule(bundleContext));
 		IAdaptiveReadyComponent ads_2_AdaptationRuleARC = BasicMAPEKLiteLoopHelper.deployAdaptationRule(new ADS_2_AdaptationRule(bundleContext));

		// MONITORS
		IAdaptiveReadyComponent roadMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new RoadMonitor(bundleContext));
		IAdaptiveReadyComponent engineHealthMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new EngineHealthMonitor(bundleContext));
		IAdaptiveReadyComponent driverFaceMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new DriverFaceMonitor(bundleContext));
		IAdaptiveReadyComponent driverHandsMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new DriverHandsMonitor(bundleContext));
		IAdaptiveReadyComponent driverSeatedMonitorARC = BasicMAPEKLiteLoopHelper.deployMonitor(new DriverSeatedMonitor(bundleContext));

		// PROBES
		IAdaptiveReadyComponent roadProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new RoadProbe(bundleContext), roadMonitorARC);
		IAdaptiveReadyComponent engineHealthProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new EngineHealthProbe(bundleContext), engineHealthMonitorARC);
		IAdaptiveReadyComponent driverFaceProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new DriverFaceProbe(bundleContext), driverFaceMonitorARC);
		IAdaptiveReadyComponent driverHandsProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new DriverHandsProbe(bundleContext), driverHandsMonitorARC);
		IAdaptiveReadyComponent driverSeatedProbeARC = BasicMAPEKLiteLoopHelper.deployProbe(new DriverSeatedProbe(bundleContext), driverSeatedMonitorARC);

	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

	
	protected IRuleComponentsSystemConfiguration createInitialSystemConfiguration() {
		
		IRuleComponentsSystemConfiguration theInitialSystemConfiguration = SystemConfigurationHelper.createPartialSystemConfiguration("InitialConfiguration_" + ITimeStamped.getCurrentTimeStamp());
			
		//
		// ... adding and removing components examples ...
		// SystemConfigurationHelper.componentToAdd or SystemConfigurationHelper.componentToRemove
		//		systemconfiguration :  una IRuleComponentsSystemConfiguration donde se añadirán o eliminarán los componentes
		//		component-id		:  nombre del compopnente a añadir o quitar
		//		component-version	:  versión del componente
		
		// Ejemplo 1: Añadimos los componentes "device.RoadSensor" y "device.Engine", y eliminamos el componente "device.Steering" ...
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.RoadSensor", "1.0.0");
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "device.Engine", "1.0.0");
		SystemConfigurationHelper.componentToRemove(theInitialSystemConfiguration, "device.Steering", "1.0.0");
		
		// Ejemplo 2: ... y añadimos el servicio "driving.FallbackPlan.Emergency", que representa al fallback plan de emergencia
		SystemConfigurationHelper.componentToAdd(theInitialSystemConfiguration, "driving.FallbackPlan.Emergency", "1.0.0");
		
		
		
		//
		// ... adding and removing binding examples ...
		// SystemConfigurationHelper.bindingToAdd or SystemConfigurationHelper.bindingToRemove
		//		systemconfiguration   :  una IRuleComponentsSystemConfiguration donde se añadirán o eliminarán los componentes
		//		req-component-id	  :  nombre del componente que requiere la conexión
		//		req-component-version :  versión del componente que requiere la conexión
		//		req-component-interfaz:  interfaz requerida del componente
		//      prov-component-id	  :  nombre del componente que provee la conexión
		//		prov-component-version:  versión del componente que provee la conexión
		//		prov-component-interfaz:  interfaz proporcionada del componente
		
		// Ejemplo 3: Conectar el componente "driving.FallbackPlan.Emergency" (a través de su interfaz requerida "required_engine")
		//    con el componente "device.Engine" (a través de su interfaz proporcionada "provided_device")
		SystemConfigurationHelper.bindingToAdd(theInitialSystemConfiguration, 
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.REQUIRED_ENGINE,
				"device.Engine", "1.0.0", EngineARC.PROVIDED_DEVICE);

		// Ejemplo 4: Desconectar del componente "driving.FallbackPlan.Emergency" (en su interfaz requerida "required_steering")
		//    del componente "device.Steering" (a través de su interfaz proporcionada "provided_device")
		SystemConfigurationHelper.bindingToRemove(theInitialSystemConfiguration, 
				"driving.FallbackPlan.Emergency", "1.0.0", FallbackPlanARC.REQUIRED_STEERING,
				"device.Steering", "1.0.0", SteeringARC.PROVIDED_DEVICE);

		
		//
		// ... setting parameters examples ...
		// SystemConfigurationHelper.setParameter
		//		systemconfiguration   :  una IRuleComponentsSystemConfiguration donde se añadirán el set parameter
		//		component-id		  :  nombre del componente
		//		component-version	  :  versión del componente
		//		parameter-id		  :  nombre del parámetro
		//		parameter-value		  :  valor del parámetro
		
		// Ejemplo 5: Establecer el parámetro "referencespeed" a 100Km/h del servicio de conducción "driving.L3.HighwayChauffer"
		SystemConfigurationHelper.setParameter(theInitialSystemConfiguration, 
				"driving.L3.HighwayChauffer", "1.0.0", L3_DrivingServiceARC.PARAMETER_REFERENCESPEED, "100");
		// * El servicio de conducción "driving.L3.HighwayChauffer" puede no estar activo en este momento, y por tanto
		//   este 'set parameter' puede que no provoque ningún cambio de manera efectiva.
		//   Si quisiéramos que el servicio "driving.L3.HighwayChauffer" estuviera activo, deberíamos añadirlo como en el Ejemplo 2
		// ...

		return theInitialSystemConfiguration;
		
	}
}
