package net.jmp.demo.glassfish.war.message;

/*
 * (#)TestRegistrationMessageBean.java  0.3.0   07/02/2026
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

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.TextMessage;

import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/// The test class for the RegistrationMessageBean class
@ExtendWith(MockitoExtension.class)
class TestRegistrationMessageBean {
    @Test
    void testOnMessageWithTextMessageExtractsText() throws Exception {
        final TextMessage message = mock(TextMessage.class);
        final RegistrationMessageBean bean = new RegistrationMessageBean();

        when(message.getText()).thenReturn("Registered: jonathan@example.com");

        assertDoesNotThrow(() -> bean.onMessage(message));
    }

    @Test
    void testOnMessageWithNonTextMessageDoesNotThrow() {
        final Message message = mock(Message.class);
        final RegistrationMessageBean bean = new RegistrationMessageBean();

        assertDoesNotThrow(() -> bean.onMessage(message));
    }

    @Test
    void testOnMessageWithJMSExceptionThrowsRuntimeException() throws Exception {
        final TextMessage message = mock(TextMessage.class);
        final RegistrationMessageBean bean = new RegistrationMessageBean();

        when(message.getText()).thenThrow(new JMSException("test"));

        assertThrows(RuntimeException.class, () -> bean.onMessage(message));
    }
}
