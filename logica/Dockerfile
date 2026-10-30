# Servidor de LÓGICA: WildFly + logica.war
FROM quay.io/wildfly/wildfly:38.0.1.Final-jdk21

# Usuario de aplicación: Servicios lo usa para llamar los EJB remotos de este servidor
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbuser -p ejbclave123 --silent

COPY target/logica.war /opt/jboss/wildfly/standalone/deployments/
