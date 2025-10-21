
package com.mycompany.ProyectoII.Conexión;

import com.mycompany.ProyectoII.control.Control;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente implements Runnable {
    private Socket socket;
    private Control control;
    private PrintWriter salida;
    private String usuario;

    public ManejadorCliente(Socket socket, Control control) {
        this.socket = socket;
        this.control = control;
    }

    public void enviarMensaje(String mensaje) {
        salida.println(mensaje);
    }

    @Override
    public void run() {
        try (
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            salida = new PrintWriter(socket.getOutputStream(), true);
            salida.println("Bienvenido al servidor multicliente!");

            String linea;
            while ((linea = entrada.readLine()) != null) {
                String[] partes = linea.split(":", 2);
                String comando = partes[0];

                switch (comando) {
                    case "LOGIN":
                        usuario = partes[1]; // ej: LOGIN:Karen
                        // Aquí validar usuario usando `control`
                        Servidor.notificarATodos("USUARIO_CONECTADO:" + usuario);
                        break;
                    case "LOGOUT":
                        Servidor.notificarATodos("USUARIO_DESCONECTADO:" + usuario);
                        Servidor.removerCliente(this);
                        socket.close();
                        return;
                    case "MENSAJE":
                        String[] msg = partes[1].split(";", 2); // destinatario;mensaje
                        String destinatario = msg[0];
                        String texto = msg[1];
                        Servidor.notificarATodos("MENSAJE_DE:" + usuario + ":" + texto);
                        break;
                    default:
                        salida.println("Comando no reconocido.");
                }
            }
        } catch (IOException ex) {
            System.err.println("Error con cliente: " + ex.getMessage());
        } finally {
            Servidor.removerCliente(this);
        }
    }
}
