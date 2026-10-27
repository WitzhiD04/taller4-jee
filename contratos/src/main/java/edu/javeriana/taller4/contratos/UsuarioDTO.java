package edu.javeriana.taller4.contratos;

import java.io.Serializable;

/**
 * Usuario que ya inició sesión. Es lo que la lógica le devuelve a la presentación.
 * No lleva la clave: eso nunca sale de la capa de lógica.
 */
public class UsuarioDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String usuario;
    private String nombre;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String usuario, String nombre) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
