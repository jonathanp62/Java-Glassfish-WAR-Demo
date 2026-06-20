package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestProjectsServlet.java  0.2.0   06/18/2026
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import java.util.List;

import javax.sql.DataSource;

import net.jmp.demo.glassfish.war.dto.Project;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the ProjectsServlet class
@ExtendWith(MockitoExtension.class)
class TestProjectsServlet {
    /// The projects JSP
    private static final String PROJECTS_JSP = "/WEB-INF/jsp/projects.jsp";

    @Test
    void testDoGetSetsProjectsAttributeAndForwardsToProjectsJsp() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("project_id")).thenReturn(1);
        when(resultSet.getString("project_name")).thenReturn("Alpha Core App");
        when(resultSet.getString("status")).thenReturn("In Progress");
        when(resultSet.getTimestamp("created_at")).thenReturn(createdAt);
        when(request.getRequestDispatcher(PROJECTS_JSP)).thenReturn(dispatcher);

        final ProjectsServlet servlet = new ProjectsServlet(dataSource);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> projectsCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("projects"), projectsCaptor.capture());
        verify(request).getRequestDispatcher(PROJECTS_JSP);
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<Project> projects = projectsCaptor.getValue();

        assertEquals(1, projects.size());

        final Project project = projects.getFirst();

        assertEquals(1, project.getProjectId());
        assertEquals("Alpha Core App", project.getProjectName());
        assertEquals("In Progress", project.getStatus());
        assertEquals(createdAt, project.getCreatedAt());
    }

    @Test
    void testDoGetWithEmptyResultSetSetsEmptyListAndForwardsToProjectsJsp() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);
        when(request.getRequestDispatcher(PROJECTS_JSP)).thenReturn(dispatcher);

        final ProjectsServlet servlet = new ProjectsServlet(dataSource);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> projectsCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("projects"), projectsCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(projectsCaptor.getValue().isEmpty());
    }

    @Test
    void testDoGetWithNullDataSourceSetsEmptyListAndForwardsToProjectsJsp() throws Exception {
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(request.getRequestDispatcher(PROJECTS_JSP)).thenReturn(dispatcher);

        final ProjectsServlet servlet = new ProjectsServlet();

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> projectsCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("projects"), projectsCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(projectsCaptor.getValue().isEmpty());
    }

    @Test
    void testDoGetWithSQLExceptionSetsEmptyListAndForwardsToProjectsJsp() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));
        when(request.getRequestDispatcher(PROJECTS_JSP)).thenReturn(dispatcher);

        final ProjectsServlet servlet = new ProjectsServlet(dataSource);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> projectsCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("projects"), projectsCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(projectsCaptor.getValue().isEmpty());
    }
}
