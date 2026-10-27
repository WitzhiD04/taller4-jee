package edu.javeriana.taller4.logica;

import edu.javeriana.taller4.contratos.DatoInvalidoException;
import edu.javeriana.taller4.contratos.GestionProductosRemote;
import edu.javeriana.taller4.contratos.ProductoDTO;
import edu.javeriana.taller4.contratos.ProductoDatosRemote;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import java.util.List;
import javax.naming.NamingException;

/** Reglas de negocio de productos. Valida y le delega el guardado a la capa de Datos. */
@Stateless
@Remote(GestionProductosRemote.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class GestionProductosBean implements GestionProductosRemote {

    @Override
    public List<ProductoDTO> listar() {
        return productos().listar();
    }

    @Override
    public ProductoDTO crear(String nombre, double precio) throws DatoInvalidoException {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre es obligatorio");
        }
        if (nombre.trim().length() > 100) { // la columna en la BD es VARCHAR(100)
            throw new DatoInvalidoException("El nombre admite máximo 100 caracteres");
        }
        // NaN ("no es un número") e Infinity pasan el "precio <= 0", por eso se revisan aparte
        if (Double.isNaN(precio) || Double.isInfinite(precio) || precio <= 0) {
            throw new DatoInvalidoException("El precio debe ser mayor que cero");
        }
        ProductoDTO nuevo = new ProductoDTO(null, nombre.trim(), precio);
        return productos().guardar(nuevo);
    }

    private ProductoDatosRemote productos() {
        try {
            return LocalizadorDatos.buscar("ProductoDatosBean", ProductoDatosRemote.class);
        } catch (NamingException e) {
            throw new IllegalStateException("No se pudo conectar con la capa de Datos", e);
        }
    }
}
