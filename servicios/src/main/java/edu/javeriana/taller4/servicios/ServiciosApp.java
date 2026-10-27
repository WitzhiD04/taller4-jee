package edu.javeriana.taller4.servicios;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/** Todos los servicios REST quedan bajo  http://servidor:8080/servicios/api/... */
@ApplicationPath("api")
public class ServiciosApp extends Application {
}
