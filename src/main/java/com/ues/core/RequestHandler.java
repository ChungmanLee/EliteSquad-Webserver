package com.ues.core;

import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import com.ues.http.HttpResponseUtil;
import reactor.core.publisher.Mono;

public class RequestHandler {

    private final GetRequestHandler getRequestHandler;
    private final PostRequestHandler postRequestHandler;
    private final PutRequestHandler putRequestHandler;
    private final DeleteRequestHandler deleteRequestHandler;

    public RequestHandler()  {
        this.getRequestHandler = new GetRequestHandler();
        this.postRequestHandler = new PostRequestHandler();
        this.putRequestHandler = new PutRequestHandler();
        this.deleteRequestHandler = new DeleteRequestHandler();
    }

    public Mono<Void> handle(HttpRequest request, HttpResponse response) {
        String method = request.getMethod();

        if (method == null) {
            return handleUnsupportedMethod(response);
        }
        
        System.out.println("Handling request method: " + method);
        System.out.println(request.getHeader("Content-Type"));
        System.out.println(request.getBody());
    
        switch (method) {
            case "GET":
                return getRequestHandler.handle(request, response)
                    .doOnSuccess(v -> System.out.println("Handled GET request successfully"))
                    .doOnError(e -> System.out.println("Error handling GET request: " + e.getMessage()));
            case "POST":
                return postRequestHandler.handle(request, response)
                    .doOnSuccess(v -> System.out.println("Handled POST request successfully"))
                    .doOnError(e -> System.out.println("Error handling POST request: " + e.getMessage()));
            case "PUT":
                return putRequestHandler.handle(request, response)
                    .doOnSuccess(v -> System.out.println("Handled PUT request successfully"))
                    .doOnError(e -> System.out.println("Error handling PUT request: " + e.getMessage()));
            case "DELETE":
                return deleteRequestHandler.handle(request, response)
                    .doOnSuccess(v -> System.out.println("Handled DELETE request successfully"))
                    .doOnError(e -> System.out.println("Error handling DELETE request: " + e.getMessage()));
            case "OPTIONS":
                return HttpResponseUtil.send204(response)
                        .doOnSuccess(v -> System.out.println("Handled OPTIONS request successfully"))
                        .doOnError(e -> System.out.println("Error handling OPTIONS request: " + e.getMessage()));
            default:
                return handleUnsupportedMethod(response)
                    .doOnSuccess(v -> System.out.println("Handled unsupported method"))
                    .doOnError(e -> System.out.println("Error handling unsupported method: " + e.getMessage()));
        }
    }

    private Mono<Void> handleUnsupportedMethod(HttpResponse response) {
        return HttpResponseUtil.send405(response, "text/html");
    }
}