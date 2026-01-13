package com.oliver;

import java.net.*;
import java.io.*;
import java.util.*;

public class ClientMain {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("localhost", 5000); // Cambiar la ip a la ne
        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        while (true) {
            String msg = in.readLine();
            if (msg == null) break;

            if (msg.equals("NOMBRE")) {
                System.out.print("Nombre: ");
                out.println(sc.nextLine());
            }

            if (msg.startsWith("ROL:")) {
                System.out.println("Tu rol es: " + msg.split(":")[1]);
            }
        }
    }
}

