package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestInitParamServlet.java 0.1.0   06/11/2026
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

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// The test class for the InitParamServlet class
@ExtendWith(MockitoExtension.class)
class TestInitParamServlet {
    /// The init-param JSP
    private static final String INIT_PARAM_JSP = "/WEB-INF/jsp/init-param.jsp";

    @Test
    void testDoGetSetsNameAndEmailAttributesFromInitParamsAndForwardsToJsp() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final ServletConfig servletConfig = mock(ServletConfig.class);

        lenient().when(servletConfig.getInitParameter("name")).thenReturn("Jonathan");
        lenient().when(servletConfig.getInitParameter("email")).thenReturn("jonathan@example.com");

        when(request.getRequestDispatcher(INIT_PARAM_JSP)).thenReturn(dispatcher);

        final InitParamServlet servlet = new InitParamServlet();

        servlet.init(servletConfig);
        servlet.doGet(request, response);

        verify(request).setAttribute("name", "Jonathan");
        verify(request).setAttribute("email", "jonathan@example.com");
        verify(request).getRequestDispatcher(INIT_PARAM_JSP);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGetSetsNullNameWhenInitParamIsMissing() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final ServletConfig servletConfig = mock(ServletConfig.class);

        when(request.getRequestDispatcher(INIT_PARAM_JSP)).thenReturn(dispatcher);

        final InitParamServlet servlet = new InitParamServlet();

        servlet.init(servletConfig);
        servlet.doGet(request, response);

        verify(request).setAttribute("name", null);
        verify(request).setAttribute("email", null);
        verify(dispatcher).forward(request, response);
    }
}
