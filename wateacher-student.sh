#!/bin/bash

cat << EOF > /usr/local/bin/Student.java
import com.sun.net.httpserver.HttpServer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;

public class Student {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(args.length > 0 ? Integer.parseInt(args[0]) : 7654), 0);
        
        server.createContext("/screenshot", exchange -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("OPTIONS".equals(exchange.getRequestMethod())) { // Preflight request CORS
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // https://forum.snapcraft.io/t/pipewire-doesnt-work-in-snaps/40235/37
                    ImageIO.write(new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize())), "png", baos);
                    byte[] imageBytes = baos.toByteArray();
                    exchange.getResponseHeaders().set("Content-Type", "application/octet-stream");
                    exchange.sendResponseHeaders(200, imageBytes.length);
                    exchange.getResponseBody().write(imageBytes);
                    exchange.getResponseBody().close();
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                }
            }
        });

        server.createContext("/info", exchange -> {
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

            if ("OPTIONS".equals(exchange.getRequestMethod())) { // Preflight request CORS ¿Necessari?
                exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
                exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
                exchange.sendResponseHeaders(204, -1);  // ¿200?
                return;
            }

            if ("GET".equals(exchange.getRequestMethod())) {
                try {
                    String resp = "{\"username\": \"" + System.getProperty("user.name") + "\"}";
                    exchange.getResponseHeaders().set("Content-Type", "application/json");
                    exchange.sendResponseHeaders(200, resp.length());
                    exchange.getResponseBody().write(resp.getBytes());
                    exchange.getResponseBody().close();
                } catch (Exception e) {
                    exchange.sendResponseHeaders(500, -1);
                }
            }
        });

        server.start();
    }
}
EOF

#mkdir -p bin
#javac -d bin "Student.java"
#if [ $? -ne 0 ]; then
#    exit 2
#fi
#echo "Main-Class: Student > manifest.txt
#jar cfm "Student.jar" manifest.txt -C bin .
#rm manifest.txt
#cp Student.jar /usr/local/bin/

cat << EOF > /usr/local/bin/wateacher-student
cd /usr/local/bin/
java Student.java
EOF
chmod +x /usr/local/bin/wateacher-student

mkdir -p /etc/xdg/autostart/
cat << EOF > /etc/xdg/autostart/wateacher-student.desktop
[Desktop Entry]
Type=Application
Exec=wateacher-student
Hidden=false
NoDisplay=false
X-GNOME-Autostart-enabled=true
Name[es_ES]=Wateacher Student
Name=Wateacher Student
Comment[es_ES]=
Comment=
EOF
