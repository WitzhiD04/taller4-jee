# Servidor de LÓGICA: WildFly + logica.war
FROM quay.io/wildfly/wildfly:38.0.1.Final-jdk21

# Usuario de aplicación: lo usa quien llama los EJB remotos de este servidor
# (la Presentación en el escenario 1, Servicios en los escenarios 2 y 3)
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbuser -p ejbclave123 --silent

COPY target/logica.war /opt/jboss/wildfly/standalone/deployments/
