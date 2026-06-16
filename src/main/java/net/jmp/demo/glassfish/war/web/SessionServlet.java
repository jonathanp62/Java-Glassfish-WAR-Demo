package net.jmp.demo.glassfish.war.web;

/*
 * (#)SessionServlet.java  0.2.0   06/15/2026
 * (#)SessionServlet.java  0.1.0   06/09/2026
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

import jakarta.annotation.security.DeclareRoles;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.io.UnsupportedEncodingException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import net.jmp.demo.glassfish.war.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The session demonstration servlet class
@WebServlet(urlPatterns = "/servlet/session")
@DeclareRoles("user")
@ServletSecurity(@HttpConstraint(rolesAllowed = "user"))
public class SessionServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The session JSP
    private static final String SESSION_JSP = "/WEB-INF/jsp/session.jsp";

    /// The session attribute name for visit count
    private static final String VISIT_COUNT_ATTR = "visitCount";

    /// The session attribute name for username
    private static final String USERNAME_ATTR = "username";

    /// The date format for timestamps
    private final transient DateTimeFormatter dateTimeFormatter;

    /// The constructor
    public SessionServlet() {
        super();

        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.systemDefault());
    }

    /// The GET method. Called from /servlet/session.
    /// Handles session operations based on action parameter.
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             jakarta.servlet.ServletException When an error occurs
    /// @throws             java.io.IOException When an I/O error occurs
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (final UnsupportedEncodingException uee) {
            this.logger.error("Error setting character encoding to UTF-8", uee);
            throw new ServletException(uee);
        }

        final String action = StringUtils.trimToNull(request.getParameter("action"));

        if ("invalidate".equals(action)) {
            this.handleInvalidate(request, response);
        } else {
            this.handleSessionDisplay(request, response);
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// The POST method. Called from /servlet/session.
    /// Handles username storage in session.
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             jakarta.servlet.ServletException When an error occurs
    /// @throws             java.io.IOException When an I/O error occurs
    @Override
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        try {
            request.setCharacterEncoding("UTF-8");
        } catch (final UnsupportedEncodingException uee) {
            this.logger.error("Error setting character encoding to UTF-8", uee);
            throw new ServletException(uee);
        }

        final String username = StringUtils.trimToNull(request.getParameter(USERNAME_ATTR));

        if (username != null) {
            final HttpSession session = request.getSession();

            session.setAttribute(USERNAME_ATTR, username);

            this.logger.debug("Stored username '{}' in session {}", username, session.getId());
        }

        this.handleSessionDisplay(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// Handle session invalidation
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             java.io.IOException When an I/O error occurs
    private void handleInvalidate(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        final HttpSession session = request.getSession(false);

        if (session != null) {
            final String sessionId = session.getId();

            session.invalidate();

            this.logger.debug("Invalidated session {}", sessionId);
        }

        response.sendRedirect(request.getContextPath() + "/servlet/session");

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// Handle displaying session information
    ///
    /// @param  request     jakarta.servlet.http.HttpServletRequest
    /// @param  response    jakarta.servlet.http.HttpServletResponse
    /// @throws             jakarta.servlet.ServletException When an error occurs
    /// @throws             java.io.IOException When an I/O error occurs
    private void handleSessionDisplay(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response));
        }

        final HttpSession session = request.getSession();

        /* Increment the visit count */

        Integer visitCount = (Integer) session.getAttribute(VISIT_COUNT_ATTR);

        if (visitCount == null) {
            visitCount = 1;
        } else {
            visitCount++;
        }

        session.setAttribute(VISIT_COUNT_ATTR, visitCount);

        /* Prepare session information for the JSP */

        final Instant creationTime = Instant.ofEpochMilli(session.getCreationTime());
        final Instant lastAccessedTime = Instant.ofEpochMilli(session.getLastAccessedTime());

        request.setAttribute("sessionId", session.getId());
        request.setAttribute(VISIT_COUNT_ATTR, visitCount);
        request.setAttribute("creationTime", this.dateTimeFormatter.format(creationTime));
        request.setAttribute("lastAccessedTime", this.dateTimeFormatter.format(lastAccessedTime));
        request.setAttribute("maxInactiveInterval", session.getMaxInactiveInterval());
        request.setAttribute(USERNAME_ATTR, session.getAttribute(USERNAME_ATTR));
        request.setAttribute("isNew", session.isNew());

        request.getRequestDispatcher(SESSION_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
