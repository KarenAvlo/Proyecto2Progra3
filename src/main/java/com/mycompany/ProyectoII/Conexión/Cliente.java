
package com.mycompany.ProyectoII.Conexión;

import com.mycompany.ProyectoII.Persona;
import com.mycompany.ProyectoII.control.Control;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Cliente {

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        String host = "localhost";

        try (
            Socket socket = new Socket(host, Protocolo.PUERTO);
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in)
        ) {
            System.out.println("Conectado al servidor.");
            System.out.println(entrada.readLine()); // mensaje de bienvenida
            
            
            // Hilo para escuchar mensajes del servidor
            new Thread(() -> {
                String respuesta;
                try {
                    while ((respuesta = entrada.readLine()) != null) {
                        System.out.println("Servidor: " + respuesta);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada por el servidor.");
                }
            }).start();

            System.out.println("Digite los mensajes para el servidor ('salir' para terminar):");

            while (true) {
                String mensaje = scanner.nextLine();
                salida.println(mensaje);
                if (mensaje.equalsIgnoreCase("salir")) {
                    break;
                }
            }

        } catch (IOException ex) {
            System.err.printf("Excepción: '%s'%n", ex.getMessage());
        }
    }

}
