package edu.javeriana.taller4.presentacion;

import edu.javeriana.taller4.contratos.CredencialesInvalidasException;
import edu.javeriana.taller4.contratos.DatoInvalidoException;
import edu.javeriana.taller4.contratos.ErrorRespuesta;
import edu.javeriana.taller4.contratos.LoginPeticion;
import edu.javeriana.taller4.contratos.ProductoDTO;
import edu.javeriana.taller4.contratos.UsuarioDTO;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Cliente HTTP de la capa de Servicios. En este escenario la Presentación ya NO conoce
 * los EJB de la Lógica: solo sabe que hay una API REST en SERVICIOS_URL.
 * (Es el mismo rol que tenía LocalizadorLogica en el escenario 1, pero por HTTP/JSON.)
 */
public class ClienteServicios {

    // Tiempo máximo de espera: si Servicios no responde, la página muestra el error en vez de quedarse cargando
    private static final Client CLIENTE = ClientBuilder.newBuilder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    public static UsuarioDTO login(String usuario, String clave) throws CredencialesInvalidasException {
        Response respuesta = api().path("auth/login")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new LoginPeticion(usuario, clave)));
        try {
            if (respuesta.getStatus() == 200) {
                return respuesta.readEntity(UsuarioDTO.class);
            }
            if (respuesta.getStatus() == 401 || respuesta.getStatus() == 400) {
                throw new CredencialesInvalidasException(mensajeDeError(respuesta));
            }
            // cualquier otro código (ej. 503 = Lógica caída) no es culpa del usuario
            throw new IllegalStateException("Servicios respondió " + respuesta.getStatus());
        } finally {
            respuesta.close();
        }
    }

    public static List<ProductoDTO> listarProductos() {
        ProductoDTO[] productos = api().path("productos")
                .request(MediaType.APPLICATION_JSON)
                .get(ProductoDTO[].class);
        return Arrays.asList(productos);
    }

    public static ProductoDTO crearProducto(String nombre, double precio) throws DatoInvalidoException {
        Response respuesta = api().path("productos")
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(new ProductoDTO(null, nombre, precio)));
        try {
            if (respuesta.getStatus() == 201) {
                return respuesta.readEntity(ProductoDTO.class);
            }
            if (respuesta.getStatus() == 400) {
                throw new DatoInvalidoException(mensajeDeError(respuesta));
            }
            throw new IllegalStateException("Servicios respondió " + respuesta.getStatus());
        } finally {
            respuesta.close();
        }
    }

    private static String mensajeDeError(Response respuesta) {
        return respuesta.readEntity(ErrorRespuesta.class).getMensaje();
    }

    private static WebTarget api() {
        String url = System.getenv("SERVICIOS_URL");
        if (url == null || url.isBlank()) {
            url = "http://servicios:8080/servicios/api";
        }
        return CLIENTE.target(url);
    }
}
