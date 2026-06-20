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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;

import javax.sql.DataSource;

import net.jmp.demo.glassfish.war.dto.User;

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
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        when(request.getParameter("projectId")).thenReturn("1");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("user_id")).thenReturn(1);
        when(resultSet.getString("first_name")).thenReturn("Alice");
        when(resultSet.getString("last_name")).thenReturn("Smith");
        when(resultSet.getString("email")).thenReturn("alice.smith@example.com");
        when(resultSet.getInt("age")).thenReturn(28);
        when(resultSet.getString("role")).thenReturn("lead");
        when(resultSet.getInt("project_id")).thenReturn(1);
        when(resultSet.getTimestamp("created_at")).thenReturn(createdAt);
        when(request.getRequestDispatcher(USERS_JSP)).thenReturn(dispatcher);

        final UsersServlet servlet = new UsersServlet(dataSource);

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

        final User user = users.getFirst();

        assertEquals(1, user.getUserId());
        assertEquals("Alice", user.getFirstName());
        assertEquals("Smith", user.getLastName());
        assertEquals("alice.smith@example.com", user.getEmail());
        assertEquals(28, user.getAge());
        assertEquals("lead", user.getRole());
        assertEquals(1, user.getProjectId());
        assertEquals(createdAt, user.getCreatedAt());
    }

    @Test
    void testDoGetWithEmptyResultSetSetsEmptyListAndForwardsToUsersJsp() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("projectId")).thenReturn("1");
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        when(request.getRequestDispatcher(USERS_JSP)).thenReturn(dispatcher);

        final UsersServlet servlet = new UsersServlet(dataSource);

        servlet.doGet(request, response);

        verify(request).setAttribute("projectId", 1);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> usersCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("users"), usersCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(usersCaptor.getValue().isEmpty());
    }

    @Test
    void testDoGetWithNullDataSourceSetsEmptyListAndForwardsToUsersJsp() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("projectId")).thenReturn("1");
        when(request.getRequestDispatcher(USERS_JSP)).thenReturn(dispatcher);

        final UsersServlet servlet = new UsersServlet();

        servlet.doGet(request, response);

        verify(request).setAttribute("projectId", 1);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> usersCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("users"), usersCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(usersCaptor.getValue().isEmpty());
    }

    @Test
    void testDoGetWithSQLExceptionSetsEmptyListAndForwardsToUsersJsp() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getParameter("projectId")).thenReturn("1");
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        when(request.getRequestDispatcher(USERS_JSP)).thenReturn(dispatcher);

        final UsersServlet servlet = new UsersServlet(dataSource);

        servlet.doGet(request, response);

        verify(request).setAttribute("projectId", 1);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> usersCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("users"), usersCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(usersCaptor.getValue().isEmpty());
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
