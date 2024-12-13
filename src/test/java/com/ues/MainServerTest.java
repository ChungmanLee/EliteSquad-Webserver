// package com.ues;

// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;

// import static org.junit.jupiter.api.Assertions.assertTrue;

// public class MainServerTest {

//     private Thread httpServerThread;
//     private Thread secureServerThread;

//     @BeforeEach
//     void setUp() {
//         httpServerThread = new Thread(
//             new NioHttpServer()
//         );

//         secureServerThread = new Thread(
//             new SecureServer()
//         );
//         httpServerThread.start();
//         secureServerThread.start();
//     }

//     @Test
//     void testServerThreadsStarted() throws InterruptedException {
//         httpServerThread.join(5000);
//         secureServerThread.join(5000);

//         assertTrue(httpServerThread.isAlive(), "HTTP Server thread should be alive");
//         assertTrue(secureServerThread.isAlive(), "Secure Server thread should be alive");
//     }

//     @AfterEach
//     void tearDown() {
//         httpServerThread.interrupt();
//         secureServerThread.interrupt();
//     }
// }
