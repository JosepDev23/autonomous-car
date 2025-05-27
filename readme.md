# SUA AUTONOMOUS CAR

### Josep Ramon Ribera, Javier Ruano Guallart, Samuel Cantó Ortuño, Belén Grande López

## Requisitos de adaptación implementados

Para el trabajo hemos optado por una cantidad de requisitos necesaria para aspirar al 10 en la asignatura.

Hemos implementado **ADS_L3-2**, **ADS_L3-3**, **ADS_L3-4**, **ADS_L3-5**, **ADS-2** y **INTERACT-1**.

Como nuestra especificación teórica era mejorable, no la hemos seguido para el trabajo práctico. Hemos rediseñado los monitores, sondas y reglas de adaptación para implementar de una manera más correcta el proyecto.

Nuestro principal fallo en la especificación fue que la lógica de las adaptaciones recayó en los monitores, no en las reglas de adaptación. Por lo que en esta implementación lo hemos cambiado.

### Requisitos **ADS_L3-2**, **ADS_L3-3**, **ADS_L3-4** y **ADS_L3-5**

Para estos requisitos hemos utilizado una única sonda, llamada RoadProbe. Se encarga de leer los valores del RoadSensor que indican el estado de la carretera y el tipo de la misma. Esta sonda notifica de estos valores al RoadMonitor.

El RoadMonitor recibe los valores y se encarga de actualizarlos en el Knowledge, para que así las reglas de adaptación realicen las pertinentes adaptaciones.

Hay una regla de adaptación por cada una de los 4 requisitos. Y para facilitar la comprensión del código, hemos nombrado las reglas como los propios requisitos de adaptación.

Cada regla, en base al anterior nivel de conducción activo y a las propiedades de la carretera, actualiza el knowledge del nivel de conducción que debería estar activo y realiza las adaptaciones necesarias para que se active el nivel de conducción.

### Requisito **ADS-2**

Para este requisito hemos implementado una sonda llamada EngineHealthProbe. Se encarga de monitorizar si el Engine está activo o no. Esta información se la notifica el EngineHealthMonitor.

El monitor recibe el valor booleano que indica si el motor está activo o no, y lo guarda en el knowledge.

Con la información del valor booleano del knowledge, la regla de adaptación creada explícitamente para este requisito determina que si el motor no está activo, se trata de un fallo sistémico general y activa el nivel de conducción L0.

Se podrían monitorear otros componentes para determinar si hay un fallo sistémico, no obstante, para cumplir con el requisito para este trabajo hemos considerado que con el motor es suficiente.

### Requisito **Interact-1**

Para este requisito hemos implementado 3 sondas. DriverFaceProbe, DriverHandsProbe y DriverSeatedProbe. Cada una comprueba el estado respectivo de la atención del conductor, la posición de las manos respecto del volante del conductor, y de la ocupación del conductor respecto del asiento del mismo. Estas 3 sondas notifican a 3 distintos monitores con los mismos nombres.

Los 3 monitores reciben el valor de las sondas y actualizan el knowledge con la información que reciben

Luego, se han creado 3 reglas de adaptación, una por cada condición del Interact-1. Estas 3 reglas utilizan el knowledge proviniente de estos 3 monitores para ejecutar las adaptaciones. También es necesario que en el knowledge esté indicado que estamos en cualquier nivel de conducción L3.
