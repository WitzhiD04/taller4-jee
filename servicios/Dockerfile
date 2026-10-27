# Servidor de SERVICIOS: WildFly + servicios.war (REST y SOAP). Es cliente de la Lógica por EJB remoto.
FROM quay.io/wildfly/wildfly:38.0.1.Final-jdk21

# El WSDL trae la dirección (soap:address) a la que el cliente SOAP debe llamar.
# Por defecto WildFly pone la IP interna del contenedor, que no sirve fuera de Docker.
# Con "jbossws.undefined.host" usa el host y puerto con que se pidió el WSDL (ej: localhost:8081).
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --commands="embed-server,/subsystem=webservices:write-attribute(name=wsdl-host,value=jbossws.undefined.host),stop-embedded-server" \
 && rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

COPY target/servicios.war /opt/jboss/wildfly/standalone/deployments/
