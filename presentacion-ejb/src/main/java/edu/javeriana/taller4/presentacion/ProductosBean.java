package edu.javeriana.taller4.presentacion;

import edu.javeriana.taller4.contratos.DatoInvalidoException;
import edu.javeriana.taller4.contratos.GestionProductosRemote;
import edu.javeriana.taller4.contratos.ProductoDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.util.ArrayList;
import java.util.List;
import javax.naming.NamingException;

/** Backing bean de app/productos.xhtml: lista y crea productos llamando a la Lógica. */
@Named("productos")
@RequestScoped
public class ProductosBean {

    private String nombre;
    private Double precio;
    private List<ProductoDTO> lista;

    /**
     * Lo llama f:viewAction en productos.xhtml ANTES de dibujar la página.
     * Así, si la Lógica está caída, el mensaje de error alcanza a salir en h:messages.
     */
    public void cargar() {
        getLista();
    }

    public List<ProductoDTO> getLista() {
        if (lista == null) {
            try {
                lista = logica().listar();
            } catch (NamingException | RuntimeException e) {
                mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo conectar con la capa de Lógica");
                lista = new ArrayList<ProductoDTO>();
            }
        }
        return lista;
    }

    public String crear() {
        try {
            double valor = 0;
            if (precio != null) {
                valor = precio;
            }
            logica().crear(nombre, valor);
            mensaje(FacesMessage.SEVERITY_INFO, "Producto creado");
            nombre = null;
            precio = null;
            lista = null; // para que se vuelva a consultar
        } catch (DatoInvalidoException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, e.getMessage());
        } catch (NamingException | RuntimeException e) {
            mensaje(FacesMessage.SEVERITY_ERROR, "No se pudo conectar con la capa de Lógica");
        }
        return null; // quedarse en la misma página
    }

    private GestionProductosRemote logica() throws NamingException {
        return LocalizadorLogica.buscar("GestionProductosBean", GestionProductosRemote.class);
    }

    private void mensaje(FacesMessage.Severity tipo, String texto) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(tipo, texto, null));
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}
