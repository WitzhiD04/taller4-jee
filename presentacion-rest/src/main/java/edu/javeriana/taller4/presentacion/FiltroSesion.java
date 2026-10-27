package edu.javeriana.taller4.presentacion;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Todo lo que está bajo /app/ exige haber hecho login; si no, manda a login.xhtml. */
@WebFilter("/app/*")
public class FiltroSesion implements Filter {

    public static final String ATRIBUTO_USUARIO = "usuario";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain cadena)
            throws IOException, ServletException {
        HttpServletRequest peticion = (HttpServletRequest) req;
        HttpServletResponse respuesta = (HttpServletResponse) resp;

        HttpSession sesion = peticion.getSession(false);
        boolean autenticado = sesion != null && sesion.getAttribute(ATRIBUTO_USUARIO) != null;

        if (autenticado) {
            cadena.doFilter(req, resp);
        } else {
            respuesta.sendRedirect(peticion.getContextPath() + "/login.xhtml");
        }
    }
}
