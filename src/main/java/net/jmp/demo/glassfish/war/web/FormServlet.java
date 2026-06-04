package net.jmp.demo.glassfish.war.web;

/*
 * (#)FormServlet.java  0.1.0   06/03/2026
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

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import net.jmp.demo.glassfish.war.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.jspecify.annotations.Nullable;

import static net.jmp.util.logging.LoggerUtils.*;

/// The form servlet class
@WebServlet(urlPatterns = "/servlet/form")
public class FormServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The messages resource bundle
    private final transient ResourceBundle bundle;

    /// The form JSP
    private static final String FORM_JSP = "/WEB-INF/jsp/form.jsp";

    /// The constructor
    ///
    /// @param  bundle  java.util.ResourceBundle
    @Inject
    public FormServlet(@Named("messages") final ResourceBundle bundle) {
        super();

        this.bundle = bundle;
    }

    /// The GET method. Called from /servlet/form.
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

        request.getRequestDispatcher(FORM_JSP).forward(request, response);

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

        final String name = StringUtils.trimToNull(request.getParameter("name"));
        final String email = StringUtils.trimToNull(request.getParameter("email"));
        final String comment = StringUtils.trimToNull(request.getParameter("comment"));

        request.setAttribute("name", name);
        request.setAttribute("email", email);
        request.setAttribute("comment", comment);

        final List<String> errors = this.validateInput(name, email, comment);

        if (errors.isEmpty()) {
            request.setAttribute("successMessage", this.bundle.getString("servlet.form.success"));
        } else {
            request.setAttribute("errors", errors);
        }

        request.getRequestDispatcher(FORM_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// The validate input method
    ///
    /// @param  name        java.lang.String
    /// @param  email       java.lang.String
    /// @param  comment     java.lang.String
    /// @return             java.util.List<java.lang.String>
    private List<String> validateInput(
            final @Nullable String name,
            final @Nullable String email,
            final @Nullable String comment) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(name, email, comment));
        }

        final List<String> errors = new ArrayList<>();

        if (name == null) {
            errors.add(this.bundle.getString("servlet.form.validation.required.name"));
        }

        if (email == null) {
            errors.add(this.bundle.getString("servlet.form.validation.required.email"));
        } else if (!StringUtils.looksLikeEmail(email)) {
            errors.add(this.bundle.getString("servlet.form.validation.email"));
        }

        if (comment == null) {
            errors.add(this.bundle.getString("servlet.form.validation.required.comment"));
        } else if (comment.length() < 10) {
            errors.add(this.bundle.getString("servlet.form.validation.comment"));
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(errors));
        }

        return errors;
    }
}
