package net.jmp.demo.glassfish.war.web;

/*
 * (#)ErrorServlet.java 0.1.0   06/06/2026
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

import java.util.ResourceBundle;

/// The error servlet class. Used to drive the 500 error JSP.
@WebServlet(urlPatterns = "/servlet/error")
public class ErrorServlet extends HttpServlet {
    /// The messages resource bundle
    private final transient ResourceBundle bundle;

    /// The constructor
    ///
    /// @param  bundle  java.util.ResourceBundle
    @Inject
    public ErrorServlet(@Named("messages") final ResourceBundle bundle) {
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
        throw new ServletException(this.bundle.getString("servlet.error.message"));
    }
}
