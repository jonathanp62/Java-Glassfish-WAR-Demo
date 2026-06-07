package net.jmp.demo.glassfish.war.filter;

/*
 * (#)TestRequestLoggingFilter.java 0.1.0   06/07/2026
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

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// The test class for the RequestLoggingFilter class
@ExtendWith(MockitoExtension.class)
class TestRequestLoggingFilter {
    @Test
    void testDoFilterInvokesChainForHttpRequest() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);
        final RequestLoggingFilter filter = new RequestLoggingFilter();

        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURI()).thenReturn("/servlet/hello");
        when(response.getStatus()).thenReturn(200);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterInvokesChainForNonHttpRequest() throws Exception {
        final ServletRequest request = mock(ServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);
        final RequestLoggingFilter filter = new RequestLoggingFilter();

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void testDoFilterPropagatesIOExceptionFromChain() throws Exception {
        final ServletRequest request = mock(ServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);
        final RequestLoggingFilter filter = new RequestLoggingFilter();
        final IOException exception = new IOException("I/O failure");

        doThrow(exception).when(chain).doFilter(request, response);

        final IOException ioException = assertThrows(IOException.class, () -> filter.doFilter(request, response, chain));

        assertEquals(exception, ioException);
    }

    @Test
    void testDoFilterPropagatesServletExceptionFromChain() throws Exception {
        final ServletRequest request = mock(ServletRequest.class);
        final ServletResponse response = mock(ServletResponse.class);
        final FilterChain chain = mock(FilterChain.class);
        final RequestLoggingFilter filter = new RequestLoggingFilter();
        final ServletException exception = new ServletException("Servlet failure");

        doThrow(exception).when(chain).doFilter(request, response);

        final ServletException servletException = assertThrows(ServletException.class, () -> filter.doFilter(request, response, chain));

        assertEquals(exception, servletException);
    }
}
