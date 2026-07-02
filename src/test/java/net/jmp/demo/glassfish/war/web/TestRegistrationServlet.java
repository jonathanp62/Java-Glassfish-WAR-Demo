package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestRegistrationServlet.java  0.3.0   07/02/2026
 * (#)TestRegistrationServlet.java  0.1.0   06/04/2026
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
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;

import java.util.List;
import java.util.ResourceBundle;

import net.jmp.demo.glassfish.war.service.RegistrationService;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/// The test class for the RegistrationServlet class
@ExtendWith(MockitoExtension.class)
class TestRegistrationServlet {
    /// The registration form JSP
    private static final String REGISTER_JSP = "/WEB-INF/jsp/register.jsp";

    /// The registration successful JSP
    private static final String REGISTERED_JSP = "/WEB-INF/jsp/registered.jsp";

    @Test
    void testDoGetForwardsToRegisterJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final RegistrationService registrationService = mock(RegistrationService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher(REGISTER_JSP)).thenReturn(dispatcher);

        final RegistrationServlet servlet = new RegistrationServlet(bundle, registrationService);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/jsp/register.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPostWithValidInputSetsSuccessMessageAndForwardsToRegisteredJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final RegistrationService registrationService = mock(RegistrationService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(bundle.getString("servlet.registration.success")).thenReturn("Registration succeeded for {0}");
        when(request.getParameter("email")).thenReturn(" jonathan@example.com ");
        when(request.getRequestDispatcher(REGISTERED_JSP)).thenReturn(dispatcher);

        final RegistrationServlet servlet = new RegistrationServlet(bundle, registrationService);

        servlet.doPost(request, response);

        verify(request).setCharacterEncoding("UTF-8");
        verify(request).setAttribute("email", "jonathan@example.com");
        verify(request).setAttribute("successMessage", "Registration succeeded for jonathan@example.com");
        verify(request, never()).setAttribute(eq("errors"), any());
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPostWithMissingInputSetsRequiredValidationErrorAndForwardsToRegisteredJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final RegistrationService registrationService = mock(RegistrationService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> errorsCaptor = ArgumentCaptor.forClass(List.class);

        when(request.getParameter("email")).thenReturn(" ");
        when(request.getRequestDispatcher(REGISTERED_JSP)).thenReturn(dispatcher);

        final RegistrationServlet servlet = new RegistrationServlet(bundle, registrationService);

        servlet.doPost(request, response);

        verify(request).setAttribute("email", null);
        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());
        verify(request, never()).setAttribute(eq("successMessage"), any());
        verify(dispatcher).forward(request, response);

        assertEquals(List.of("Email is required"), errorsCaptor.getValue());
    }

    @Test
    void testDoPostWithInvalidEmailSetsValidationErrorAndForwardsToRegisteredJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final RegistrationService registrationService = mock(RegistrationService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> errorsCaptor = ArgumentCaptor.forClass(List.class);

        when(request.getParameter("email")).thenReturn("jonathan");
        when(request.getRequestDispatcher(REGISTERED_JSP)).thenReturn(dispatcher);

        final RegistrationServlet servlet = new RegistrationServlet(bundle, registrationService);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());
        verify(request, never()).setAttribute(eq("successMessage"), any());
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<String> errors = errorsCaptor.getValue();    // Errors set by bean validation

        assertEquals(1, errors.size());
        assertTrue(errors.contains("Email must be a valid email address"));
    }

    @Test
    void testDoPostWrapsUnsupportedEncodingExceptionInServletException() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final RegistrationService registrationService = mock(RegistrationService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final UnsupportedEncodingException exception = new UnsupportedEncodingException("UTF-8");

        doThrow(exception).when(request).setCharacterEncoding("UTF-8");

        final RegistrationServlet servlet = new RegistrationServlet(bundle, registrationService);

        final ServletException servletException = assertThrows(ServletException.class, () -> servlet.doPost(request, response));

        assertEquals(exception, servletException.getCause());
    }
}
