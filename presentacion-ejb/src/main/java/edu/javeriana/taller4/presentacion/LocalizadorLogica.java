package edu.javeriana.taller4.presentacion;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Service Locator del lado de la Presentación: encuentra los EJB remotos de la Lógica,
 * que viven en el WildFly de Lógica (LOGICA_URL). Tomcat no tiene EJB, por eso
 * el .war trae las librerías cliente de WildFly (wildfly-ejb-client-bom en el pom).
 */
public class LocalizadorLogica {

    private static final String MODULO = "logica";

    public static <T> T buscar(String nombreBean, Class<T> interfaz) throws NamingException {
        Properties propiedades = new Properties();
        propiedades.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        propiedades.put(Context.PROVIDER_URL, leer("LOGICA_URL", "remote+http://logica:8080"));
        propiedades.put(Context.SECURITY_PRINCIPAL, leer("LOGICA_USUARIO", "ejbuser"));
        propiedades.put(Context.SECURITY_CREDENTIALS, leer("LOGICA_CLAVE", "ejbclave123"));

        Context contexto = new InitialContext(propiedades);
        String nombreJndi = "ejb:/" + MODULO + "/" + nombreBean + "!" + interfaz.getName();
        return interfaz.cast(contexto.lookup(nombreJndi));
    }

    private static String leer(String variable, String porDefecto) {
        String valor = System.getenv(variable);
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        return valor;
    }
}
