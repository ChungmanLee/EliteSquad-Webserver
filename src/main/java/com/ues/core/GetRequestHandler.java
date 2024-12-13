package com.ues.core;

import com.ues.database.DatabaseConfig;
import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpResponseUtil;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class GetRequestHandler {

    private static Map<String, String> domainToRootMap;

    public GetRequestHandler() {
        
    }

    public Mono<Void> handle(HttpRequest request, HttpResponse response) {
        final String path = request.getPath();
        final String contentType = determineContentType(request);

        try {
            domainToRootMap = DatabaseConfig.loadConfigurationFromDatabase();
        } catch (IOException e) {
            e.printStackTrace();
            if(domainToRootMap == null)
            {
                domainToRootMap = new HashMap<>();
            }
        }

        if (path.startsWith("/data/")) {
            final String tableName = getTableNameFromPath(path);
            return ResourceManager.getData(tableName, "1=1")
                    .flatMap(messages -> HttpResponseUtil.send200(response, messages, contentType))
                    .onErrorResume(e -> HttpResponseUtil.send500(response, e.getMessage(), contentType));
        }

        return Mono.create(sink -> {
            String host = request.getHeader("Host").split(":")[0];
            String rootDir = domainToRootMap.get(host);
            if (rootDir == null) {
                HttpResponseUtil.send404(response, "Host not found: " + host, contentType)
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnTerminate(sink::success)
                    .subscribe();
                return;
            }

            String adjustedPath = path.equals("/") ? "/index.php" : path;
            System.out.println("Requested path: " + adjustedPath);
            File file = new File(rootDir, adjustedPath);
            System.out.println("File absolute path: " + file.getAbsolutePath());

            if (!file.exists()) {
                HttpResponseUtil.send404(response, "File not found: " + adjustedPath, contentType)
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnTerminate(sink::success)
                    .subscribe();
                return;
            }

            if (file.getName().endsWith(".php")) {
                executePhp(file, response)
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnTerminate(sink::success)
                    .subscribe();
            } else {
                sendResponse(file, response, request)
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnTerminate(sink::success)
                    .subscribe();
            }
        });
    }

    protected Mono<Void> sendResponse(File file, HttpResponse response, HttpRequest request) {
        return Mono.fromCallable(() -> {
            try {
                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    contentType = "text/plain";
                }
                byte[] content = Files.readAllBytes(file.toPath());
                return HttpResponseUtil.send200(response, new String(content), contentType);
            } catch (IOException e) {
                return HttpResponseUtil.send500(response, e.getMessage(), "text/plain");
            }
        }).flatMap(mono -> mono);
    }

    protected Mono<Void> executePhp(File file, HttpResponse response) {
        return Mono.fromCallable(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("php-cgi", file.getPath());
                Process process = pb.start();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try (InputStream inputStream = process.getInputStream()) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, length);
                    }
                }
                return HttpResponseUtil.send200(response, new String(outputStream.toByteArray()), "text/html");
            } catch (IOException e) {
                return HttpResponseUtil.send500(response, e.getMessage(), "text/html");
            }
        }).flatMap(mono -> mono);
    }

    private String getTableNameFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 2) {
            return parts[2];
        }
        throw new IllegalArgumentException("Invalid path: table name not found");
    }

    private String determineContentType(HttpRequest request) {
        String acceptHeader = (request != null) ? request.getHeader("Accept") : "";
        if(acceptHeader != null) {
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