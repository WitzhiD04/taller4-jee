package edu.javeriana.taller4.datos;

import edu.javeriana.taller4.contratos.ProductoDTO;
import edu.javeriana.taller4.contratos.ProductoDatosRemote;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/** EJB remoto de la capa de Datos para productos. */
@Stateless
@Remote(ProductoDatosRemote.class)
public class ProductoDatosBean implements ProductoDatosRemote {

    @PersistenceContext(unitName = "taller4PU")
    private EntityManager em;

    @Override
    public List<ProductoDTO> listar() {
        List<Producto> entidades = em.createQuery(
                "SELECT p FROM Producto p ORDER BY p.id", Producto.class).getResultList();

        // Las entidades JPA no salen del servidor: se copian a DTOs.
        List<ProductoDTO> resultado = new ArrayList<ProductoDTO>();
        for (Producto p : entidades) {
            resultado.add(new ProductoDTO(p.getId(), p.getNombre(), p.getPrecio()));
        }
        return resultado;
    }

    @Override
    public ProductoDTO guardar(ProductoDTO producto) {
        Producto entidad = new Producto();
        entidad.setNombre(producto.getNombre());
        entidad.setPrecio(producto.getPrecio());
        em.persist(entidad);
        em.flush(); // para que Postgres asigne el id ya mismo

        return new ProductoDTO(entidad.getId(), entidad.getNombre(), entidad.getPrecio());
    }
}
