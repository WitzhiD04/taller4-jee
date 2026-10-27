package edu.javeriana.taller4.contratos;

import java.util.List;

/** Lo que la capa de Datos ofrece sobre productos. */
public interface ProductoDatosRemote {

    List<ProductoDTO> listar();

    ProductoDTO guardar(ProductoDTO producto);
}
