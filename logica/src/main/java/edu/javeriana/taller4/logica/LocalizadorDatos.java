package edu.javeriana.taller4.logica;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Service Locator: encuentra los EJB remotos de la capa de Datos, que viven en OTRO WildFly.
 *
 * Arma un InitialContext que apunta al servidor de Datos (DATOS_URL) y pide el EJB por su
 * nombre JNDI:  ejb:/<modulo>/<Bean>!<interfaz>
 * El módulo es el nombre del .war desplegado en ese servidor (datos.war -> "datos").
 */
public class LocalizadorDatos {

    private static final String MODULO = "datos";

    public static <T> T buscar(String nombreBean, Class<T> interfaz) throws NamingException {
        Properties propiedades = new Properties();
        propiedades.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");
        propiedades.put(Context.PROVIDER_URL, leer("DATOS_URL", "remote+http://datos:8080"));
        propiedades.put(Context.SECURITY_PRINCIPAL, leer("DATOS_USUARIO", "ejbuser"));
        propiedades.put(Context.SECURITY_CREDENTIALS, leer("DATOS_CLAVE", "ejbclave123"));

        Context contexto = new InitialContext(propiedades);
        String nombreJndi = "ejb:/" + MODULO + "/" + nombreBean + "!" + interfaz.getName();
        return interfaz.cast(contexto.lookup(nombreJndi));
    }

    /** Lee una variable de entorno (las pone el docker-compose); si no existe usa el valor por defecto. */
    private static String leer(String variable, String porDefecto) {
        String valor = System.getenv(variable);
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        return valor;
    }
}
