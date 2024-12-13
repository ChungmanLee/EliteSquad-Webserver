package com.ues.core;

import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpResponseUtil;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PostRequestHandler {

    public Mono<Void> handle(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        System.out.println(request.getBody() + " " + path);
        Map<String, String> data = parseRequestBody(request.getBody());
        String contentType = determineContentType(request);

        System.out.println("Received POST request with data: " + data);

        return ResourceManager.createData(getTableNameFromPath(path), data)
                .flatMap(success -> {
                    if (success) {
                        System.out.println("Data stored successfully.");
                        return HttpResponseUtil.send201(response, "Data created successfully", contentType);
                    } else {
                        System.out.println("Failed to store data.");
                        return HttpResponseUtil.send409(response, "Failed to create data", contentType);
                    }
                })
                .onErrorResume(e -> {
                    System.out.println("Error storing data: " + e.getMessage());
                    return HttpResponseUtil.send500(response, e.getMessage(), contentType);
                });
    }

    public static Map<String, String> parseRequestBody(String body) {
        Map<String, String> data = new HashMap<>();

        if (body != null && !body.isEmpty()) {
            String[] pairs = body.split("&");

            System.out.println("Pairs:");
            for (String pair : pairs) {
                System.out.println(pair);
            }

            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    try {
                        String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                        data.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        System.err.println("UnsupportedEncodingException: " + e.getMessage());
                    }
                }
            }
        }

        return data;
    }

    private String getTableNameFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 2) {
            return parts[2];
        }
        throw new IllegalArgumentException("Invalid path: table name not found");
    }

    private String determineContentType(HttpRequest request) {
        String acceptHeader = request.getHeader("Accept");
        if (acceptHeader != null) {
            if (acceptHeader.contains("application/json")) {
                return "application/json";
            }
            if (acceptHeader.contains("text/html")) {
                return "text/html";
            }
        }
        return "text/plain";
    }
}