package edu.javeriana.taller4.servicios;

import edu.javeriana.taller4.contratos.AutenticacionRemote;
import edu.javeriana.taller4.contratos.CredencialesInvalidasException;
import edu.javeriana.taller4.contratos.DatoInvalidoException;
import edu.javeriana.taller4.contratos.GestionProductosRemote;
import edu.javeriana.taller4.contratos.ProductoDTO;
import edu.javeriana.taller4.contratos.UsuarioDTO;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;
import javax.naming.NamingException;

/**
 * Los MISMOS servicios que AutenticacionResource y ProductoResource, pero por SOAP.
 * WildFly genera el WSDL solo:  http://servidor:8080/servicios/ProductosSoap?wsdl
 *
 * Idea del escenario 3: una sola Lógica y varias "puertas" (REST, SOAP, web).
 * Esta clase tampoco tiene reglas de negocio: solo le pasa la llamada a la Lógica.
 */
@WebService(serviceName = "ProductosSoap", targetNamespace = "http://servicios.taller4.javeriana.edu/")
public class ProductosSoapService {

    @WebMethod
    public UsuarioDTO login(@WebParam(name = "usuario") String usuario,
                            @WebParam(name = "clave") String clave) throws CredencialesInvalidasException {
        return autenticacion().login(usuario, clave);
    }

    @WebMethod
    public List<ProductoDTO> listarProductos() {
        return productos().listar();
    }

    @WebMethod
    public ProductoDTO crearProducto(@WebParam(name = "nombre") String nombre,
                                     @WebParam(name = "precio") double precio) throws DatoInvalidoException {
        return productos().crear(nombre, precio);
    }

    private AutenticacionRemote autenticacion() {
        try {
            return LocalizadorLogica.buscar("AutenticacionBean", AutenticacionRemote.class);
        } catch (NamingException e) {
            throw new IllegalStateException("No se pudo conectar con la capa de Lógica", e);
        }
    }

    private GestionProductosRemote productos() {
        try {
            return LocalizadorLogica.buscar("GestionProductosBean", GestionProductosRemote.class);
        } catch (NamingException e) {
            throw new IllegalStateException("No se pudo conectar con la capa de Lógica", e);
        }
    }
}
