package edu.javeriana.taller4.contratos;

import java.io.Serializable;

/** Cuerpo JSON de POST /api/auth/login  ->  {"usuario": "...", "clave": "..."} */
public class LoginPeticion implements Serializable {

    private static final long serialVersionUID = 1L;

    private String usuario;
    private String clave;

    public LoginPeticion() {
    }

    public LoginPeticion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getClave() { return clave; }
    public void setClave(String clave) { this.clave = clave; }
}
