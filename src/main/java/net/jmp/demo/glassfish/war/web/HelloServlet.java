package net.jmp.demo.glassfish.war.web;

/*
 * (#)HelloServlet.java 0.1.0   06/02/2026
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
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;

import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The hello servlet class
@WebServlet(urlPatterns = "/servlet/hello")
public class HelloServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The messages resource bundle
    private final transient ResourceBundle bundle;

    /// The constructor
    ///
    /// @param  bundle  java.util.ResourceBundle
    @Inject
    public HelloServlet(@Named("messages") final ResourceBundle bundle) {
        super();

        this.bundle = bundle;
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

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html");

        final String rawName = request.getParameter("name");
        final String name = this.escapeHtml(rawName == null || rawName.isBlank() ? "World" : rawName);

        try (final PrintWriter out = response.getWriter()) {
            out.append("<!doctype html>")
                    .append("<html lang=\"en\">")
                    .append("<head>")
                    .append("<meta charset=\"UTF-8\">")
                    .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">")
                    .append("<title>")
                    .append(this.bundle.getString("servlet.title"))
                    .append("</title>")
                    .append("</head>")
                    .append("<body>")
                    .append("<h1>")
                    .append(this.bundle.getString("hello"))
                    .append(", ")
                    .append(name)
                    .append("!</h1>")
                    .append("<p>")
                    .append(this.bundle.getString("try"))
                    .append(": <code>?name=Jonathan</code></p>")
                    .append("</body>")
                    .append("</html>");
        } catch (final RuntimeException re) {
            this.logger.error("Error writing response", re);

            throw new ServletException(re);
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// The escape HTML method
    ///
    /// @param  s   java.lang.String
    /// @return     java.lang.String
    private String escapeHtml(final String s) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(s));
        }

        final String result = s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(result));
        }

        return result;
    }
}
