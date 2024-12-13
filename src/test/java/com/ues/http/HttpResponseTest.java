package com.ues.http;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpResponseTest {

    @Test
    public void testSetStatusCode() {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testSetReasonPhrase() {
        HttpResponse response = new HttpResponse();
        response.setReasonPhrase("OK");
        assertEquals("OK", response.getReasonPhrase());
    }

    @Test
    public void testSetHeaders() {
        HttpResponse response = new HttpResponse();
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html");
        headers.put("Content-Length", "123");

        response.setHeaders(headers);

        assertEquals("text/html", response.getHeaders().get("Content-Type"));
        assertEquals("123", response.getHeaders().get("Content-Length"));
    }

    @Test
    public void testSetBody() {
        HttpResponse response = new HttpResponse();
        byte[] body = "Hello, world!".getBytes();
        response.setBody(body);
        assertArrayEquals(body, response.getBody());
    }

    @Test
    public void testGetResponseBytes() {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        response.setReasonPhrase("OK");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html");
        response.setHeaders(headers);
        byte[] body = "Hello, world!".getBytes();
        response.setBody(body);

        byte[] expectedHeaderBytes = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n".getBytes();
        byte[] expectedResponseBytes = new byte[expectedHeaderBytes.length + body.length];
        System.arraycopy(expectedHeaderBytes, 0, expectedResponseBytes, 0, expectedHeaderBytes.length);
        System.arraycopy(body, 0, expectedResponseBytes, expectedHeaderBytes.length, body.length);

        assertArrayEquals(expectedResponseBytes, response.getResponseBytes());
    }

    @Test
    public void testWrite() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        response.setReasonPhrase("OK");
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html");
        response.setHeaders(headers);
        byte[] body = "Hello, world!".getBytes();
        response.setBody(body);

        byte[] expectedHeaderBytes = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n".getBytes();
        byte[] expectedResponseBytes = new byte[expectedHeaderBytes.length + body.length];
        System.arraycopy(expectedHeaderBytes, 0, expectedResponseBytes, 0, expectedHeaderBytes.length);
        System.arraycopy(body, 0, expectedResponseBytes, expectedHeaderBytes.length, body.length);

        OutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);

        assertArrayEquals(expectedResponseBytes, ((ByteArrayOutputStream) outputStream).toByteArray());
    }
}