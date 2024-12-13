package com.ues;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import com.ues.core.RequestHandler;

public class SecureServer implements Runnable {

    private static final int PORT = 8443;

    @Override
    public void run() {
        try {
            SSLServerSocketFactory sslServerSocketFactory = SSLConfiguration.getServerSocketFactory();
            SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(PORT);

            System.out.println("Secure NIO HTTPS Server is listening on port " + PORT);

            RequestHandler handler = new RequestHandler();

            while (true) {
                SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
                new Thread(new SecureRequestHandler(sslSocket, handler)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}