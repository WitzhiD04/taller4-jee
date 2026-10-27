package edu.javeriana.taller4.datos;

import edu.javeriana.taller4.contratos.UsuarioDatos;
import edu.javeriana.taller4.contratos.UsuarioDatosRemote;
import jakarta.ejb.Remote;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;

/**
 * EJB de la capa de Datos. Es @Remote: lo llama la Lógica, que corre en OTRO servidor.
 * Solo hace acceso a datos, nada de reglas de negocio.
 */
@Stateless
@Remote(UsuarioDatosRemote.class)
public class UsuarioDatosBean implements UsuarioDatosRemote {

    @PersistenceContext(unitName = "taller4PU")
    private EntityManager em;

    @Override
    public UsuarioDatos buscarPorUsuario(String usuario) {
        TypedQuery<Usuario> consulta = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.usuario = :usuario", Usuario.class);
        consulta.setParameter("usuario", usuario);
        List<Usuario> resultado = consulta.getResultList();

        if (resultado.isEmpty()) {
            return null;
        }
        Usuario u = resultado.get(0);
        return new UsuarioDatos(u.getId(), u.getUsuario(), u.getNombre(), u.getClaveHash());
    }
}
