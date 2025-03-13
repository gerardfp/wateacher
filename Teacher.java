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

    static final String page = """
    <!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Wateachers</title>
<style>
* { box-sizing: border-box; }

body { background-color: #f3f4f6; margin: 8px;}
:popover-open { background-color: black;}
input { border-radius: 0; }

#marco { display: flex; flex-wrap: wrap; gap: 0.4em; }

#marco img {
    object-fit: contain;
    width: 100%;
    height: 100%;
}

#full img {
    width: 80vw;
    height: 45vw;
    object-fit: contain;
}

.host {
    border: 1px solid black;
    display: grid;
    width: 320px;
    height: 180px;
    box-shadow: 0px 10px 15px -3px rgba(0, 0, 0, 0.211);
    border-radius: 4px;
    background-color: black;
}

.wide {
    width: 80vw;
    height: 45vw;
    margin-right: 20vw;
}

.host * { grid-column: 1; grid-row: 1; }

.host span {
    color:white;
    text-shadow: 1px 1px 2px black;
    background-color: #646464bb;
    align-self: start;
    justify-self: start;
    font-family: 'Courier New', Courier, monospace;
}

div.host:has(img[src=undefined]), div.host:has(img:not([src])) { display: none; }

#pi-button {
    font-size: 24px;
    position: absolute;
    bottom: 0.2em;
    right: 0.3em;
    cursor: pointer;
    background: none;
    border: none;
}

#config {
    display: flex; 
    flex-direction: column;
    background-color: black;
    color: greenyellow;
}
</style>
</head>

<div id="marco"></div>
<div id="full"></div>

<button id="pi-button" popovertarget="popover-config">π</button>
<div popover id="popover-config" style="_display: block;">
    <div id="config">
        <img id="mozart" src="data:image/png;base64,/9j/4AAQSkZJRgABAQIAdgB2AAD//gATQ3JlYXRlZCB3aXRoIEdJTVD/4gKwSUNDX1BST0ZJTEUAAQEAAAKgbGNtcwQwAABtbnRyUkdCIFhZWiAH6QADAAwAFQA3ADNhY3NwQVBQTAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLWxjbXMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA1kZXNjAAABIAAAAEBjcHJ0AAABYAAAADZ3dHB0AAABmAAAABRjaGFkAAABrAAAACxyWFlaAAAB2AAAABRiWFlaAAAB7AAAABRnWFlaAAACAAAAABRyVFJDAAACFAAAACBnVFJDAAACFAAAACBiVFJDAAACFAAAACBjaHJtAAACNAAAACRkbW5kAAACWAAAACRkbWRkAAACfAAAACRtbHVjAAAAAAAAAAEAAAAMZW5VUwAAACQAAAAcAEcASQBNAFAAIABiAHUAaQBsAHQALQBpAG4AIABzAFIARwBCbWx1YwAAAAAAAAABAAAADGVuVVMAAAAaAAAAHABQAHUAYgBsAGkAYwAgAEQAbwBtAGEAaQBuAABYWVogAAAAAAAA9tYAAQAAAADTLXNmMzIAAAAAAAEMQgAABd7///MlAAAHkwAA/ZD///uh///9ogAAA9wAAMBuWFlaIAAAAAAAAG+gAAA49QAAA5BYWVogAAAAAAAAJJ8AAA+EAAC2xFhZWiAAAAAAAABilwAAt4cAABjZcGFyYQAAAAAAAwAAAAJmZgAA8qcAAA1ZAAAT0AAACltjaHJtAAAAAAADAAAAAKPXAABUfAAATM0AAJmaAAAmZwAAD1xtbHVjAAAAAAAAAAEAAAAMZW5VUwAAAAgAAAAcAEcASQBNAFBtbHVjAAAAAAAAAAEAAAAMZW5VUwAAAAgAAAAcAHMAUgBHAEL/2wBDAFA3PEY8MlBGQUZaVVBfeMiCeG5uePWvuZHI////////////////////////////////////////////////////2wBDAVVaWnhpeOuCguv/////////////////////////////////////////////////////////////////////////wgARCADaANkDAREAAhEBAxEB/8QAFwABAQEBAAAA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">
        <hr>
        <label for="startip">Start IP:</label>
        <input id="startip">
        <label for="endip">End IP:</label>
        <input id="endip">
        <hr>
        <label for="endip">Refresh rate:</label>
        <input id="rate">
    </div>

</div>

<script>
    let startip = localStorage.getItem("startip") || "192.168.1.217";
    let endip = localStorage.getItem("endip") || "192.168.1.217";
    let rate = localStorage.getItem("rate") || 2000;
    $("startip").value = startip;
    $("endip").value = endip;
    $("rate").value = rate;
    $("startip").addEventListener("input", (e) => { window.localStorage.setItem("startip", e.target.value); });
    $("endip").addEventListener("input", (e) => { window.localStorage.setItem("endip", e.target.value); });
    $("rate").addEventListener("input", (e) => { window.localStorage.setItem("rate", e.target.value); });


    const startipNumber = ipToNumber(startip);
    const endipNumber = ipToNumber(endip);
    for(let i=startipNumber; i <= endipNumber; i++) {
        $("full").innerHTML += `<img popover id="full${i}" alt='Captura'>`
        $("marco").innerHTML += `
            <div class="host" id="host${i}" host="host${i}"
                onclick="host${i}.classList.toggle('wide'); this.scrollIntoView()"
                _onclick="full${i}.showPopover(); console.log('yeahh')"
                draggable=true ondragstart="drag(event)" ondrop=drop(event) ondragover="allowDrop(event)">
                    <img id='shot${i}' host="host${i}" alt='Captura'>
                    <span id="info${i}" host="host${i}"></span>
            </div>`;
        show(i);
    }

    async function show(n) {
        const ip = numberToIp(n);
        while(true){
            try {
                const username = (await (await fetch(`http://${ip}:7654/info`)).json()).username;
                const shot = URL.createObjectURL(new Blob([new DataView((await (await fetch(`http://${ip}:7654/screenshot`)).arrayBuffer()))], { type: "image/png" }));

                $(`shot${n}`).src = shot;
                $(`full${n}`).src = shot;
                $(`info${n}`).innerText = username;
            } catch(e) {
                $(`shot${n}`).removeAttribute('src');
                $(`full${n}`).removeAttribute('src');
                $(`info${n}`).innerText = "";
            }
            await sleep(rate);

        }
    }

    function allowDrop(e) { e.preventDefault(); }
    function drag(e) { e.dataTransfer.setData("host", e.target.getAttribute("host")); }
    function drop(e) { e.preventDefault(); e.currentTarget.before($(e.dataTransfer.getData("host")));}

    // UTILS
    function $(id){ return document.getElementById(id); };
    function sleep(ms) { return new Promise(resolve => setTimeout(resolve, ms)); }
    function ipToNumber(ip) { return ip.split('.').reduce((acc, octet) => (acc << 8) + parseInt(octet, 10), 0) >>> 0;}
    function numberToIp(num) { return [(num >>> 24) & 255, (num >>> 16) & 255, (num >>> 8) & 255, num & 255].join('.');}
</script>

</html>
""";

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

            //String page = Files.readString(Path.of("index.html"));
            byte[] contentBytes = page.getBytes();
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, contentBytes.length);
            exchange.getResponseBody().write(contentBytes);
            exchange.getResponseBody().close();
        }
    }
}
