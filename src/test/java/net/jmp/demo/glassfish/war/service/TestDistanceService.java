package net.jmp.demo.glassfish.war.service;

/*
 * (#)TestDistanceService.java  0.2.0   06/23/2026
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

import java.util.List;

import net.jmp.demo.glassfish.war.dto.DistanceData;

import org.bson.Document;
import org.bson.types.ObjectId;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the DistanceService class
@ExtendWith(MockitoExtension.class)
class TestDistanceService {
    @Test
    void testGetAllReturnsDistancesFromDatabase() {
        final MongoDatabase mongoDatabase = mock(MongoDatabase.class);

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

        final DistanceService service = new DistanceService(mongoDatabase);
        final List<DistanceData> results = service.getAll();

        assertEquals(1, results.size());

        final DistanceData result = results.getFirst();

        assertEquals(objectId.toHexString(), result.getDocumentId());
        assertEquals("10001", result.getFromZipCode());
        assertEquals("90210", result.getToZipCode());
        assertEquals("Beverly Hills", result.getToCity());
        assertEquals("CA", result.getToState());
        assertEquals(2789.456, result.getDistanceInMiles());
        assertEquals(4489.012, result.getDistanceInKilometers());

        verify(mongoDatabase).getCollection("distance");
        verify(collection).find();
    }

    @Test
    void testGetAllReturnsEmptyListWhenCollectionIsEmpty() {
        final MongoDatabase mongoDatabase = mock(MongoDatabase.class);

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

        final DistanceService service = new DistanceService(mongoDatabase);
        final List<DistanceData> results = service.getAll();

        assertTrue(results.isEmpty());

        verify(mongoDatabase).getCollection("distance");
        verify(collection).find();
    }

    @Test
    void testGetAllReturnsMultipleDistances() {
        final MongoDatabase mongoDatabase = mock(MongoDatabase.class);

        @SuppressWarnings("unchecked")
        final MongoCollection<Document> collection = mock(MongoCollection.class);

        @SuppressWarnings("unchecked")
        final FindIterable<Document> findIterable = mock(FindIterable.class);

        @SuppressWarnings("unchecked")
        final MongoCursor<Document> cursor = mock(MongoCursor.class);

        final Document doc1 = new Document("_id", new ObjectId())
                .append("fromZipCode", "10001")
                .append("toZipCode", "90210")
                .append("toCity", "Beverly Hills")
                .append("toState", "CA")
                .append("distanceInMiles", 2789.456)
                .append("distanceInKilometers", 4489.012);

        final Document doc2 = new Document("_id", new ObjectId())
                .append("fromZipCode", "10001")
                .append("toZipCode", "60601")
                .append("toCity", "Chicago")
                .append("toState", "IL")
                .append("distanceInMiles", 790.123)
                .append("distanceInKilometers", 1271.456);

        when(mongoDatabase.getCollection("distance")).thenReturn(collection);
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, true, false);
        when(cursor.next()).thenReturn(doc1, doc2);

        final DistanceService service = new DistanceService(mongoDatabase);
        final List<DistanceData> results = service.getAll();

        assertEquals(2, results.size());
        assertEquals("Beverly Hills", results.get(0).getToCity());
        assertEquals("Chicago", results.get(1).getToCity());
    }

    @Test
    void testGetAllSetsZeroDistancesWhenFieldsAreNull() {
        final MongoDatabase mongoDatabase = mock(MongoDatabase.class);

        @SuppressWarnings("unchecked")
        final MongoCollection<Document> collection = mock(MongoCollection.class);

        @SuppressWarnings("unchecked")
        final FindIterable<Document> findIterable = mock(FindIterable.class);

        @SuppressWarnings("unchecked")
        final MongoCursor<Document> cursor = mock(MongoCursor.class);

        final Document doc = new Document("_id", new ObjectId())
                .append("fromZipCode", "10001")
                .append("toZipCode", "90210")
                .append("toCity", "Beverly Hills")
                .append("toState", "CA");

        when(mongoDatabase.getCollection("distance")).thenReturn(collection);
        when(collection.find()).thenReturn(findIterable);
        when(findIterable.iterator()).thenReturn(cursor);
        when(cursor.hasNext()).thenReturn(true, false);
        when(cursor.next()).thenReturn(doc);

        final DistanceService service = new DistanceService(mongoDatabase);
        final List<DistanceData> results = service.getAll();

        assertEquals(1, results.size());
        assertEquals(0.0, results.getFirst().getDistanceInMiles());
        assertEquals(0.0, results.getFirst().getDistanceInKilometers());
    }
}
