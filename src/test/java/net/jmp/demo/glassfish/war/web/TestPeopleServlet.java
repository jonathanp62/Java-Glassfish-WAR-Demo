package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestPeopleServlet.java    0.2.0   06/25/2026
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.ArrayList;
import java.util.List;

import net.jmp.demo.glassfish.war.dto.Person;
import net.jmp.demo.glassfish.war.service.PeopleService;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the PeopleServlet class
@ExtendWith(MockitoExtension.class)
class TestPeopleServlet {
    /// The people JSP
    private static final String PEOPLE_JSP = "/WEB-INF/jsp/people.jsp";

    @Test
    void testDoGetSetsPeopleAttributeAndForwardsToPeopleJsp() throws Exception {
        final PeopleService peopleService = mock(PeopleService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        final Person person = new Person();

        person.setId(1L);
        person.setName("Jane Doe");
        person.setEmail("jane.doe@example.com");
        person.setComment("Test comment");

        when(peopleService.getAll()).thenReturn(List.of(person));
        when(request.getRequestDispatcher(PEOPLE_JSP)).thenReturn(dispatcher);

        final PeopleServlet servlet = new PeopleServlet(peopleService);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> peopleCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("people"), peopleCaptor.capture());
        verify(request).getRequestDispatcher(PEOPLE_JSP);
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<Person> people = peopleCaptor.getValue();

        assertEquals(1, people.size());

        final Person result = people.getFirst();

        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane.doe@example.com", result.getEmail());
        assertEquals("Test comment", result.getComment());
    }

    @Test
    void testDoGetWithEmptyListSetsEmptyListAndForwardsToPeopleJsp() throws Exception {
        final PeopleService peopleService = mock(PeopleService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(peopleService.getAll()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher(PEOPLE_JSP)).thenReturn(dispatcher);

        final PeopleServlet servlet = new PeopleServlet(peopleService);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> peopleCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("people"), peopleCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(peopleCaptor.getValue().isEmpty());
    }
}
