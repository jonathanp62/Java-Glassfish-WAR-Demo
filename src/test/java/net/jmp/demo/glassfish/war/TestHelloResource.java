package net.jmp.demo.glassfish.war;

/*
 * (#)TestHelloResource.java    0.1.0   06/01/2026
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

import jakarta.ws.rs.core.Response;

import java.util.ResourceBundle;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/// The test class for the HelloResource class
@ExtendWith(MockitoExtension.class)
class TestHelloResource {
    @Test
    void testSayHello() {
        final ResourceBundle bundle = mock(ResourceBundle.class);

        when(bundle.getString("hello.java")).thenReturn("Hello (test)");

        final HelloResource helloResource = new HelloResource(bundle);

        try (final Response response = helloResource.sayHello()) {
            assertEquals(200, response.getStatus());
            assertEquals("Hello (test)", response.getEntity());
        }
    }

    @Test
    void testJsonHello() {
        final ResourceBundle bundle = mock(ResourceBundle.class);
        final HelloResource helloResource = new HelloResource(bundle);
        final StatusMessage expected = new StatusMessage("OK", "The quick brown fox jumped over the lazy dog");

        try (final Response response = helloResource.jsonHello()) {
            assertEquals(200, response.getStatus());
            assertEquals(expected, response.getEntity());
        }
    }
}
