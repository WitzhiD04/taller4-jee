package edu.javeriana.taller4.contratos;

/** Lo que la capa de Datos ofrece sobre usuarios. */
public interface UsuarioDatosRemote {

    /** Devuelve el usuario o null si no existe. */
    UsuarioDatos buscarPorUsuario(String usuario);
}
