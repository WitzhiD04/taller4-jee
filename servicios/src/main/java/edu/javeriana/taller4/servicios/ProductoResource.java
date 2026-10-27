package edu.javeriana.taller4.servicios;

import edu.javeriana.taller4.contratos.DatoInvalidoException;
import edu.javeriana.taller4.contratos.ErrorRespuesta;
import edu.javeriana.taller4.contratos.GestionProductosRemote;
import edu.javeriana.taller4.contratos.ProductoDTO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import javax.naming.NamingException;

/** GET y POST /api/productos  ->  le delega todo a la Lógica. */
@Path("productos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductoResource {

    @GET
    public List<ProductoDTO> listar() throws NamingException {
        return logica().listar();
    }

    @POST
    public Response crear(ProductoDTO producto) throws NamingException {
        if (producto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorRespuesta("Falta el cuerpo {nombre, precio}")).build();
        }
        try {
            ProductoDTO creado = logica().crear(producto.getNombre(), producto.getPrecio());
            return Response.status(Response.Status.CREATED).entity(creado).build();   // 201
        } catch (DatoInvalidoException e) {
            return Response.status(Response.Status.BAD_REQUEST)                       // 400
                    .entity(new ErrorRespuesta(e.getMessage())).build();
        }
    }

    private GestionProductosRemote logica() throws NamingException {
        return LocalizadorLogica.buscar("GestionProductosBean", GestionProductosRemote.class);
    }
}
