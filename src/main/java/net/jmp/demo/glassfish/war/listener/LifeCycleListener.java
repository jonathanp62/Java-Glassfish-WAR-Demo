package net.jmp.demo.glassfish.war.listener;

/*
 * (#)LifeCycleListener.java 0.1.0   06/09/2026
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

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The application life cycle listener class
@WebListener
public class LifeCycleListener implements ServletContextListener {
    private static final String ATTR_APP_START_INSTANT = "net.jmp.demo.glassfish.war.appStartInstant";

    // Initialize the SLF4J Logger
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The default constructor
    public LifeCycleListener() {
        super();
    }

    /// Receives notification that the web application initialization process is starting
    ///
    /// @param  sce jakarta.servlet.ServletContextEvent
    @Override
    public void contextInitialized(final ServletContextEvent sce) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(sce));
        }

        final Instant start = Instant.now();
        final ServletContext context = sce.getServletContext();

        context.setAttribute(ATTR_APP_START_INSTANT, start);

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Application startup");
            this.logger.info("Context path: {}", context.getContextPath());
            this.logger.info("Server info: {}", context.getServerInfo());
            this.logger.info("Servlet API version: {}.{}", context.getMajorVersion(), context.getMinorVersion());
        }

        final String localizationContext = context.getInitParameter("jakarta.servlet.jsp.jstl.fmt.localizationContext");

        if (this.logger.isInfoEnabled()) {
            this.logger.info("Context parameter {} = {}",
                    "jakarta.servlet.jsp.jstl.fmt.localizationContext",
                    localizationContext
            );
        }

        if (this.logger.isDebugEnabled()) {
            final Enumeration<String> initParameterNames = context.getInitParameterNames();

            while (initParameterNames.hasMoreElements()) {
                final String name = initParameterNames.nextElement();
                final String value = context.getInitParameter(name);

                this.logger.debug("Context parameter {} = {}", name, value);
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// Receives notification that the servlet context is about to be shut down
    ///
    /// @param  sce jakarta.servlet.ServletContextEvent
    @Override
    public void contextDestroyed(final ServletContextEvent sce) {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(sce));
        }

        final ServletContext context = sce.getServletContext();
        final Object attr = context.getAttribute(ATTR_APP_START_INSTANT);

        if (attr instanceof Instant start) {
            final Duration uptime = Duration.between(start, Instant.now());

            if (this.logger.isInfoEnabled()) {
                this.logger.info("Application shutdown; uptime {} seconds", uptime.toSeconds());
            }
        } else {
            if (this.logger.isInfoEnabled()) {
                this.logger.info("Application shutdown");
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
