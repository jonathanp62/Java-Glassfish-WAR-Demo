package net.jmp.demo.glassfish.war.web;

/*
 * (#)InitParamXmlServlet.java  0.1.0   06/11/2026
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

import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The init parameters servlet class
public class InitParamXmlServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The init-param JSP
    private static final String INIT_PARAM_JSP = "/WEB-INF/jsp/init-param.jsp";

    /// The name
    private @Nullable String name;

    /// The email
    private @Nullable String email;

    /// The default constructor
    public InitParamXmlServlet() {
        super();
    }

    /// The init method
    @Override
    public void init() throws ServletException {
        this.name = getInitParameter("name");
        this.email = getInitParameter("email");
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

        if (name != null) {
            request.setAttribute("name",  this.name);
        }

        if (email != null) {
            request.setAttribute("email", this.email);
        }

        request.getRequestDispatcher(INIT_PARAM_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
