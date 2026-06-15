package net.jmp.demo.glassfish.war.web;

/*
 * (#)InitParamServlet.java 0.2.0   06/15/2026
 * (#)InitParamServlet.java 0.1.0   06/10/2026
 *
 * @author   Jonathan Parker
 *
 * MIT License
 *
 * Copyright (c) 2026 Jonathan M. Parker
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import jakarta.annotation.security.DeclareRoles;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The init parameters servlet class
///
/// Found a way to test the security configuration
/// Using Safari, open a private window and navigate to a servlet
/// Once the authentication has been made, close the private window,
/// open a new one, then navigate to the same or a different servlet
///
@WebServlet(
    urlPatterns = "/servlet/init-param",
    initParams = {
        @WebInitParam(name = "name",  value = "Jonathan"),
        @WebInitParam(name = "email", value = "jonathan@example.com")
    }
)
@DeclareRoles("user")
@ServletSecurity(@HttpConstraint(rolesAllowed = "user"))
public class InitParamServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The init-param JSP
    private static final String INIT_PARAM_JSP = "/WEB-INF/jsp/init-param.jsp";

    /// The default constructor
    public InitParamServlet() {
        super();
    }

    /// The GET method
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             jakarta.servlet.ServletException When an error occurs
    /// @throws             java.io.IOException When an I/O error occurs
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        request.setAttribute("name",  this.getServletConfig().getInitParameter("name"));
        request.setAttribute("email", this.getServletConfig().getInitParameter("email"));

        request.getRequestDispatcher(INIT_PARAM_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
