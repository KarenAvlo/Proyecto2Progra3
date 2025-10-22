package com.mycompany.ProyectoII.Conexión;

import com.mycompany.ProyectoII.vista.VentanaAdministrador;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author minar
 */
public class ServiceProxy {

    private Socket socket;
    private PrintWriter salida;
    private BufferedReader entrada;
    private List<String> usuariosActivos = new ArrayList<>();
    private List<String> mensajesPendientes = new ArrayList<>();

    public ServiceProxy(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        salida = new PrintWriter(socket.getOutputStream(), true);
        entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Hilo para recibir notificaciones
        new Thread(() -> {
            try {
                String linea;
                while ((linea = entrada.readLine()) != null) {
                    if (linea.startsWith("USUARIO_CONECTADO:")) {
                        String usuario = linea.split(":")[1];
                        if (!usuariosActivos.contains(usuario)) {
                            usuariosActivos.add(usuario);
                        }
                        System.out.println(usuario + " se conectó");
                    } else if (linea.startsWith("USUARIO_DESCONECTADO:")) {
                        String usuario = linea.split(":")[1];
                        usuariosActivos.remove(usuario);
                        System.out.println(usuario + " se desconectó");
                    } else if (linea.startsWith("MENSAJE_DE:")) {
                        String[] partes = linea.split(":", 3); // "MENSAJE_DE", remitente, texto
                        String remitente = partes[1];
                        String texto = partes[2];
                        mensajesPendientes.add(remitente + ": " + texto); // guardamos mensaje
                    }
                }
            } catch (IOException e) {
                System.out.println("Conexión cerrada.");
            }
        }).start();
    }

    public void login(String usuario) {
        salida.println("LOGIN:" + usuario);
    }

    public void logout(String usuario) {
        salida.println("LOGOUT:" + usuario);
    }

    public List<String> getUsuariosActivos() {
        return usuariosActivos;
    }

    public void enviarMensaje(String destinatario, String mensaje) {
        salida.println("MENSAJE:" + destinatario + ";" + mensaje);
    }

    public List<String> getMensajesPendientes() {
        return new ArrayList<>(mensajesPendientes);
    }

    public void limpiarMensajesPendientes() {
        mensajesPendientes.clear();
    }

}
