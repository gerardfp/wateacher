import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Teacher {

    public static void main(String[] args) throws Exception {
        int port = 7655;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new HtmlHandler());
        server.start();
        System.out.println("Servidor HTTP corriendo en http://localhost:" + port);
    }

    static class HtmlHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {

            String content = Files.readString(Path.of("index.html"));
            byte[] contentBytes = content.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, contentBytes.length);
            exchange.getResponseBody().write(contentBytes);
            exchange.getResponseBody().close();
        }
    }
}
