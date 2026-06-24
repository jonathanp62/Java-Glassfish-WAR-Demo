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

import jakarta.servlet.RequestDispatcher;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

import net.jmp.demo.glassfish.war.dto.DistanceData;

import net.jmp.demo.glassfish.war.service.DistanceService;

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
        final DistanceService distanceService = mock(DistanceService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);
        final DistanceData data = new DistanceData();

        data.setDocumentId("abc123");
        data.setFromZipCode("10001");
        data.setToZipCode("90210");
        data.setToCity("Beverly Hills");
        data.setToState("CA");
        data.setDistanceInMiles(2789.456);
        data.setDistanceInKilometers(4489.012);

        when(distanceService.getAll()).thenReturn(List.of(data));
        when(request.getRequestDispatcher(DISTANCE_JSP)).thenReturn(dispatcher);

        final DistanceServlet servlet = new DistanceServlet(distanceService);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> distancesCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("distances"), distancesCaptor.capture());
        verify(request).getRequestDispatcher(DISTANCE_JSP);
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<DistanceData> distances = distancesCaptor.getValue();

        assertEquals(1, distances.size());

        final DistanceData result = distances.getFirst();

        assertEquals("abc123", result.getDocumentId());
        assertEquals("10001", result.getFromZipCode());
        assertEquals("90210", result.getToZipCode());
        assertEquals("Beverly Hills", result.getToCity());
        assertEquals("CA", result.getToState());
        assertEquals(2789.456, result.getDistanceInMiles());
        assertEquals(4489.012, result.getDistanceInKilometers());
    }

    @Test
    void testDoGetWithEmptyCollectionSetsEmptyListAndForwardsToDistanceJsp() throws Exception {
        final DistanceService distanceService = mock(DistanceService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(distanceService.getAll()).thenReturn(List.of());
        when(request.getRequestDispatcher(DISTANCE_JSP)).thenReturn(dispatcher);

        final DistanceServlet servlet = new DistanceServlet(distanceService);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> distancesCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("distances"), distancesCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(distancesCaptor.getValue().isEmpty());
    }

    @Test
    void testDoGetWithServiceExceptionPropagatesRuntimeException() {
        final DistanceService distanceService = mock(DistanceService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);

        when(distanceService.getAll()).thenThrow(new RuntimeException("Service failure"));

        final DistanceServlet servlet = new DistanceServlet(distanceService);

        assertThrows(RuntimeException.class, () -> servlet.doGet(request, response));
    }
}
