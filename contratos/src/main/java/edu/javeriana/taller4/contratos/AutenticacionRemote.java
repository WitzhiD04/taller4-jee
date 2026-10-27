package edu.javeriana.taller4.contratos;

/** Lo que la capa de Lógica ofrece para el login. */
public interface AutenticacionRemote {

    UsuarioDTO login(String usuario, String clave) throws CredencialesInvalidasException;
}
