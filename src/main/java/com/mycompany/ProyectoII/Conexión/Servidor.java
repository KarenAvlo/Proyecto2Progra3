package com.mycompany.ProyectoII.Conexión;

import com.mycompany.ProyectoII.control.Control;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    private static List<ManejadorCliente> clientesActivos = new ArrayList<>();
    private static Control control;

    public static void main(String[] args) {

        try {
            control = new Control();
        } catch (SQLException ex) {
            System.err.println("Error al inicializar Control: " + ex.getMessage());
            return; // salimos porque no se puede continuar
        }

        try (ServerSocket serverSocket = new ServerSocket(Protocolo.PUERTO)) {
            System.out.println("Servidor iniciado. Esperando conexiones...");
            while (true) {
                Socket socket = serverSocket.accept();
                ManejadorCliente mc = new ManejadorCliente(socket, control);
                clientesActivos.add(mc);
                new Thread(mc).start();
            }
        } catch (IOException ex) {
            System.err.println("Excepción: " + ex.getMessage());
        }
    }

    public static void notificarATodos(String mensaje) {
        for (ManejadorCliente mc : clientesActivos) {
            mc.enviarMensaje(mensaje);
        }
    }

    public static void removerCliente(ManejadorCliente mc) {
        clientesActivos.remove(mc);
    }
}
