package net.jmp.demo.glassfish.war.service;

/*
 * (#)TestProjectService.java   0.2.0   06/23/2026
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

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the ProjectService class
@ExtendWith(MockitoExtension.class)
class TestProjectService {
    @Test
    void testGetAllReturnsProjectsFromDatabase() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("project_id")).thenReturn(1);
        when(resultSet.getString("project_name")).thenReturn("Alpha Core App");
        when(resultSet.getString("status")).thenReturn("In Progress");
        when(resultSet.getTimestamp("created_at")).thenReturn(createdAt);

        final ProjectService service = new ProjectService(dataSource);
        final List<Project> results = service.getAll();

        assertEquals(1, results.size());

        final Project result = results.getFirst();

        assertEquals(1, result.getProjectId());
        assertEquals("Alpha Core App", result.getProjectName());
        assertEquals("In Progress", result.getStatus());
        assertEquals(createdAt, result.getCreatedAt());

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(statement).executeQuery();
    }

    @Test
    void testGetAllReturnsEmptyListWhenNoProjectsExist() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final ProjectService service = new ProjectService(dataSource);
        final List<Project> results = service.getAll();

        assertTrue(results.isEmpty());

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(statement).executeQuery();
    }

    @Test
    void testGetAllReturnsMultipleProjects() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("project_id")).thenReturn(1, 2);
        when(resultSet.getString("project_name")).thenReturn("Alpha Core App", "Beta Platform");
        when(resultSet.getString("status")).thenReturn("In Progress", "Complete");
        when(resultSet.getTimestamp("created_at")).thenReturn(createdAt);

        final ProjectService service = new ProjectService(dataSource);
        final List<Project> results = service.getAll();

        assertEquals(2, results.size());
        assertEquals("Alpha Core App", results.get(0).getProjectName());
        assertEquals("Beta Platform", results.get(1).getProjectName());
    }

    @Test
    void testGetAllReturnsEmptyListWhenDataSourceIsNull() {
        final ProjectService service = new ProjectService();
        final List<Project> results = service.getAll();

        assertTrue(results.isEmpty());
    }

    @Test
    void testGetAllReturnsEmptyListOnSQLException() throws Exception {
        final DataSource dataSource = mock(DataSource.class);

        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        final ProjectService service = new ProjectService(dataSource);
        final List<Project> results = service.getAll();

        assertTrue(results.isEmpty());
    }
}
