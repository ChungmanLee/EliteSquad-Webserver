package com.ues.core;

import com.ues.database.DatabaseConfig;
import com.ues.database.ResourceManager;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpResponseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GetRequestHandlerTest {

    private GetRequestHandler getRequestHandler;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        getRequestHandler = spy(new GetRequestHandler());
    }

    @Test
    void handleFileRequest_fileExists() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getHeader("Host")).thenReturn("example.com");
        when(request.getPath()).thenReturn("/index.html");
        when(request.getHeader("Accept")).thenReturn("text/html");

        Path filePath = tempDir.resolve("index.html");
        Files.createFile(filePath);

        Map<String, String> domainToRootMap = new HashMap<>();
        domainToRootMap.put("example.com", tempDir.toString());

        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class)) {
            mockedConfig.when(DatabaseConfig::loadConfigurationFromDatabase).thenReturn(domainToRootMap);

            doReturn(Mono.empty()).when(getRequestHandler).sendResponse(any(File.class), any(HttpResponse.class), any(HttpRequest.class));

            Mono<Void> result = getRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            verify(getRequestHandler).sendResponse(any(File.class), eq(response), eq(request));
        }
    }

    @Test
    void handleFileRequest_fileNotExists() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getHeader("Host")).thenReturn("example.com");
        when(request.getPath()).thenReturn("/nonexistent.html");
        when(request.getHeader("Accept")).thenReturn("text/html");

        Map<String, String> domainToRootMap = new HashMap<>();
        domainToRootMap.put("example.com", tempDir.toString());

        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class);
             MockedStatic<HttpResponseUtil> mockedUtil = mockStatic(HttpResponseUtil.class)) {
            mockedConfig.when(DatabaseConfig::loadConfigurationFromDatabase).thenReturn(domainToRootMap);

            mockedUtil.when(() -> HttpResponseUtil.send404(any(HttpResponse.class), anyString(), anyString()))
                      .thenReturn(Mono.empty());

            Mono<Void> result = getRequestHandler.handle(request, response);

            StepVerifier.create(result)
                        .verifyComplete();

            mockedUtil.verify(() -> HttpResponseUtil.send404(eq(response), eq("File not found: /nonexistent.html"), eq("text/html")));
        }
    }

    @Test
    void handlePhpFileRequest_phpFileExists() throws Exception {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getHeader("Host")).thenReturn("example.com");
        when(request.getPath()).thenReturn("/index.php");
        when(request.getHeader("Accept")).thenReturn("text/html");

        Path filePath = tempDir.resolve("index.php");
        Files.createFile(filePath);

        Map<String, String> domainToRootMap = new HashMap<>();
        domainToRootMap.put("example.com", tempDir.toString());

        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class)) {
            mockedConfig.when(DatabaseConfig::loadConfigurationFromDatabase).thenReturn(domainToRootMap);

            doReturn(Mono.empty()).when(getRequestHandler).executePhp(any(File.class), any(HttpResponse.class));

            Mono<Void> result = getRequestHandler.handle(request, response);

            StepVerifier.create(result)
                    .verifyComplete();

            verify(getRequestHandler).executePhp(any(File.class), eq(response));
        }
    }

    @Test
    void handleHostNotFound() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getHeader("Host")).thenReturn("nonexistent.com");
        when(request.getPath()).thenReturn("/index.html");
        when(request.getHeader("Accept")).thenReturn("text/html");

        Map<String, String> domainToRootMap = new HashMap<>();

        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class);
             MockedStatic<HttpResponseUtil> mockedUtil = mockStatic(HttpResponseUtil.class)) {
            mockedConfig.when(DatabaseConfig::loadConfigurationFromDatabase).thenReturn(domainToRootMap);

            mockedUtil.when(() -> HttpResponseUtil.send404(any(HttpResponse.class), anyString(), anyString()))
                      .thenReturn(Mono.empty());

            Mono<Void> result = getRequestHandler.handle(request, response);

            StepVerifier.create(result)
                        .verifyComplete();

            mockedUtil.verify(() -> HttpResponseUtil.send404(eq(response), eq("Host not found: nonexistent.com"), eq("text/html")));
        }
    }

    @Test
    void handleDynamicDataRequest_success() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getHeader("Host")).thenReturn("example.com");
        when(request.getPath()).thenReturn("/data/chat");
        when(request.getHeader("Accept")).thenReturn("application/json");

        List<Map<String, String>> mockData = List.of(
                Map.of("name", "John", "message", "Hello"),
                Map.of("name", "Jane", "message", "Hi")
        );

        Map<String, String> domainToRootMap = new HashMap<>();
        domainToRootMap.put("example.com", tempDir.toString());

        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class);
             MockedStatic<ResourceManager> mockedResourceManager = mockStatic(ResourceManager.class);
             MockedStatic<HttpResponseUtil> mockedHttpResponseUtil = mockStatic(HttpResponseUtil.class)) {

            mockedConfig.when(DatabaseConfig::loadConfigurationFromDatabase).thenReturn(domainToRootMap);

            mockedResourceManager.when(() -> ResourceManager.getData(anyString(), anyString()))
                                 .thenReturn(Mono.just(mockData));

            mockedHttpResponseUtil.when(() -> HttpResponseUtil.send200(any(HttpResponse.class), anyList(), anyString()))
                                  .thenReturn(Mono.empty());

            Mono<Void> result = getRequestHandler.handle(request, response);

            StepVerifier.create(result).verifyComplete();

            mockedResourceManager.verify(() -> ResourceManager.getData(eq("chat"), eq("1=1")));
            mockedHttpResponseUtil.verify(() -> HttpResponseUtil.send200(eq(response), eq(mockData), eq("application/json")));
        }
    }

    @Test
    void handleDynamicDataRequest_failure() {
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = new HttpResponse();
        when(request.getHeader("Host")).thenReturn("example.com");
        when(request.getPath()).thenReturn("/data/chat");
        when(request.getHeader("Accept")).thenReturn("application/json");

        Map<String, String> domainToRootMap = new HashMap<>();
        domainToRootMap.put("example.com", tempDir.toString());

        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class);
             MockedStatic<ResourceManager> mockedResourceManager = mockStatic(ResourceManager.class);
             MockedStatic<HttpResponseUtil> mockedHttpResponseUtil = mockStatic(HttpResponseUtil.class)) {

            mockedConfig.when(DatabaseConfig::loadConfigurationFromDatabase).thenReturn(domainToRootMap);

            mockedResourceManager.when(() -> ResourceManager.getData(anyString(), anyString()))
                                 .thenReturn(Mono.error(new RuntimeException("Database error")));
                                 mockedHttpResponseUtil.when(() -> HttpResponseUtil.send500(any(HttpResponse.class), anyString(), anyString()))
                                 .thenReturn(Mono.empty());

           Mono<Void> result = getRequestHandler.handle(request, response);

           StepVerifier.create(result)
                       .verifyComplete();

           mockedResourceManager.verify(() -> ResourceManager.getData(eq("chat"), eq("1=1")));
           mockedHttpResponseUtil.verify(() -> HttpResponseUtil.send500(eq(response), anyString(), eq("application/json")));
       }
   }
}




