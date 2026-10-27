package edu.javeriana.taller4.contratos;

/** Un dato que llega a la lógica no cumple las reglas (nombre vacío, precio negativo...). */
public class DatoInvalidoException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
