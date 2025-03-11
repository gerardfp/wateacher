import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.crypto.SecretKey;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

public class Student {
    public static void main(String[] args) throws Exception {
        int port = 7654;

        // BufferedImage screenFullImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/screenshot", new ScreenshotHandler());
        server.createContext("/info", new InfoHandler());
        //server.setExecutor(null);
        server.start();
    }

    static class InfoHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");


            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // NO ES NECESARI EL OPTIONS
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                // Preflight request CORS
                exchange.sendResponseHeaders(204, -1);  // ¿200?
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String resp = "{\"username\": \"" + System.getProperty("user.name") + "\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, resp.length());
                    OutputStream os = exchange.getResponseBody();
                    os.write(resp.getBytes());
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            } else {
                exchange.sendResponseHeaders(405, -1);
            }
        }
    }

    static class ScreenshotHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equals(exchange.getRequestMethod())) {
                // Preflight request CORS
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    BufferedImage screenFullImage = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(screenFullImage, "png", baos);
                    byte[] imageBytes = baos.toByteArray();

                    exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                    exchange.sendResponseHeaders(200, imageBytes.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(imageBytes);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, -1);
                }
            } else {
                    exchange.sendResponseHeaders(405, -1);
            }
        }
    }
}
