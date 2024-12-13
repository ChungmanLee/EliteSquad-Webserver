package com.ues.http;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private int statusCode;
    private String reasonPhrase;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body = new byte[0];

    
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers.clear();
        this.headers.putAll(headers);
    }

    public void setBody(byte[] body) {
        this.body = body != null ? body : new byte[0];
    }

    public byte[] getResponseBytes() {
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 ").append(statusCode).append(" ").append(reasonPhrase).append("\r\n");

        headers.forEach((key, value) -> response.append(key).append(": ").append(value).append("\r\n"));
        response.append("\r\n");

        byte[] headerBytes = response.toString().getBytes();
        byte[] responseBytes = new byte[headerBytes.length + body.length];

        System.arraycopy(headerBytes, 0, responseBytes, 0, headerBytes.length);
        System.arraycopy(body, 0, responseBytes, headerBytes.length, body.length);

        return responseBytes;
    }

    public void write(OutputStream outputStream) throws IOException {
        outputStream.write(getResponseBytes());
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }
}
