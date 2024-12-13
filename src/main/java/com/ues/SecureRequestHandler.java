package com.ues;

import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import com.ues.core.RequestHandler;
import com.ues.http.HttpRequest;
import com.ues.http.HttpResponse;
import reactor.core.publisher.Mono;

public class SecureRequestHandler implements Runnable {
    private final SSLSocket sslSocket;
    private final RequestHandler handler;

    public SecureRequestHandler(SSLSocket sslSocket, RequestHandler handler) {
        this.sslSocket = sslSocket;
        this.handler = handler;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
             OutputStream out = sslSocket.getOutputStream()) {

            StringBuilder requestBuilder = new StringBuilder();
            String line;
            int contentLength = 0;

            System.out.println("Started processing a new request.");

            line = in.readLine();
            if (line != null) {
                requestBuilder.append(line).append("\r\n");
                String[] requestLineParts = line.split(" ");
                String method = requestLineParts[0];
                String path = requestLineParts[1];
                String version = requestLineParts[2];

                while ((line = in.readLine()) != null && !line.isEmpty()) {
                    requestBuilder.append(line).append("\r\n");
                    if (line.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(line.split(":")[1].trim());
                    }
                }

                System.out.println("Request headers read. Content-Length: " + contentLength);

                char[] body = new char[contentLength];
                if (contentLength > 0) {
                    in.read(body, 0, contentLength);
                    requestBuilder.append(body);
                }

                String fullRequest = requestBuilder.toString();
                System.out.println("Request body read: " + new String(body));

                HttpRequest httpRequest = new HttpRequest();
                httpRequest.setRequestLine(method, path, version);
                if (contentLength > 0) {
                    httpRequest.setBody(new String(body));
                }

                String[] headerLines = fullRequest.split("\r\n\r\n")[0].split("\r\n");
                for (String headerLine : headerLines) {
                    if (headerLine.contains(": ")) {
                        String[] headerParts = headerLine.split(": ");
                        httpRequest.addHeader(headerParts[0], headerParts[1]);
                    }
                }

                HttpResponse response = new HttpResponse();

                System.out.println("Complete request: " + fullRequest);

                Mono<Void> result = handler.handle(httpRequest, response);

                result.doOnSuccess(v -> {
                    try {
                        byte[] responseBytes = response.getResponseBytes();
                        out.write(responseBytes);
                        out.flush();
                        System.out.println("Response sent successfully.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).doFinally(signalType -> {
                    try {
                        sslSocket.close();
                        System.out.println("SSL socket closed.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).block();
            }
        } catch (SSLHandshakeException e){
            System.err.println("SSL handshake failed: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}