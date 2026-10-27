package edu.javeriana.taller4.servicios;

import edu.javeriana.taller4.contratos.AutenticacionRemote;
import edu.javeriana.taller4.contratos.CredencialesInvalidasException;
import edu.javeriana.taller4.contratos.ErrorRespuesta;
import edu.javeriana.taller4.contratos.LoginPeticion;
import edu.javeriana.taller4.contratos.UsuarioDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import javax.naming.NamingException;

/**
 * POST /api/auth/login
 * Capa de Servicios: no tiene reglas de negocio. Traduce HTTP/JSON a una llamada
 * al EJB remoto de la Lógica, y la respuesta (o el error) otra vez a HTTP/JSON.
 */
@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AutenticacionResource {

    @POST
    @Path("login")
    public Response login(LoginPeticion peticion) throws NamingException {
        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorRespuesta("Falta el cuerpo {usuario, clave}")).build();
        }
        AutenticacionRemote logica = LocalizadorLogica.buscar("AutenticacionBean", AutenticacionRemote.class);
        try {
            UsuarioDTO usuario = logica.login(peticion.getUsuario(), peticion.getClave());
            return Response.ok(usuario).build();                                   // 200
        } catch (CredencialesInvalidasException e) {
            return Response.status(Response.Status.UNAUTHORIZED)                    // 401
                    .entity(new ErrorRespuesta(e.getMessage())).build();
        }
    }
}
