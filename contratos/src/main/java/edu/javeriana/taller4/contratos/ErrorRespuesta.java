package edu.javeriana.taller4.contratos;

import java.io.Serializable;

/** JSON que devuelven los servicios cuando algo falla  ->  {"mensaje": "..."} */
public class ErrorRespuesta implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mensaje;

    public ErrorRespuesta() {
    }

    public ErrorRespuesta(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
