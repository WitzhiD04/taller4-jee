package edu.javeriana.taller4.contratos;

import java.io.Serializable;

/**
 * Usuario tal como está guardado en la BD (incluye el hash de la clave).
 * Solo viaja entre Datos y Lógica; la Lógica lo convierte a UsuarioDTO.
 */
public class UsuarioDatos implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String usuario;
    private String nombre;
    private String claveHash;

    public UsuarioDatos() {
    }

    public UsuarioDatos(Long id, String usuario, String nombre, String claveHash) {
        this.id = id;
        this.usuario = usuario;
        this.nombre = nombre;
        this.claveHash = claveHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getClaveHash() { return claveHash; }
    public void setClaveHash(String claveHash) { this.claveHash = claveHash; }
}
