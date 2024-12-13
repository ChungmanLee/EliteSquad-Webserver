package com.ues.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class HttpResponseUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private HttpResponseUtil() {
    }

    private static void addCORSHeaders(HttpResponse response) {
        response.getHeaders().put("Access-Control-Allow-Origin", "*");
        response.getHeaders().put("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.getHeaders().put("Access-Control-Allow-Headers", "Content-Type");
    }

    // Common response sender with content type
    public static Mono<Void> sendResponse(HttpResponse response, HttpStatus status, String bodyContent, String contentType) {
        response.setStatusCode(status.getCode());
        response.setReasonPhrase(status.getReasonPhrase());
        response.setHeaders(Map.of("Content-Type", contentType));
        addCORSHeaders(response);
        response.setBody(bodyContent.getBytes());
        return Mono.empty();
    }

    // Overloaded send200 method to handle List<Map<String, String>>
    public static Mono<Void> send200(HttpResponse response, List<Map<String, String>> bodyContent, String contentType) {
        try {
            String jsonString = objectMapper.writeValueAsString(bodyContent);
            return sendResponse(response, HttpStatus.OK, jsonString, contentType);
        } catch (JsonProcessingException e) {
            return send500(response, e.getMessage(), contentType);
        }
    }

    // 200 OK with customizable content type
    public static Mono<Void> send200(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.OK, bodyContent, contentType);
    }

    // 201 Created with customizable content type
    public static Mono<Void> send201(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.CREATED, bodyContent, contentType);
    }

    // 202 Accepted with customizable content type
    public static Mono<Void> send202(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.ACCEPTED, bodyContent, contentType);
    }

    // 203 Non-Authoritative Information with customizable content type
    public static Mono<Void> send203(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.NON_AUTHORITATIVE_INFORMATION, bodyContent, contentType);
    }

    // 204 No Content
    public static Mono<Void> send204(HttpResponse response) {
        return sendResponse(response, HttpStatus.NO_CONTENT, "", "text/html");
    }

    // 205 Reset Content with customizable content type
    public static Mono<Void> send205(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.RESET_CONTENT, bodyContent, contentType);
    }

    // 206 Partial Content with customizable content type
    public static Mono<Void> send206(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.PARTIAL_CONTENT, bodyContent, contentType);
    }

    // 207 Multi-Status with customizable content type
    public static Mono<Void> send207(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.MULTI_STATUS, bodyContent, contentType);
    }

    // 208 Already Reported with customizable content type
    public static Mono<Void> send208(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.ALREADY_REPORTED, bodyContent, contentType);
    }

    // 226 IM Used with customizable content type
    public static Mono<Void> send226(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.IM_USED, bodyContent, contentType);
    }

    // 301 Moved Permanently with customizable content type
    public static Mono<Void> send301(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.MOVED_PERMANENTLY, bodyContent, contentType);
    }

    // 302 Found with customizable content type
    public static Mono<Void> send302(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.FOUND, bodyContent, contentType);
    }

    // 303 See Other with customizable content type
    public static Mono<Void> send303(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.SEE_OTHER, bodyContent, contentType);
    }

    // 304 Not Modified with customizable content type
    public static Mono<Void> send304(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.NOT_MODIFIED, bodyContent, contentType);
    }

    // 307 Temporary Redirect with customizable content type
    public static Mono<Void> send307(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.TEMPORARY_REDIRECT, bodyContent, contentType);
    }

    // 308 Permanent Redirect with customizable content type
    public static Mono<Void> send308(HttpResponse response, String bodyContent, String contentType) {
        return sendResponse(response, HttpStatus.PERMANENT_REDIRECT, bodyContent, contentType);
    }

    // 400 Bad Request with customizable content type
    public static Mono<Void> send400(HttpResponse response, String message, String contentType) {
        String body = "<h1>400 Bad Request</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.BAD_REQUEST, body, contentType);
    }

    // 401 Unauthorized with customizable content type
    public static Mono<Void> send401(HttpResponse response, String message, String contentType) {
        String body = "<h1>401 Unauthorized</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.UNAUTHORIZED, body, contentType);
    }

    // 403 Forbidden with customizable content type
    public static Mono<Void> send403(HttpResponse response, String message, String contentType) {
        String body = "<h1>403 Forbidden</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.FORBIDDEN, body, contentType);
    }

    // 404 Not Found with customizable content type
    public static Mono<Void> send404(HttpResponse response, String message, String contentType) {
        String body = "<h1>404 Not Found</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.NOT_FOUND, body, contentType);
    }

    // 405 Method Not Allowed with customizable content type
    public static Mono<Void> send405(HttpResponse response, String contentType) {
        String body = "<h1>405 Method Not Allowed</h1><p>The HTTP method is not allowed.</p>";
        return sendResponse(response, HttpStatus.METHOD_NOT_ALLOWED, body, contentType);
    }

    // 409 Conflict with customizable content type
    public static Mono<Void> send409(HttpResponse response, String message, String contentType) {
        String body = "<h1>409 Conflict</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.CONFLICT, body, contentType);
    }

    // 500 Internal Server Error with customizable content type
    public static Mono<Void> send500(HttpResponse response, String message, String contentType) {
        String body = "<h1>500 Internal Server Error</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, body, contentType);
    }

    // 501 Not Implemented with customizable content type
    public static Mono<Void> send501(HttpResponse response, String message, String contentType) {
        String body = "<h1>501 Not Implemented</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.NOT_IMPLEMENTED, body, contentType);
    }

    // 502 Bad Gateway with customizable content type
    public static Mono<Void> send502(HttpResponse response, String message, String contentType) {
        String body = "<h1>502 Bad Gateway</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.BAD_GATEWAY, body, contentType);
    }

    // 503 Service Unavailable with customizable content type
    public static Mono<Void> send503(HttpResponse response, String message, String contentType) {
        String body = "<h1>503 Service Unavailable</h1><p>" + message + "</p>";
        return sendResponse(response, HttpStatus.SERVICE_UNAVAILABLE, body, contentType);
    }
}
