
package com.mycompany.ProyectoII.Conexión;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ManejadorCliente  implements Runnable {

    private final Socket socket;

    public ManejadorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true)
        ) {
            salida.println("Bienvenido al servidor multicliente!");
            String mensaje;

            while ((mensaje = entrada.readLine()) != null) {
                System.out.printf("Mensaje de %s: '%s'%n", socket.getInetAddress(), mensaje);
                if (mensaje.equalsIgnoreCase("salir")) {
                    salida.println("Conexión cerrada. ¡Hasta luego!");
                    break;
                }
                // (opcional) enviar eco al cliente
                salida.println("Servidor recibió: " + mensaje);
            }

        } catch (IOException ex) {
            System.err.println("Error con cliente " + socket.getInetAddress() + ": " + ex.getMessage());
        } finally {
            try {
                socket.close();
                System.out.println("Cliente desconectado: " + socket.getInetAddress());
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket del cliente.");
            }
        }
    }
}
