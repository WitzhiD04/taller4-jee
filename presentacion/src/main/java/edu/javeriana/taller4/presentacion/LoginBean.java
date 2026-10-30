package edu.javeriana.taller4.presentacion;

import edu.javeriana.taller4.contratos.CredencialesInvalidasException;
import edu.javeriana.taller4.contratos.UsuarioDTO;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import java.io.Serializable;

/** Backing bean de login.xhtml. Vive mientras dure la sesión del navegador. */
@Named("login")
@SessionScoped
public class LoginBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private String clave;
    private UsuarioDTO usuarioActual;

    public String ingresar() {
        FacesContext faces = FacesContext.getCurrentInstance();
        try {
            usuarioActual = ClienteServicios.login(usuario, clave);
            clave = null;

            // Nuevo id de sesión al iniciar sesión, para que no sirva un id que alguien conociera de antes
            ((HttpServletRequest) faces.getExternalContext().getRequest()).changeSessionId();

            // El filtro FiltroSesion revisa este atributo para dejar entrar a /app/*
            faces.getExternalContext().getSessionMap().put(FiltroSesion.ATRIBUTO_USUARIO, usuarioActual);
            return "/app/productos?faces-redirect=true";

        } catch (CredencialesInvalidasException e) {
            faces.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        } catch (RuntimeException e) {
            faces.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo conectar con la capa de Servicios", null));
            return null;
        }
    }

    public String salir() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/login?faces-redirect=true";
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }

    public UsuarioDTO getUsuarioActual() { return usuarioActual; }
}
