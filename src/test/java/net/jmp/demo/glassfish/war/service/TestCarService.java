package net.jmp.demo.glassfish.war.service;

/*
 * (#)TestCarService.java  0.2.0   06/22/2026
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

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.ArrayList;
import java.util.List;

import net.jmp.demo.glassfish.war.dto.Car;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the CarService class
@ExtendWith(MockitoExtension.class)
class TestCarService {
    @Test
    void testGetAllReturnsCarsFromQuery() {
        final EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        final TypedQuery<Car> query = mock(TypedQuery.class);

        final Car car = new Car();

        car.setId(1L);
        car.setYear(2024);
        car.setMake("Toyota");
        car.setModel("Camry");
        car.setColor("Blue");
        car.setStyle("Sedan");

        when(em.createQuery("SELECT c FROM Car c", Car.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(car));

        final CarService service = new CarService(em);

        final List<Car> results = service.getAll();

        assertEquals(1, results.size());

        final Car result = results.getFirst();

        assertEquals(1L, result.getId());
        assertEquals(2024, result.getYear());
        assertEquals("Toyota", result.getMake());
        assertEquals("Camry", result.getModel());
        assertEquals("Blue", result.getColor());
        assertEquals("Sedan", result.getStyle());

        verify(em).createQuery("SELECT c FROM Car c", Car.class);
        verify(query).getResultList();
    }

    @Test
    void testGetAllReturnsEmptyListWhenNoCarsExist() {
        final EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        final TypedQuery<Car> query = mock(TypedQuery.class);

        when(em.createQuery("SELECT c FROM Car c", Car.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        final CarService service = new CarService(em);

        final List<Car> results = service.getAll();

        assertTrue(results.isEmpty());

        verify(em).createQuery("SELECT c FROM Car c", Car.class);
        verify(query).getResultList();
    }

    @Test
    void testGetAllReturnsMultipleCars() {
        final EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        final TypedQuery<Car> query = mock(TypedQuery.class);

        final Car car1 = new Car();

        car1.setId(1L);
        car1.setYear(2024);
        car1.setMake("Toyota");
        car1.setModel("Camry");
        car1.setColor("Blue");
        car1.setStyle("Sedan");

        final Car car2 = new Car();

        car2.setId(2L);
        car2.setYear(2023);
        car2.setMake("Honda");
        car2.setModel("Civic");
        car2.setColor("Red");
        car2.setStyle("Coupe");

        when(em.createQuery("SELECT c FROM Car c", Car.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(car1, car2));

        final CarService service = new CarService(em);

        final List<Car> results = service.getAll();

        assertEquals(2, results.size());
        assertEquals("Toyota", results.get(0).getMake());
        assertEquals("Honda", results.get(1).getMake());
    }
}
