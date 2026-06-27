package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestFormServlet.java  0.1.0   06/03/2026
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

import net.jmp.demo.glassfish.war.dto.Person;

import net.jmp.demo.glassfish.war.service.PeopleService;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

/// The test class for the FormServlet class
@ExtendWith(MockitoExtension.class)
class TestFormServlet {
    /// The form JSP
    private static final String FORM_JSP = "/WEB-INF/jsp/form.jsp";

    @Test
    void testDoGetForwardsToFormJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher(FORM_JSP)).thenReturn(dispatcher);

        final FormServlet servlet = new FormServlet(bundle);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher("/WEB-INF/jsp/form.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPostWithValidInputSetsSuccessMessageAndForwardsToFormJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final PeopleService peopleService = mock(PeopleService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final ArgumentCaptor<Person> personCaptor = ArgumentCaptor.forClass(Person.class);

        when(bundle.getString("servlet.form.success")).thenReturn("Form submitted successfully");
        when(request.getParameter("name")).thenReturn(" Jonathan ");
        when(request.getParameter("email")).thenReturn(" jonathan@example.com ");
        when(request.getParameter("comment")).thenReturn(" This is a test comment. ");
        when(request.getRequestDispatcher(FORM_JSP)).thenReturn(dispatcher);

        final FormServlet servlet = new FormServlet(bundle, peopleService);

        servlet.doPost(request, response);

        verify(request).setCharacterEncoding("UTF-8");
        verify(request).setAttribute("name", "Jonathan");
        verify(request).setAttribute("email", "jonathan@example.com");
        verify(request).setAttribute("comment", "This is a test comment.");
        verify(request).setAttribute("successMessage", "Form submitted successfully");
        verify(request, never()).setAttribute(eq("errors"), any());
        verify(dispatcher).forward(request, response);
        verify(peopleService).save(personCaptor.capture());

        final Person saved = personCaptor.getValue();

        assertEquals("Jonathan", saved.getName());
        assertEquals("jonathan@example.com", saved.getEmail());
        assertEquals("This is a test comment.", saved.getComment());
    }

    @Test
    void testDoPostWithMissingInputSetsRequiredValidationErrorsAndForwardsToFormJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> errorsCaptor = ArgumentCaptor.forClass(List.class);

        when(request.getParameter("name")).thenReturn(" ");     // Trim to null applies
        when(request.getParameter("email")).thenReturn(null);   // Trim to null applies
        when(request.getParameter("comment")).thenReturn("");   // Trim to null applies
        when(request.getRequestDispatcher(FORM_JSP)).thenReturn(dispatcher);

        final FormServlet servlet = new FormServlet(bundle);

        servlet.doPost(request, response);

        verify(request).setAttribute("name", null);
        verify(request).setAttribute("email", null);
        verify(request).setAttribute("comment", null);
        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());
        verify(request, never()).setAttribute(eq("successMessage"), any());
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<String> errors = errorsCaptor.getValue();    // Errors set by bean validation

        assertEquals(3, errors.size());
        assertTrue(errors.contains("Name is required"));
        assertTrue(errors.contains("Email is required"));
        assertTrue(errors.contains("Comment is required"));
    }

    @Test
    void testDoPostWithInvalidEmailAndShortCommentSetsValidationErrorsAndForwardsToFormJsp() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> errorsCaptor = ArgumentCaptor.forClass(List.class);

        when(request.getParameter("name")).thenReturn("Jonathan");
        when(request.getParameter("email")).thenReturn("jonathan");
        when(request.getParameter("comment")).thenReturn("Short");
        when(request.getRequestDispatcher(FORM_JSP)).thenReturn(dispatcher);

        final FormServlet servlet = new FormServlet(bundle);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), errorsCaptor.capture());
        verify(request, never()).setAttribute(eq("successMessage"), any());
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<String> errors = errorsCaptor.getValue();    // Errors set by bean validation

        assertEquals(2, errors.size());
        assertTrue(errors.contains("Email must be a valid email address"));
        assertTrue(errors.contains("Comment must be at least 10 characters"));
    }

    @Test
    void testDoPostWrapsUnsupportedEncodingExceptionInServletException() throws Exception {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final UnsupportedEncodingException exception = new UnsupportedEncodingException("UTF-8");

        doThrow(exception).when(request).setCharacterEncoding("UTF-8");

        final FormServlet servlet = new FormServlet(bundle);

        final ServletException servletException = assertThrows(ServletException.class, () -> servlet.doPost(request, response));

        assertEquals(exception, servletException.getCause());
    }
}
