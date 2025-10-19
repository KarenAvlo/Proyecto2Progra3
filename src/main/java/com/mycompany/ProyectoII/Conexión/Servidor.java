package com.mycompany.ProyectoII.Conexión;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Servidor {
    
    public static void main(String[] args) {

        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        try (
                ServerSocket serverSocket = new ServerSocket(Protocolo.PUERTO)) {
            System.out.println("Servidor iniciado. Esperando conexión..");

            while(true){ // Espera una conexión de un cliente
                    Socket socket = serverSocket.accept(); 
                System.out.println("Cliente conectado desde " + socket.getInetAddress());

              // Crear un hilo para atender a ese cliente
                new Thread(new ManejadorCliente(socket)).start();
            }

        } catch (IOException ex) {
            System.err.printf("Excepción: '%s'%n", ex.getMessage());
        }
    }
}
