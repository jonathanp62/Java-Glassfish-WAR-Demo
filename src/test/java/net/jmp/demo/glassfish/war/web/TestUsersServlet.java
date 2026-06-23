package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestUsersServlet.java  0.2.0   06/20/2026
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

import java.sql.Timestamp;

import java.util.List;

import net.jmp.demo.glassfish.war.dto.User;

import net.jmp.demo.glassfish.war.service.UserService;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the UsersServlet class
@ExtendWith(MockitoExtension.class)
class TestUsersServlet {
    /// The users JSP
    private static final String USERS_JSP = "/WEB-INF/jsp/users.jsp";

    @Test
    void testDoGetSetsProjectIdAndUsersAttributesAndForwardsToUsersJsp() throws Exception {
        final UserService userService = mock(UserService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        final User user = new User();

        user.setUserId(1);
        user.setFirstName("Alice");
        user.setLastName("Smith");
        user.setEmail("alice.smith@example.com");
        user.setAge(28);
        user.setRole("lead");
        user.setProjectId(1);
        user.setCreatedAt(createdAt);

        when(request.getParameter("projectId")).thenReturn("1");
        when(userService.getForProject(1)).thenReturn(List.of(user));
        when(request.getRequestDispatcher(USERS_JSP)).thenReturn(dispatcher);

        final UsersServlet servlet = new UsersServlet(userService);

        servlet.doGet(request, response);

        verify(request).setAttribute("projectId", 1);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> usersCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("users"), usersCaptor.capture());
        verify(request).getRequestDispatcher(USERS_JSP);
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<User> users = usersCaptor.getValue();

        assertEquals(1, users.size());

        final User result = users.getFirst();

        assertEquals(1, result.getUserId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("alice.smith@example.com", result.getEmail());
        assertEquals(28, result.getAge());
        assertEquals("lead", result.getRole());
        assertEquals(1, result.getProjectId());
        assertEquals(createdAt, result.getCreatedAt());
    }

    @Test
    void testDoGetWithEmptyResultSetSetsEmptyListAndForwardsToUsersJsp() throws Exception {
        final UserService userService = mock(UserService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("projectId")).thenReturn("1");
        when(userService.getForProject(1)).thenReturn(List.of());
        when(request.getRequestDispatcher(USERS_JSP)).thenReturn(dispatcher);

        final UsersServlet servlet = new UsersServlet(userService);

        servlet.doGet(request, response);

        verify(request).setAttribute("projectId", 1);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> usersCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("users"), usersCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(usersCaptor.getValue().isEmpty());
    }

    @Test
    void testDoGetWithNullUserServiceThrowsNullPointerException() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("projectId")).thenReturn("1");

        final UsersServlet servlet = new UsersServlet();

        assertThrows(NullPointerException.class, () -> servlet.doGet(request, response));
    }

    @Test
    void testDoGetWithServiceExceptionPropagatesRuntimeException() {
        final UserService userService = mock(UserService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("projectId")).thenReturn("1");
        when(userService.getForProject(1)).thenThrow(new RuntimeException("Service failure"));

        final UsersServlet servlet = new UsersServlet(userService);

        assertThrows(RuntimeException.class, () -> servlet.doGet(request, response));
    }

    @Test
    void testDoGetWithMissingProjectIdThrowsServletException() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("projectId")).thenReturn(null);

        final UsersServlet servlet = new UsersServlet();

        assertThrows(ServletException.class, () -> servlet.doGet(request, response));
    }

    @Test
    void testDoGetWithInvalidProjectIdThrowsServletException() {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(request.getParameter("projectId")).thenReturn("abc");

        final UsersServlet servlet = new UsersServlet();

        assertThrows(ServletException.class, () -> servlet.doGet(request, response));
    }
}
