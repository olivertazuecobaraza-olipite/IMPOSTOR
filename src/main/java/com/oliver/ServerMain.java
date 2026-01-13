package com.oliver;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerMain {

    static List<ClientHandler> players = new ArrayList<>();
    static boolean partidaActiva = false;

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Servidor iniciado");

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                if (sc.nextLine().equalsIgnoreCase("START")) {
                    if (!partidaActiva) iniciarPartida();
                }
            }
        }).start();

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler ch = new ClientHandler(socket);
            players.add(ch);
            ch.start();
            System.out.println("Jugador conectado");
        }
    }

    static void iniciarPartida() {
        partidaActiva = true;

        List<ClientHandler> vivos = getVivos();
        Random r = new Random();
        ClientHandler impostor = vivos.get(r.nextInt(vivos.size()));
        impostor.impostor = true;

        for (ClientHandler c : vivos) {
            c.send("ROL:" + (c.impostor ? "IMPOSTOR" : "INOCENTE"));
        }

        enviarATodos("COMIENZA");
        cicloJuego();
    }

    static void cicloJuego() {
        List<String> candidatos = getNombresVivos();

        while (partidaActiva) {
            realizarVotacion(candidatos);
            comprobarFinal();
            candidatos = getNombresVivos();
        }
    }

    static void realizarVotacion(List<String> candidatos) {

        limpiarVotos();
        enviarATodos("VOTAR:" + String.join(",", candidatos));

        esperar(15000);

        Map<String, Integer> conteo = new HashMap<>();
        for (ClientHandler c : getVivos()) {
            if (c.voto != null) {
                conteo.put(c.voto, conteo.getOrDefault(c.voto, 0) + 1);
            }
        }

        if (conteo.isEmpty()) return;

        int max = Collections.max(conteo.values());
        List<String> empatados = new ArrayList<>();

        for (String nombre : conteo.keySet()) {
            if (conteo.get(nombre) == max) {
                empatados.add(nombre);
            }
        }

        if (empatados.size() > 1) {
            enviarATodos("EMPATE:" + String.join(",", empatados));
            realizarVotacion(empatados);
        } else {
            expulsarJugador(empatados.get(0));
        }
    }

    static void expulsarJugador(String nombre) {
        for (ClientHandler c : players) {
            if (c.nombre.equals(nombre) && c.vivo) {
                c.vivo = false;
                enviarATodos("EXPULSADO:" + nombre);

                if (c.impostor) {
                    enviarATodos("FIN:INOCENTES");
                    partidaActiva = false;
                }
                return;
            }
        }
    }

    static void comprobarFinal() {
        if (getVivos().size() == 2 && partidaActiva) {
            enviarATodos("FIN:IMPOSTOR");
            partidaActiva = false;
        }
    }

    static void enviarATodos(String msg) {
        for (ClientHandler c : players) {
            if (c.vivo) c.send(msg);
        }
    }

    static void limpiarVotos() {
        for (ClientHandler c : players) c.voto = null;
    }

    static List<ClientHandler> getVivos() {
        List<ClientHandler> vivos = new ArrayList<>();
        for (ClientHandler c : players) if (c.vivo) vivos.add(c);
        return vivos;
    }

    static List<String> getNombresVivos() {
        List<String> nombres = new ArrayList<>();
        for (ClientHandler c : getVivos()) nombres.add(c.nombre);
        return nombres;
    }

    static void esperar(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}

