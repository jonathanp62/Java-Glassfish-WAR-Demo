package net.jmp.demo.glassfish.war.service;

/*
 * (#)TestPeopleService.java  0.2.0   06/25/2026
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

import net.jmp.demo.glassfish.war.dto.Person;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

/// The test class for the PeopleService class
@ExtendWith(MockitoExtension.class)
class TestPeopleService {
    @Test
    void testGetAllReturnsPeopleFromQuery() {
        final EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        final TypedQuery<Person> query = mock(TypedQuery.class);

        final Person person = new Person();

        person.setId(1L);
        person.setName("Jane Doe");
        person.setEmail("jane.doe@example.com");
        person.setComment("Test comment");

        when(em.createQuery("SELECT p FROM Person p", Person.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(person));

        final PeopleService service = new PeopleService(em);

        final List<Person> results = service.getAll();

        assertEquals(1, results.size());

        final Person result = results.getFirst();

        assertEquals(1L, result.getId());
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane.doe@example.com", result.getEmail());
        assertEquals("Test comment", result.getComment());

        verify(em).createQuery("SELECT p FROM Person p", Person.class);
        verify(query).getResultList();
    }

    @Test
    void testGetAllReturnsEmptyListWhenNoPeopleExist() {
        final EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        final TypedQuery<Person> query = mock(TypedQuery.class);

        when(em.createQuery("SELECT p FROM Person p", Person.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(new ArrayList<>());

        final PeopleService service = new PeopleService(em);

        final List<Person> results = service.getAll();

        assertTrue(results.isEmpty());

        verify(em).createQuery("SELECT p FROM Person p", Person.class);
        verify(query).getResultList();
    }

    @Test
    void testGetAllReturnsMultiplePeople() {
        final EntityManager em = mock(EntityManager.class);

        @SuppressWarnings("unchecked")
        final TypedQuery<Person> query = mock(TypedQuery.class);

        final Person person1 = new Person();

        person1.setId(1L);
        person1.setName("Jane Doe");
        person1.setEmail("jane.doe@example.com");
        person1.setComment("First");

        final Person person2 = new Person();

        person2.setId(2L);
        person2.setName("John Doe");
        person2.setEmail("john.doe@example.com");
        person2.setComment("Second");

        when(em.createQuery("SELECT p FROM Person p", Person.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(person1, person2));

        final PeopleService service = new PeopleService(em);

        final List<Person> results = service.getAll();

        assertEquals(2, results.size());
        assertEquals("Jane Doe", results.get(0).getName());
        assertEquals("John Doe", results.get(1).getName());
    }

    @Test
    void testSavePersistsWhenIdIsNull() {
        final EntityManager em = mock(EntityManager.class);

        final Person person = new Person();

        person.setName("Jane Doe");
        person.setEmail("jane.doe@example.com");
        person.setComment("New");

        final PeopleService service = new PeopleService(em);

        final Person result = service.save(person);

        assertSame(person, result);

        verify(em).persist(person);
        verify(em, never()).merge(any(Person.class));
    }

    @Test
    void testSaveMergesWhenIdIsNotNull() {
        final EntityManager em = mock(EntityManager.class);

        final Person person = new Person();

        person.setId(1L);
        person.setName("Jane Doe");
        person.setEmail("jane.doe@example.com");
        person.setComment("Updated");

        final Person merged = new Person();

        merged.setId(1L);
        merged.setName("Jane Doe");
        merged.setEmail("jane.doe@example.com");
        merged.setComment("Updated");

        when(em.merge(person)).thenReturn(merged);

        final PeopleService service = new PeopleService(em);

        final Person result = service.save(person);

        assertSame(merged, result);

        verify(em, never()).persist(any(Person.class));
        verify(em).merge(person);
    }
}
