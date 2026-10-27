package edu.javeriana.taller4.logica;

import edu.javeriana.taller4.contratos.AutenticacionRemote;
import edu.javeriana.taller4.contratos.CredencialesInvalidasException;
import edu.javeriana.taller4.contratos.UsuarioDTO;
import edu.javeriana.taller4.contratos.UsuarioDatos;
import edu.javeriana.taller4.contratos.UsuarioDatosRemote;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import javax.naming.NamingException;

/**
 * Regla del login: el usuario existe y el hash de la clave coincide.
 * Es @Remote porque lo llama la Presentación desde OTRO servidor (Tomcat).
 */
@Stateless
@Remote(AutenticacionRemote.class)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED) // la transacción la maneja Datos, no la Lógica
public class AutenticacionBean implements AutenticacionRemote {

    @Override
    public UsuarioDTO login(String usuario, String clave) throws CredencialesInvalidasException {
        if (usuario == null || usuario.isBlank() || clave == null || clave.isBlank()) {
            throw new CredencialesInvalidasException("Usuario y clave son obligatorios");
        }

        UsuarioDatos encontrado = usuarios().buscarPorUsuario(usuario.trim());

        if (encontrado == null || !encontrado.getClaveHash().equals(Cifrado.sha256(clave))) {
            // mismo mensaje en los dos casos: no le decimos al atacante cuál de los dos falló
            throw new CredencialesInvalidasException("Usuario o clave incorrectos");
        }

        return new UsuarioDTO(encontrado.getId(), encontrado.getUsuario(), encontrado.getNombre());
    }

    private UsuarioDatosRemote usuarios() {
        try {
            return LocalizadorDatos.buscar("UsuarioDatosBean", UsuarioDatosRemote.class);
        } catch (NamingException e) {
            throw new IllegalStateException("No se pudo conectar con la capa de Datos", e);
        }
    }
}
