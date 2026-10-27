package edu.javeriana.taller4.contratos;

/** Usuario o clave incorrectos. */
public class CredencialesInvalidasException extends Exception {

    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException(String mensaje) {
        super(mensaje);
    }
}
