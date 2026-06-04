package net.jmp.demo.glassfish.war.web;

/*
 * (#)RegistrationServlet.java  0.1.0   06/04/2026
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

import jakarta.inject.Inject;
import jakarta.inject.Named;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jspecify.annotations.Nullable;

import static net.jmp.util.logging.LoggerUtils.*;

/// The registration servlet class
@WebServlet(urlPatterns = "/servlet/register")
public class RegistrationServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The messages resource bundle
    private final transient ResourceBundle bundle;

    /// The registration form JSP
    private static final String REGISTER_JSP = "/WEB-INF/jsp/register.jsp";

    /// The registration successful JSP
    private static final String REGISTERED_JSP = "/WEB-INF/jsp/registered.jsp";

    /// The constructor
    ///
    /// @param  bundle  java.util.ResourceBundle
    @Inject
    public RegistrationServlet(@Named("messages") final ResourceBundle bundle) {
        super();

        this.bundle = bundle;
    }

    /// The GET method. Called from /servlet/register.
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

        request.getRequestDispatcher(REGISTER_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// The POST method
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             jakarta.servlet.ServletException When an error occurs
    /// @throws             java.io.IOException When an I/O error occurs
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (final UnsupportedEncodingException uee) {
            this.logger.error("Error setting character encoding to UTF-8", uee);
            throw new ServletException(uee);
        }

        final String email = trimToNull(request.getParameter("email"));

        request.setAttribute("email", email);

        final List<String> errors = this.validateInput(email);

        if (errors.isEmpty()) {
            final String pattern = this.bundle.getString("servlet.registration.success");
            final String successMessage = MessageFormat.format(pattern, email);

            request.setAttribute("successMessage", successMessage);
        } else {
            request.setAttribute("errors", errors);
        }

        request.getRequestDispatcher(REGISTERED_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// The validate input method
    ///
    /// @param  email   java.lang.String
    /// @return         java.util.List<java.lang.String>
    private List<String> validateInput(final @Nullable String email) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(email));
        }

        final List<String> errors = new ArrayList<>();

        if (email == null) {
            errors.add(this.bundle.getString("servlet.registration.validation.required.email"));
        } else if (!looksLikeEmail(email)) {
            errors.add(this.bundle.getString("servlet.registration.validation.email"));
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(errors));
        }

        return errors;
    }

    /// The trim to null method
    ///
    /// @param  s   java.lang.String
    /// @return     java.lang.String
    private static @Nullable String trimToNull(final @Nullable String s) {
        if (s == null) {
            return null;
        }

        final String t = s.trim();

        return t.isBlank() ? null : t;
    }

    /// The looks like email method
    ///
    /// @param  s   java.lang.String
    /// @return     boolean
    private static boolean looksLikeEmail(final String s) {
        return s.contains("@");
    }
}
