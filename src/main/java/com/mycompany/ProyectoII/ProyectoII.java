package com.mycompany.ProyectoII;

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
* Proyecto 1                                                          |
*                                                                     |
* 2-0816-0954; Avilés López, Karen Minards                            |
* 4-0232-0641; Zárate Hernández, Nicolas Alfredo                      |
*                                                                     |
* versión 1.0.0 13-09-2005                                            |
*                                                                     |
* --------------------------------------------------------------------+
*/
public class ProyectoII {

    public static void main(String[] args) throws IOException, JAXBException, Exception {
        try {
            System.setOut(new PrintStream(
                    new FileOutputStream(FileDescriptor.out), true,
                    StandardCharsets.UTF_8.name()));

            // 👉 Aquí aplicas FlatLaf en lugar del L&F del sistema
            //UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatDarkLaf());
            // Si quieres el tema claro:
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
                ex.printStackTrace();
            }
        });
    }

    private void mostrarInterfaz() throws Exception {
        System.out.println("Iniciando interfaz..");

        // 1️⃣ Crear modelo
        Modelo modelo = new Modelo();

        // 2️⃣ Cargar personas desde XML
        //modelo.obtenerModelo().cargarDatos();
        
//        modelo.cargarDatos();
//        
        // 3️⃣ Crear controlador con modelo
        Control gestorPrincipal = new Control(modelo);
        
        // 4️⃣ Crear ventana principal y pasar controlador
        VentanaPrincipal ventana = new VentanaPrincipal(gestorPrincipal);
        ventana.setVisible(true);
    }
        
}


    