package net.jmp.demo.glassfish.war.web;

/*
 * (#)DistanceServlet.java  0.2.0   06/17/2026
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

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.annotation.security.DeclareRoles;

import jakarta.inject.Inject;

import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import net.jmp.demo.glassfish.war.dto.DistanceData;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.jmp.util.logging.LoggerUtils.*;

/// The distance servlet class
@WebServlet(urlPatterns = "/servlet/distance")
@DeclareRoles("user")
@ServletSecurity(@HttpConstraint(rolesAllowed = "user"))
public class DistanceServlet extends HttpServlet {
    // Initialize the SLF4J Logger
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /// The distance JSP
    private static final String DISTANCE_JSP = "/WEB-INF/jsp/distance.jsp";

    /// The MongoDB database
    private final transient MongoDatabase mongoDatabase;

    /// The constructor
    ///
    /// @param  mongoDatabase   com.mongodb.client.MongoDatabase
    @Inject
    public DistanceServlet(final MongoDatabase mongoDatabase) {
        super();

        this.mongoDatabase = mongoDatabase;
    }

    /// The GET method. Called from /servlet/distance.
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

        final List<DistanceData> distances = this.readDistances();

        request.setAttribute("distances", distances);

        request.getRequestDispatcher(DISTANCE_JSP).forward(request, response);

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exit());
        }
    }

    /// Read all documents from the distance collection
    ///
    /// @return java.util.List
    private List<DistanceData> readDistances() {
        if (this.logger.isTraceEnabled()) {
            this.logger.trace(entry());
        }

        final List<DistanceData> distances = new ArrayList<>();
        final MongoCollection<Document> collection = this.mongoDatabase.getCollection("distance");

        for (final Document doc : collection.find()) {
            final DistanceData data = new DistanceData();
            final Object id = doc.get("_id");

            if (id instanceof ObjectId objectId) {
                data.setDocumentId(objectId.toHexString());
            }

            data.setFromZipCode(doc.getString("fromZipCode"));
            data.setToZipCode(doc.getString("toZipCode"));
            data.setToCity(doc.getString("toCity"));
            data.setToState(doc.getString("toState"));

            final Double miles = doc.getDouble("distanceInMiles");

            if (miles != null) {
                data.setDistanceInMiles(miles);
            }

            final Double kilometers = doc.getDouble("distanceInKilometers");

            if (kilometers != null) {
                data.setDistanceInKilometers(kilometers);
            }

            distances.add(data);
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Read {} distance documents", distances.size());
        }

        if (this.logger.isTraceEnabled()) {
            this.logger.trace(exitWith(distances));
        }

        return distances;
    }
}
