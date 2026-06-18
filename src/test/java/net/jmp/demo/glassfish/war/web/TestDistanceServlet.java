package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestDistanceServlet.java  0.2.0   06/17/2026
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

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import jakarta.servlet.RequestDispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import net.jmp.demo.glassfish.war.dto.DistanceData;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the DistanceServlet class
@ExtendWith(MockitoExtension.class)
class TestDistanceServlet {
    /// The distance JSP
    private static final String DISTANCE_JSP = "/WEB-INF/jsp/distance.jsp";

    @Test
    void testDoGetSetsDistancesAttributeAndForwardsToDistanceJsp() throws Exception {
        final MongoDatabase mongoDatabase = mock(MongoDatabase.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        @SuppressWarnings("unchecked")
        final MongoCollection<Document> collection = mock(MongoCollection.class);

        @SuppressWarnings("unchecked")
        final FindIterable<Document> findIterable = mock(FindIterable.class);

        @SuppressWarnings("unchecked")
        final MongoCursor<Document> cursor = mock(MongoCursor.class);

        final ObjectId objectId = new ObjectId();
        final Document doc = new Document("_id", objectId)
                .append("fromZipCode", "10001")
                .append("toZipCode", "90210")
                .append("toCity", "Beverly Hills")
                .append("toState", "CA")
                .append("distanceInMiles", 2789.456)
                .append("distanceInKilometers", 4489.012);

        when(mongoDatabase.getCollection("distance")).thenReturn(collection);
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(doc);
        when(request.getRequestDispatcher(DISTANCE_JSP)).thenReturn(dispatcher);

        final DistanceServlet servlet = new DistanceServlet(mongoDatabase);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> distancesCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("distances"), distancesCaptor.capture());
        verify(request).getRequestDispatcher(DISTANCE_JSP);
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<DistanceData> distances = distancesCaptor.getValue();

        assertEquals(1, distances.size());

        final DistanceData data = distances.getFirst();

        assertEquals(objectId.toHexString(), data.getDocumentId());
        assertEquals("10001", data.getFromZipCode());
        assertEquals("90210", data.getToZipCode());
        assertEquals("Beverly Hills", data.getToCity());
        assertEquals("CA", data.getToState());
        assertEquals(2789.456, data.getDistanceInMiles());
        assertEquals(4489.012, data.getDistanceInKilometers());
    }

    @Test
    void testDoGetWithEmptyCollectionSetsEmptyListAndForwardsToDistanceJsp() throws Exception {
        final MongoDatabase mongoDatabase = mock(MongoDatabase.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        @SuppressWarnings("unchecked")
        final MongoCollection<Document> collection = mock(MongoCollection.class);

        @SuppressWarnings("unchecked")
        final FindIterable<Document> findIterable = mock(FindIterable.class);

        @SuppressWarnings("unchecked")
        final MongoCursor<Document> cursor = mock(MongoCursor.class);

        when(mongoDatabase.getCollection("distance")).thenReturn(collection);
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(false);
        when(request.getRequestDispatcher(DISTANCE_JSP)).thenReturn(dispatcher);

        final DistanceServlet servlet = new DistanceServlet(mongoDatabase);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> distancesCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("distances"), distancesCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(distancesCaptor.getValue().isEmpty());
    }
}
