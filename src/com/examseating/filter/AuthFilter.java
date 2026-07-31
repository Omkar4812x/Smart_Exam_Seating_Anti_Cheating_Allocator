package com.examseating.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * AuthFilter - Servlet filter that protects all /admin/* routes.
 * 
 * Checks for a valid "adminUser" attribute in the HTTP session.
 * If not found, redirects to the login page.
 * 
 * Configured in web.xml:
 *   <filter-mapping>
 *     <url-pattern>/admin/*</url-pattern>
 *   </filter-mapping>
 */
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        HttpSession session = httpRequest.getSession(false);
        
        boolean isLoggedIn = (session != null && session.getAttribute("adminUser") != null);
        
        if (isLoggedIn) {
            // User is authenticated — allow the request to proceed
            chain.doFilter(request, response);
        } else {
            // Not logged in — redirect to login page
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // No cleanup needed
    }
}
