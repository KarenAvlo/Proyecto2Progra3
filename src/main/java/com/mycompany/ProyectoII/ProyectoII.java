package com.mycompany.ProyectoII;

import com.mycompany.ProyectoII.Conexión.Servidor;
import com.mycompany.ProyectoII.control.Control;
import com.mycompany.ProyectoII.vista.VentanaPrincipal;
import com.mycompany.ProyectoII.modelo.Modelo;
import jakarta.xml.bind.JAXBException;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/* -------------------------------------------------------------------+
*                                                                     |
* (c) 2025                                                            |
* EIF206 - Programación 3                                             |
* 2do ciclo 2025                                                      |
* NRC 51189 – Grupo 05                                                |
* Proyecto 2                                                          |
*                                                                     |
* 2-0816-0954; Avilés López, Karen Minards                            |
* 4-0232-0641; Zárate Hernández, Nicolas Alfredo                      |
*                                                                     |
* versión 2.0.0 06-11-2025                                            |
*                                                                     |
* --------------------------------------------------------------------+
 */
public class ProyectoII {

    public static void main(String[] args) throws IOException, JAXBException, Exception {
        try {
            System.setOut(new PrintStream(
                    new FileOutputStream(FileDescriptor.out), true,
                    StandardCharsets.UTF_8.name()));

            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());

            JFrame.setDefaultLookAndFeelDecorated(true);
        } catch (UnsupportedEncodingException | UnsupportedLookAndFeelException ex) {
            System.err.printf("Excepción: '%s'%n", ex.getMessage());
        }
        new ProyectoII().init();
        System.out.println("Aplicación inicializada..");
    }

    private void init() {
        SwingUtilities.invokeLater(() -> {
            try {
                mostrarInterfaz();
            } catch (Exception ex) {

            }
        });
    }

    private void mostrarInterfaz() throws Exception {
        new Thread(() -> {
            try {
                Servidor.main(new String[]{}); // corre el servidor dentro del mismo proceso
            } catch (Exception e) {

            }
        }).start();

        // Luego iniciar la interfaz
        Thread.sleep(1000); // pequeña pausa para dar tiempo a que el servidor arranque
        Modelo modelo = new Modelo();
        Control gestorPrincipal = new Control(modelo);
        VentanaPrincipal ventana = new VentanaPrincipal(gestorPrincipal);
        ventana.setVisible(true);
    }

}
    