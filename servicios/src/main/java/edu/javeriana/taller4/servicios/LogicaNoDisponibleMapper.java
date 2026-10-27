package edu.javeriana.taller4.servicios;

import edu.javeriana.taller4.contratos.ErrorRespuesta;
import jakarta.ejb.EJBException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Si el servidor de Lógica (o el de Datos) está caído, la llamada al EJB remoto lanza
 * una EJBException. Sin esta clase el cliente recibiría un 500 con un texto interno de WildFly;
 * con ella recibe 503 y el mismo JSON de error que usan los demás casos:  {"mensaje": "..."}
 */
@Provider
public class LogicaNoDisponibleMapper implements ExceptionMapper<EJBException> {

    @Override
    public Response toResponse(EJBException e) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)                  // 503
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorRespuesta("La capa de Lógica no está disponible"))
                .build();
    }
}
