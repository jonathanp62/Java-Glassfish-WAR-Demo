package net.jmp.demo.glassfish.war.filter;

/*
 * (#)RequestLoggingFilter.java 0.1.0   06/07/2026
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

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import jakarta.servlet.annotation.WebFilter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The request logging filter class
@WebFilter(urlPatterns = "/*")
public class RequestLoggingFilter implements Filter {
    // Initialize the SLF4J Logger
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The do filter method
    ///
    /// @param  request     jakarta.servlet.ServletRequest
    /// @param  response    jakarta.servlet.ServletResponse
    /// @param  chain       jakarta.servlet.FilterChain
    /// @throws             java.io.IOException When an I/O error occurs
    /// @throws             jakarta.servlet.ServletException When an error occurs
    @Override
    public void doFilter(
            final ServletRequest request,
            final ServletResponse response,
            final FilterChain chain) throws IOException, ServletException {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entryWith(request, response, chain));
        }

        final long start = System.nanoTime();

        try {
            chain.doFilter(request, response);
        } finally {
            final long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;

            if (request instanceof HttpServletRequest httpRequest && response instanceof HttpServletResponse httpResponse) {
                this.logger.info(
                        "{} {} completed with status {} in {} ms",
                        httpRequest.getMethod(),
                        httpRequest.getRequestURI(),
                        httpResponse.getStatus(),
                        elapsedMillis);
            } else {
                this.logger.info("Request completed in {} ms", elapsedMillis);
            }
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }
}
