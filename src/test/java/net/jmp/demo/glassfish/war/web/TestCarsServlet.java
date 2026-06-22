package net.jmp.demo.glassfish.war.web;

/*
 * (#)TestCarsServlet.java  0.2.0   06/22/2026
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

import java.util.ArrayList;
import java.util.List;

import net.jmp.demo.glassfish.war.dto.Car;
import net.jmp.demo.glassfish.war.service.CarService;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the CarsServlet class
@ExtendWith(MockitoExtension.class)
class TestCarsServlet {
    /// The cars JSP
    private static final String CARS_JSP = "/WEB-INF/jsp/cars.jsp";

    @Test
    void testDoGetSetsCarsAttributeAndForwardsToCarsJsp() throws Exception {
        final CarService carService = mock(CarService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        final Car car = new Car();

        car.setId(1L);
        car.setYear(2024);
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setColor("Blue");
        car.setStyle("Sedan");

        when(carService.getAll()).thenReturn(List.of(car));
        when(request.getRequestDispatcher(CARS_JSP)).thenReturn(dispatcher);

        final CarsServlet servlet = new CarsServlet(carService);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> carsCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("cars"), carsCaptor.capture());
        verify(request).getRequestDispatcher(CARS_JSP);
        verify(dispatcher).forward(request, response);

        @SuppressWarnings("unchecked")
        final List<Car> cars = carsCaptor.getValue();

        assertEquals(1, cars.size());

        final Car result = cars.getFirst();

        assertEquals(1L, result.getId());
        assertEquals(2024, result.getYear());
        assertEquals("Toyota", result.getMake());
        assertEquals("Camry", result.getModel());
        assertEquals("Blue", result.getColor());
        assertEquals("Sedan", result.getStyle());
    }

    @Test
    void testDoGetWithEmptyListSetsEmptyListAndForwardsToCarsJsp() throws Exception {
        final CarService carService = mock(CarService.class);
        final HttpServletRequest request = mock(HttpServletRequest.class);
        final HttpServletResponse response = mock(HttpServletResponse.class);
        final RequestDispatcher dispatcher = mock(RequestDispatcher.class);

        when(carService.getAll()).thenReturn(new ArrayList<>());
        when(request.getRequestDispatcher(CARS_JSP)).thenReturn(dispatcher);

        final CarsServlet servlet = new CarsServlet(carService);

        servlet.doGet(request, response);

        @SuppressWarnings("rawtypes")
        final ArgumentCaptor<List> carsCaptor = ArgumentCaptor.forClass(List.class);

        verify(request).setAttribute(eq("cars"), carsCaptor.capture());
        verify(dispatcher).forward(request, response);

        assertTrue(carsCaptor.getValue().isEmpty());
    }
}
