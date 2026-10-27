# Servidor de DATOS: WildFly + driver de Postgres + datasource + datos.war
FROM quay.io/wildfly/wildfly:38.0.1.Final-jdk21

# 1. Driver JDBC de Postgres y script que crea el datasource
ADD --chown=jboss:jboss https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.8/postgresql-42.7.8.jar /tmp/postgresql.jar
COPY --chown=jboss:jboss configurar-datasource.cli /tmp/configurar-datasource.cli
RUN /opt/jboss/wildfly/bin/jboss-cli.sh --file=/tmp/configurar-datasource.cli \
 && rm -rf /opt/jboss/wildfly/standalone/configuration/standalone_xml_history

# 2. Usuario de aplicación: la Lógica lo usa para poder llamar los EJB remotos de este servidor
RUN /opt/jboss/wildfly/bin/add-user.sh -a -u ejbuser -p ejbclave123 --silent

# 3. La aplicación
COPY target/datos.war /opt/jboss/wildfly/standalone/deployments/
