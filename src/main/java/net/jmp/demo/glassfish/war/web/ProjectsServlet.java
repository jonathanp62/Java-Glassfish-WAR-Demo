package net.jmp.demo.glassfish.war.web;

/*
 * (#)ProjectsServlet.java 0.2.0   06/18/2026
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

import jakarta.annotation.Resource;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import net.jmp.demo.glassfish.war.dto.Project;

import org.jspecify.annotations.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The projects servlet class
@WebServlet(urlPatterns = "/servlet/projects")
public class ProjectsServlet extends HttpServlet {
    /// The serial version UID
    @Serial
    private static final long serialVersionUID = 1L;

    /// The projects JSP
    private static final String PROJECTS_JSP = "/WEB-INF/jsp/projects.jsp";

    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The SQLite data source
    @Resource(lookup = "jdbc/sqlite")
    @SuppressWarnings("NullAway")
    private @Nullable DataSource dataSource;

    /// Default constructor
    ProjectsServlet() {
        super();
    }

    /// Constructor for testing
    ///
    /// @param dataSource javax.sql.DataSource
    ProjectsServlet(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// The GET method. Called from /api/projects.
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             jakarta.servlet.ServletException    When an error occurs
    /// @throws             java.io.IOException                 When an I/O error occurs
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        request.setAttribute("projects", this.readProjects());
        request.getRequestDispatcher(PROJECTS_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// Read all projects from the SQLite database
    ///
    /// @return java.util.List
    private List<Project> readProjects() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final List<Project> projects = new ArrayList<>();
        final String sql = "SELECT project_id, project_name, status, created_at FROM projects";

        if (this.dataSource == null) {
            this.logger.error("DataSource is null");
            return projects;
        }

        try (final Connection connection = this.dataSource.getConnection();
             final PreparedStatement statement = connection.prepareStatement(sql);
             final ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                final Project project = new Project();

                project.setProjectId(resultSet.getInt("project_id"));
                project.setProjectName(resultSet.getString("project_name"));
                project.setStatus(resultSet.getString("status"));
                project.setCreatedAt(resultSet.getTimestamp("created_at"));

                projects.add(project);
            }
        } catch (final SQLException e) {
            this.logger.error("Error reading projects from database", e);
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Read {} projects", projects.size());
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(projects));
        }

        return projects;
    }
}
