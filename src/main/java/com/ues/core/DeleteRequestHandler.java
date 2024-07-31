package com.ues.core;

import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpResponseUtil;
import reactor.core.publisher.Mono;

public class DeleteRequestHandler {

    public Mono<Void> handle(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        String condition = getConditionFromPath(path);
        String contentType = determineContentType(request);

        return ResourceManager.deleteData(getTableNameFromPath(path), condition)
                .flatMap(success -> {
                    if (success) {
                        return HttpResponseUtil.send200(response, "Data deleted successfully", contentType);
                    } else {
                        return HttpResponseUtil.send500(response, "Failed to delete data", contentType);
                    }
                })
                .onErrorResume(e -> HttpResponseUtil.send500(response, e.getMessage(), contentType));
    }

    private String getTableNameFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 1) {
            return parts[1];
        }
        throw new IllegalArgumentException("Invalid path: table name not found");
    }

    private String getConditionFromPath(String path) {
        String[] parts = path.split("/");
        return parts.length > 2 ? parts[2] : "1=1";
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
