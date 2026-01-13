package com.oliver;

import java.net.*;
import java.io.*;
import java.util.*;

public class ServerMain {

    static List<ClientHandler> players = new ArrayList<>();
    static boolean partidaActiva = false;
    static Random rm = new Random();
    static String[] palabras = {
    "casa", "perro", "gato", "coche", "ciudad", "persona", "tiempo", "día",
    "noche", "agua", "fuego", "tierra", "aire", "amor", "vida", "trabajo",
    "escuela", "libro", "mesa", "silla", "puerta", "ventana", "calle",
    "camino", "mar", "montaña", "sol", "luna", "estrella", "árbol",
    "flor", "fruta", "comida", "bebida", "familia", "amigo", "dinero",
    "música", "película", "juego", "deporte", "cuerpo", "mente",
    "idea", "problema", "solución", "historia", "futuro", "pasado",
    "pueblo", "estado", "país", "mundo", "universo", "espacio", "nube",
    "lluvia", "nieve", "viento", "bosque", "playa", "isla", "río",
    "lago", "puente", "edificio", "piso", "techo", "pared", "jardín",
    "parque", "plaza", "museo", "hospital", "cine", "teatro", "banco",
    "tienda", "mercado", "ropa", "zapato", "pantalón", "camisa", "reloj",
    "gafas", "bolsa", "maleta", "llave", "teléfono", "computadora", "radio",
    "televisión", "cámara", "papel", "lápiz", "pluma", "cuaderno", "sobre",
    "carta", "noticia", "idioma", "palabra", "frase", "pregunta", "respuesta",
    "número", "letra", "forma", "color", "rojo", "azul", "verde", "amarillo",
    "blanco", "negro", "gris", "café", "claro", "oscuro", "grande", "pequeño",
    "largo", "corto", "ancho", "estrecho", "alto", "bajo", "nuevo", "viejo",
    "joven", "mayor", "bueno", "malo", "mejor", "peor", "fácil", "difícil",
    "posible", "imposible", "cierto", "falso", "hermoso", "feo", "rico", "pobre",
    "lleno", "vacío", "caliente", "frío", "rápido", "lento", "fuerte", "débil",
    "feliz", "triste", "alegre", "enojado", "cansado", "enfermo", "sano", "seguro",
    "peligroso", "importante", "necesario", "especial", "común", "diferente", "igual", "libre",
    "paz", "guerra", "ley", "justicia", "gobierno", "política", "derecho", "deber",
    "arte", "ciencia", "salud", "energía", "fuerza", "poder", "miedo", "sueño",
    "verdad", "mentira", "razón", "sentido", "alma", "corazón", "sangre", "mano",
    "pie", "brazo", "pierna", "cabeza", "cara", "ojo", "oreja", "nariz",
    "boca", "diente", "pelo", "espalda", "pecho", "hombro", "dedo", "uña",
    "médico", "maestro", "estudiante", "policía", "cocinero", "conductor", "pintor", "escritor",
    "actor", "cantante", "juez", "abogado", "ingeniero", "jefe", "empleado", "vecino",
    "niño", "niña", "hombre", "mujer", "padre", "madre", "hijo", "hija",
    "hermano", "hermana", "abuelo", "abuela", "tío", "tía", "primo", "sobrino",
    "esposo", "pareja", "gente", "público", "grupo", "equipo", "clase", "club",
    "sociedad", "cultura", "tradición", "viaje", "vuelo", "tren", "barco", "bicicleta",
    "maletero", "rueda", "motor", "gasolina", "aceite", "hierro", "oro", "plata",
    "piedra", "madera", "cristal", "plástico", "papel", "cuero", "algodón", "seda"
};
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Servidor iniciado");

        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            while (true) {
                if (sc.nextLine().equalsIgnoreCase("START")) {
                    iniciarPartida();
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
    List<ClientHandler> vivos = getVivos();
    
    // VALIDACIÓN: Evita errores si no hay jugadores
    if (vivos.isEmpty()) {
        System.out.println("No hay jugadores para iniciar.");
        return;
    }

    partidaActiva = true;

    // 1. REINICIAR ESTADOS: Limpiar roles anteriores
    for (ClientHandler c : players) {
        c.impostor = false;
    }

    // 2. SELECCIONAR NUEVO IMPOSTOR
    String palabra = palabras[rm.nextInt(palabras.length)]; // Usar .length es más seguro que 250
    ClientHandler impostor = vivos.get(rm.nextInt(vivos.size()));
    impostor.impostor = true;

    // 3. ENVIAR ROLES
    for (ClientHandler c : vivos) {
        c.send("ROL:" + (c.impostor ? "IMPOSTOR" : "INOCENTE, palabra=" + palabra));
    }

    enviarATodos("COMIENZA");
}


    static void enviarATodos(String msg) {
        for (ClientHandler c : players) {
            if (c.vivo) c.send(msg);
        }
    }

    static List<ClientHandler> getVivos() {
        List<ClientHandler> vivos = new ArrayList<>();
        for (ClientHandler c : players) if (c.vivo) vivos.add(c);
        return vivos;
    }
}

