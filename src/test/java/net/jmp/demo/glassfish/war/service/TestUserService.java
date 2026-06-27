package net.jmp.demo.glassfish.war.service;

/*
 * (#)TestUserService.java  0.2.0   06/23/2026
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

import net.jmp.demo.glassfish.war.dto.User;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the UserService class
@ExtendWith(MockitoExtension.class)
class TestUserService {
    @Test
    void testGetForProjectReturnsUsersFromDatabase() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

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

        final UserService service = new UserService(dataSource);
        final List<User> results = service.getForProject(1);

        assertEquals(1, results.size());

        final User result = results.getFirst();

        assertEquals(1, result.getUserId());
        assertEquals("Alice", result.getFirstName());
        assertEquals("Smith", result.getLastName());
        assertEquals("alice.smith@example.com", result.getEmail());
        assertEquals(28, result.getAge());
        assertEquals("lead", result.getRole());
        assertEquals(1, result.getProjectId());
        assertEquals(createdAt, result.getCreatedAt());

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(statement).executeQuery();
    }

    @Test
    void testGetForProjectReturnsEmptyListWhenNoUsersExist() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        final UserService service = new UserService(dataSource);
        final List<User> results = service.getForProject(1);

        assertTrue(results.isEmpty());

        verify(dataSource).getConnection();
        verify(connection).prepareStatement(anyString());
        verify(statement).executeQuery();
    }

    @Test
    void testGetForProjectReturnsMultipleUsers() throws Exception {
        final DataSource dataSource = mock(DataSource.class);
        final Connection connection = mock(Connection.class);
        final PreparedStatement statement = mock(PreparedStatement.class);
        final ResultSet resultSet = mock(ResultSet.class);
        final Timestamp createdAt = new Timestamp(System.currentTimeMillis());

        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("user_id")).thenReturn(1, 2);
        when(resultSet.getString("first_name")).thenReturn("Alice", "Bob");
        when(resultSet.getString("last_name")).thenReturn("Smith", "Jones");
        when(resultSet.getString("email")).thenReturn("alice.smith@example.com", "bob.jones@example.com");
        when(resultSet.getInt("age")).thenReturn(28, 34);
        when(resultSet.getString("role")).thenReturn("lead", "developer");
        when(resultSet.getInt("project_id")).thenReturn(1);
        when(resultSet.getTimestamp("created_at")).thenReturn(createdAt);

        final UserService service = new UserService(dataSource);
        final List<User> results = service.getForProject(1);

        assertEquals(2, results.size());
        assertEquals("Alice", results.get(0).getFirstName());
        assertEquals("Bob", results.get(1).getFirstName());
    }

    @Test
    void testGetForProjectReturnsEmptyListWhenDataSourceIsNull() {
        final UserService service = new UserService();
        final List<User> results = service.getForProject(1);

        assertTrue(results.isEmpty());
    }

    @Test
    void testGetForProjectReturnsEmptyListOnSQLException() throws Exception {
        final DataSource dataSource = mock(DataSource.class);

        when(dataSource.getConnection()).thenThrow(new SQLException("Connection failed"));

        final UserService service = new UserService(dataSource);
        final List<User> results = service.getForProject(1);

        assertTrue(results.isEmpty());
    }
}
