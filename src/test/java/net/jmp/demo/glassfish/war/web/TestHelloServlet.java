package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestHelloServlet.java 0.1.0   06/02/2026
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.nio.charset.StandardCharsets;

import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// The test class for the HelloServlet class
@ExtendWith(MockitoExtension.class)
class TestHelloServlet {
    @Test
    void testDoGetDefaultsToWorld() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        when(bundle.getString("servlet.title")).thenReturn("Servlet Title");
        when(bundle.getString("hello")).thenReturn("Hello");
        when(bundle.getString("try")).thenReturn("Try");
        when(request.getParameter("name")).thenReturn(null);
        when(response.getWriter()).thenReturn(printWriter);

        final HelloServlet servlet = new HelloServlet(bundle);

        servlet.doGet(request, response);

        printWriter.flush();

        /* Make sure the set methods were called on the response one time for each */

        verify(response).setCharacterEncoding(StandardCharsets.UTF_8.name());
        verify(response).setContentType("text/html");

        final String html = stringWriter.toString();

        assertTrue(html.contains("<title>Servlet Title</title>"));
        assertTrue(html.contains("<h1>Hello, World!</h1>"));
        assertTrue(html.contains("<p>Try: <code>?name=Jonathan</code></p>"));
    }

    @Test
    void testDoGetUsesProvidedName() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        when(bundle.getString("servlet.title")).thenReturn("Servlet Title");
        when(bundle.getString("hello")).thenReturn("Hello");
        when(bundle.getString("try")).thenReturn("Try");
        when(request.getParameter("name")).thenReturn("Jonathan");
        when(response.getWriter()).thenReturn(printWriter);

        final HelloServlet servlet = new HelloServlet(bundle);

        servlet.doGet(request, response);

        printWriter.flush();

        assertTrue(stringWriter.toString().contains("<h1>Hello, Jonathan!</h1>"));
    }

    @Test
    void testDoGetEscapesProvidedName() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        when(bundle.getString("servlet.title")).thenReturn("Servlet Title");
        when(bundle.getString("hello")).thenReturn("Hello");
        when(bundle.getString("try")).thenReturn("Try");
        when(request.getParameter("name")).thenReturn("<script>alert(\"xss\")</script>");
        when(response.getWriter()).thenReturn(printWriter);

        final HelloServlet servlet = new HelloServlet(bundle);

        servlet.doGet(request, response);

        printWriter.flush();

        final String html = stringWriter.toString();

        assertTrue(html.contains("<h1>Hello, &lt;script&gt;alert(&quot;xss&quot;)&lt;/script&gt;!</h1>"));
        assertEquals(-1, html.indexOf("<script>"));
    }
}
