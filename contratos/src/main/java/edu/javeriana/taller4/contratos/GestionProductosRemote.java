package edu.javeriana.taller4.contratos;

import java.util.List;

/** Lo que la capa de Lógica ofrece sobre productos. */
public interface GestionProductosRemote {

    List<ProductoDTO> listar();

    ProductoDTO crear(String nombre, double precio) throws DatoInvalidoException;
}
