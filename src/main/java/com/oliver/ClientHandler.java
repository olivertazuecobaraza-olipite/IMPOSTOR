package com.oliver;

import java.io.*;
import java.net.*;

public class ClientHandler extends Thread {

    Socket socket;
    PrintWriter out;
    BufferedReader in;

    String nombre;
    boolean impostor = false;
    boolean vivo = true;
    String voto;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    void send(String msg) {
        out.println(msg);
    }

    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            send("NOMBRE");
            nombre = in.readLine();

            while (vivo) {
                String msg = in.readLine();
                if (msg == null) break;

                if (msg.startsWith("VOTO:")) {
                    voto = msg.split(":")[1];
                }

                if (msg.equalsIgnoreCase("SALIR")) {
                    vivo = false;
                }
            }

        } catch (IOException e) {
            vivo = false;
        }
    }
}

