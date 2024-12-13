package com.ues.core;

import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpResponseUtil;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PutRequestHandler {

    public Mono<Void> handle(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        System.out.println(request.getBody() + " " + path); 
        Map<String, String> data = parseRequestBody(request.getBody());
        String condition = getConditionFromPath(path);
        String contentType = determineContentType(request);

        System.out.println("Received PUT request with data: " + data);

        return ResourceManager.updateData(getTableNameFromPath(path), data, condition)
                .flatMap(success -> {
                    if (success) {
                        return HttpResponseUtil.send200(response, "Data updated successfully", contentType);
                    } else {
                        return HttpResponseUtil.send404(response, "Data not found", contentType);
                    }
                })
                .onErrorResume(e -> {
                    System.out.println("Error updating data: " + e.getMessage());
                    return HttpResponseUtil.send500(response, e.getMessage(), contentType);
                });
    }

    private Map<String, String> parseRequestBody(String body) {
        Map<String, String> data = new HashMap<>();
        if (body != null && !body.isEmpty()) {
            String[] pairs = body.split("&");
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

    private String getConditionFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 3) {
            return "id=" + parts[3];
        }
        throw new IllegalArgumentException("Invalid path: ID not found");
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