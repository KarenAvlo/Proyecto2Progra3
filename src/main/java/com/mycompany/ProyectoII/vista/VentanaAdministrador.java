package com.mycompany.ProyectoII.vista;

import com.mycompany.ProyectoII.Administrativo;
import com.mycompany.ProyectoII.Farmaceuta;
import com.mycompany.ProyectoII.Indicaciones;
import com.mycompany.ProyectoII.Medicamento;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Paciente;
import com.mycompany.ProyectoII.Persona;
import com.mycompany.ProyectoII.Receta;
import com.mycompany.ProyectoII.control.Control;
import com.mycompany.ProyectoII.modelo.modelo;
import cr.ac.una.gui.FormHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.kordamp.ikonli.swing.FontIcon;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;

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

public class VentanaAdministrador extends javax.swing.JFrame {

    public VentanaAdministrador(Control controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser null");
        }
        this.controlador = controlador;
        this.estado = new FormHandler();
        initComponents();
        this.setLocationRelativeTo(null); // aparece en el centro
        init();
    }

    public void init() {
        // ====== DocumentListener para campos de edición ======
        DocumentListener listenerEdicion = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                indicarCambios();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                indicarCambios();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                indicarCambios();
            }
        };

        // ====== DocumentListener para campos de búsqueda ======
        DocumentListener listenerBusqueda = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarControles();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarControles();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarControles();
            }
        };

        // ====== Campos Médicos ======
        campoId.getDocument().addDocumentListener(listenerEdicion);
        campoId1.getDocument().addDocumentListener(listenerEdicion);
        campoId2.getDocument().addDocumentListener(listenerEdicion);

        cedulatxt1.getDocument().addDocumentListener(listenerBusqueda);
        ResultadoMtxt.getDocument().addDocumentListener(listenerBusqueda);

        // ====== Campos Farmaceutas ======
        CedulaFtxt.getDocument().addDocumentListener(listenerEdicion);
        NombreFtxt.getDocument().addDocumentListener(listenerEdicion);

        CedulaFtxt2.getDocument().addDocumentListener(listenerBusqueda);
        ResultadoFtxt.getDocument().addDocumentListener(listenerBusqueda);

        // ====== Campos Pacientes ======
        CedulaPtxt.getDocument().addDocumentListener(listenerEdicion);
        NombrePtxt.getDocument().addDocumentListener(listenerEdicion);
        FechaNacPtxt.getDocument().addDocumentListener(listenerEdicion);
        TelefonoPtxt.getDocument().addDocumentListener(listenerEdicion);

        CedulaPtxt2.getDocument().addDocumentListener(listenerBusqueda);
        ResultadoPtxt.getDocument().addDocumentListener(listenerBusqueda);

        // ====== Campos Medicamentos ======
        //  Campos de edición activan botones de edición
        CodigoMtxt.getDocument().addDocumentListener(listenerEdicion);
        NombreMedicamentotxt.getDocument().addDocumentListener(listenerEdicion);
        PresentacionMedicamentotxt.getDocument().addDocumentListener(listenerEdicion);

        // Campos de búsqueda solo activan botones de búsqueda
        CodigoMtxt2.getDocument().addDocumentListener(listenerBusqueda);
        ResultadoMedicamentotxt.getDocument().addDocumentListener(listenerBusqueda);
        // ====== Actualizar tablas al cambiar pestaña ======
        jTabbedPane1.addChangeListener(e -> {
            int index = jTabbedPane1.getSelectedIndex();
            actualizarControles(); // Mantener botones sincronizados
            actualizarCampos();
            switch (index) {
                case 0:
                    actualizarTablaMedicos();
                    break;
                case 1:
                    actualizarTablaFarmaceutas();
                    break;
                case 2:
                    actualizarTablaPacientes();
                    break;
                case 3:
                    actualizarTablaMedicamentos();
                    estado.changeToAddMode();
                    break;
                case 5:
                    actualizarTablaRecetas();
                    break;
            }
            actualizarControles(); // Mantener botones sincronizados
        });

        // ====== Inicializar tablas ======
        actualizarTablaMedicos();
        actualizarTablaFarmaceutas();
        actualizarTablaPacientes();
        actualizarTablaMedicamentos();
        actualizarTablaRecetas();

        // ====== Cambiar a modo agregar al abrir ======
        cambiarModoAgregar();
        actualizarComponentes();
        configurarSpinnersDashboard();
        cargarMedicamentosComboBox();
        asignarIconosPestanas();
        // ====== Mostrar ventana ======
        setVisible(true);
    }

    // -------------------------------------------------------------------------
    // MÉTODOS DE MODOS
    // -------------------------------------------------------------------------
    private void cambiarModoVista() {
        estado.changeToViewMode();
        actualizarComponentes();
        estado.setModified(false);
    }

    private void cambiarModoAgregar() {
        estado.changeToAddMode();
        actualizarCampos();
        actualizarComponentes();

        int pestanaSeleccionada = jTabbedPane1.getSelectedIndex();
        switch (pestanaSeleccionada) {
            case 0 -> {
                // Médicos
                campoId.requestFocusInWindow();
                campoId.selectAll();
            }
            case 1 -> {
                // Farmacéutas
                CedulaFtxt.requestFocusInWindow();
                CedulaFtxt.selectAll();
            }
            case 3 -> {
                // Medicamentos
                CodigoMtxt.requestFocusInWindow();
                CodigoMtxt.selectAll();
            }
        }
    }

    private void cambiarModoBuscar() {
        estado.changeToSearchMode();
        actualizarComponentes();

        int pestanaSeleccionada = jTabbedPane1.getSelectedIndex();
        switch (pestanaSeleccionada) {
            case 0:
                ResultadoMtxt.requestFocusInWindow();
                ResultadoMtxt.selectAll();
                break;
            case 1:
                CedulaFtxt2.requestFocusInWindow();
                CedulaFtxt2.selectAll();
                break;
            case 2:
                CedulaPtxt2.requestFocusInWindow();
                CedulaPtxt2.selectAll();
                break;
            case 3:
                CodigoMtxt2.requestFocusInWindow();
                CodigoMtxt2.selectAll();
                break;

        }
    }

    // -------------------------------------------------------------------------
    // ACTUALIZACIÓN DE COMPONENTES
    // -------------------------------------------------------------------------
    private void actualizarComponentes() {
        actualizarControles();
        actualizarCampos();
    }

    private void actualizarControles() {
        int pestanaSeleccionada = jTabbedPane1.getSelectedIndex();

        switch (pestanaSeleccionada) {
            case 0 -> {
                // Médicos
                boolean NohaytextoMedicoid = campoId.getText().trim().isEmpty();
                boolean NohaytextoMedicoid1 = campoId1.getText().trim().isEmpty();
                boolean NohaytextoMedicoid2 = campoId2.getText().trim().isEmpty();
                boolean NohaytextoMedico2 = cedulatxt1.getText().trim().isEmpty();
                boolean NohaytextoM3 = ResultadoMtxt.getText().trim().isEmpty();

                BotonGuardarMedico.setEnabled(!estado.isViewing() && estado.isModified() || estado.isViewing());
                BotonLimpiarMedico.setEnabled(!NohaytextoMedico2 || !NohaytextoMedicoid || !NohaytextoMedicoid1 || !NohaytextoMedicoid2 || !NohaytextoM3);
                BotonEliminarMedico.setEnabled(!NohaytextoMedicoid);
                BotonBuscarMedico.setEnabled(!NohaytextoMedico2);

                BotonLimpiarMedico.setText((!NohaytextoMedicoid || !NohaytextoMedicoid1 || !NohaytextoMedicoid2|| !NohaytextoMedicoid2 || !NohaytextoM3) ? "Limpiar" : "Cancelar");
            }

            case 1 -> {
                // Farmacéutas
                boolean noHayTextoFarma = CedulaFtxt.getText().trim().isEmpty();
                boolean noHayTextoFarma2 = NombreFtxt.getText().trim().isEmpty();
                boolean noHayTextoFarma3 = CedulaFtxt2.getText().trim().isEmpty();
                boolean noHayTextoFarma4 = ResultadoFtxt.getText().trim().isEmpty();
                
                // Botones habilitados según estado.isModified y contenido de los campos
                BotonGuardarF1.setEnabled(!estado.isViewing() && estado.isModified() || estado.isViewing());
                BotonLimpiarF.setEnabled(!noHayTextoFarma || !noHayTextoFarma2 || !noHayTextoFarma3||!noHayTextoFarma4);
                BotonEliminarF.setEnabled(!noHayTextoFarma);
                BotonBuscarF.setEnabled(!noHayTextoFarma3);
                BotonLimpiarF.setText((!noHayTextoFarma || !noHayTextoFarma2|| !noHayTextoFarma3||!noHayTextoFarma4) ? "Limpiar" : "Cancelar");
            }

            case 2 -> {
                // Pacientes
                boolean NohayTextoPac = CedulaPtxt.getText().trim().isEmpty();
                boolean NohaytextoPac2 = NombrePtxt.getText().trim().isEmpty();
                boolean NohayTextoPac3 = FechaNacPtxt.getText().trim().isEmpty();
                boolean NohaytextoPac4 = TelefonoPtxt.getText().trim().isEmpty();
                boolean NohaytextoPac5 = CedulaPtxt2.getText().trim().isEmpty();
                boolean NohaytextoPac6 = ResultadoPtxt.getText().trim().isEmpty();             
                BotonBuscarP.setEnabled(!NohaytextoPac5);
                BotonEliminarP.setEnabled(!NohayTextoPac);
                BotonGuardarP.setEnabled(!estado.isViewing() && estado.isModified() || estado.isViewing());
                BotonLimpiarP.setEnabled(!NohayTextoPac || !NohaytextoPac2
                        || !NohayTextoPac3 || !NohaytextoPac4 ||!NohaytextoPac5||! NohaytextoPac6);
            }

            case 3 -> {
                // Medicamentos
                boolean noHayTextoMedic = CodigoMtxt.getText().trim().isEmpty();
                boolean noHayTextoMedic2 = NombreMedicamentotxt.getText().trim().isEmpty();
                boolean noHayTextoMedic3 = PresentacionMedicamentotxt.getText().trim().isEmpty();
                boolean noHayTextoMedicBusqueda = CodigoMtxt2.getText().trim().isEmpty();
                boolean noHayTextoResuBusqueda = ResultadoMedicamentotxt.getText().trim().isEmpty();
                BotonGuardarMedicamento.setEnabled(!estado.isViewing() && estado.isModified() || estado.isViewing());
                BotonLimpiarMedicamento.setEnabled(!noHayTextoMedic || !noHayTextoMedic2 || !noHayTextoMedic3||
                        !noHayTextoMedicBusqueda||!noHayTextoResuBusqueda);
                BotonEliminarMedicamento.setEnabled(!noHayTextoMedic);
                BotonBuscarMedicamento.setEnabled(!noHayTextoMedicBusqueda);

                BotonLimpiarMedicamento.setText((!noHayTextoMedic || !noHayTextoMedic2 || !noHayTextoMedic3
                        ||!noHayTextoMedicBusqueda||!noHayTextoResuBusqueda) ? "Limpiar" : "Cancelar");
            }
        }
    }

    private void actualizarCampos() {
        int pestanaSeleccionada = jTabbedPane1.getSelectedIndex();
        // Declarar todas las variables de modoEdicion antes del switch
        boolean modoEdicionMed = !estado.isViewing();
        boolean modoEdicionFarma = !estado.isViewing();
        boolean modoEdicionPac = !estado.isViewing();

        switch (pestanaSeleccionada) {
            case 0 -> {
                // Médicos
                campoId.setEnabled(estado.getModel() == null || modoEdicionMed || estado.isModified());
                campoId1.setEnabled(estado.getModel() == null || modoEdicionMed || estado.isModified());
                campoId2.setEnabled(estado.getModel() == null || modoEdicionMed || estado.isModified());
                cedulatxt1.setEnabled(true);
                ResultadoMtxt.setEnabled(false);
            }

            case 1 -> {
                // Farmaceutas
                CedulaFtxt.setEnabled(estado.getModel() == null || modoEdicionFarma || estado.isModified());
                NombreFtxt.setEnabled(estado.getModel() == null || modoEdicionFarma || estado.isModified());
                CedulaFtxt2.setEnabled(true);
                ResultadoFtxt.setEnabled(false);
            }

            case 2 -> {
                // Pacientes
                CedulaPtxt.setEnabled(estado.getModel() == null || modoEdicionPac || estado.isModified());
                NombrePtxt.setEnabled(estado.getModel() == null || modoEdicionPac || estado.isModified());
                FechaNacPtxt.setEnabled(estado.getModel() == null || modoEdicionPac || estado.isModified());
                TelefonoPtxt.setEnabled(estado.getModel() == null || modoEdicionPac || estado.isModified());
                CedulaPtxt2.setEnabled(true);
                ResultadoPtxt.setEnabled(false);
            }

            case 3 -> {
                // Medicamentos

                CodigoMtxt.setEnabled(estado.getModel() == null || !estado.isViewing() || estado.isModified());
                NombreMedicamentotxt.setEnabled(estado.getModel() == null || !estado.isViewing() || estado.isModified());
                PresentacionMedicamentotxt.setEnabled(estado.getModel() == null || !estado.isViewing() || estado.isModified());
                CodigoMtxt2.setEnabled(true);           // siempre editable para búsqueda
                ResultadoMedicamentotxt.setEnabled(false);
            }

        }
    }

    private void indicarCambios() {
        estado.setModified(true);
        actualizarControles();
    }

    private void limpiarCampos() {
        int pestanaSeleccionada = jTabbedPane1.getSelectedIndex();
        switch (pestanaSeleccionada) {
            case 0 -> {
                estado.setModel(null);
                campoId.setText("");
                campoId1.setText("");
                campoId2.setText("");
                cedulatxt1.setText("");
                ResultadoMtxt.setText("");
                cambiarModoAgregar();
                estado.setModified(true);
                actualizarComponentes();
            }
            case 1 -> {
                // Farmaceutas
                estado.setModel(null);
                estado.setModified(true); // Reinicia el estado para que botones no se habiliten solos
                CedulaFtxt.setText("");
                NombreFtxt.setText("");
                CedulaFtxt2.setText("");
                ResultadoFtxt.setText("");
                cambiarModoAgregar();
                actualizarComponentes(); // Fuerza actualización de botones
            }

            case 2 -> {
                //limpia pacientes
                estado.setModel(null);
                CedulaPtxt.setText("");
                NombrePtxt.setText("");
                FechaNacPtxt.setText("");
                TelefonoPtxt.setText("");
                CedulaPtxt2.setText("");
                ResultadoPtxt.setText("");
                cambiarModoAgregar();
                estado.setModified(true);
                actualizarComponentes();
            }
            case 3 -> {
                //limpia medicamentos
                estado.setModel(null);
                CodigoMtxt.setText("");
                NombreMedicamentotxt.setText("");
                PresentacionMedicamentotxt.setText("");
                CodigoMtxt2.setText("");
                ResultadoMedicamentotxt.setText("");
                cambiarModoAgregar();
                estado.setModified(true);
                actualizarComponentes();
            }

        }
    }
    // -------------------------------------------------------------------------
    // OPERACIONES CRUD
    // -------------------------------------------------------------------------
    //----------------Medicos----------------

    private void guardarMedico() {
//        try {
//            String cedula = campoId.getText().trim();
//            String nombre = campoId1.getText().trim();
//            String especialidad = campoId2.getText().trim();
//
//            if (cedula.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            boolean exito;
//            if (estado.isAdding()) {
//                exito = controlador.agregarMedico(cedula, nombre, especialidad);
//            } else if (estado.isEditing()) {
//                // Para edición, primero eliminamos y luego agregamos )
//                controlador.eliminarMedico(((Medico) estado.getModel()).getCedula());
//                exito = controlador.agregarMedico(cedula, nombre, especialidad);
//            } else {
//                exito = false;
//            }
//
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Médico guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                estado.setModel(null);     // Establece el modelo como nulo para indicar un nuevo registro
//                estado.changeToAddMode();
//                limpiarCampos();
//                actualizarComponentes();
//                actualizarTablaMedicos();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al guardar médico, ya existe esa cédula,", "Error", JOptionPane.WARNING_MESSAGE);
//            }
//        } catch (HeadlessException ex) {
//            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//            logger.log(java.util.logging.Level.SEVERE, "Error al guardar médico", ex);
//        }
    }

    private void buscarMedico() {
//        if (!estado.isSearching()) {
//            String cedula = cedulatxt1.getText().trim();
//            if (cedula.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Ingrese una cédula para buscar", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            Medico medico = controlador.buscarMedico(cedula);
//            if (medico != null) {
//                ResultadoMtxt.setText(medico.getNombre());
//                estado.setModel(null);
//                cambiarModoVista();
//                actualizarComponentes();
//                JOptionPane.showMessageDialog(this, "Médico encontrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(this, "No se encontró el médico con esa cédula", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } else {
//            cambiarModoBuscar();
//        }
    }

    private void eliminarMedico() {
//        String cedula = campoId.getText().trim();
//        Medico medico = controlador.buscarMedico(cedula);
//        if (medico == null) {
//            JOptionPane.showMessageDialog(this, "No hay médico seleccionado", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        int confirmacion = JOptionPane.showConfirmDialog(this,
//                "¿Está seguro de eliminar al médico " + medico.getNombre() + "?",
//                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
//        if (confirmacion == JOptionPane.YES_OPTION) {
//            boolean exito = controlador.eliminarMedico(medico.getCedula());
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Médico eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                estado.setModel(null);
//                cambiarModoVista();
//                actualizarControles();
//                actualizarTablaMedicos();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al eliminar el médico", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }

    private void cancelarOperacion() {
        cambiarModoVista();
    }

    private void actualizarTablaMedicos() {
//        try {
//            List<Medico> medicos = controlador.listarMedicos();
//            DefaultTableModel modelo = (DefaultTableModel) TablaMedicos.getModel();
//            modelo.setRowCount(0);
//            if (medicos != null) {
//                for (Medico m : medicos) {
//                    modelo.addRow(new Object[]{
//                        m.getCedula(),
//                        m.getNombre(),
//                        m.getEspecialidad()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            logger.log(java.util.logging.Level.SEVERE, "Error al actualizar tabla de médicos", ex);
//            JOptionPane.showMessageDialog(this, "Error al cargar los médicos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void cargarMedicoDesdeTabla() {
//        int selectedRow = TablaMedicos.getSelectedRow();
//        if (selectedRow >= 0 && estado.isViewing()) {
//            String cedula = TablaMedicos.getValueAt(selectedRow, 0).toString();
//            Medico medico = controlador.buscarMedico(cedula);
//            if (medico != null) {
//                estado.setModel(medico);
//                actualizarComponentes();
//            }
//        }
    }

    //================Fin Medicos===============
    ////--------------Farmaceutas----------------

private void guardarFarmaceuta() {
//        try {
//            String cedula = CedulaFtxt.getText().trim();
//            String nombre = NombreFtxt.getText().trim();
//
//            if (cedula.isEmpty() || nombre.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Nombre y cédula son obligatorios");
//                return;
//            }
//            boolean exito;
//            if (estado.isAdding()) {
//                exito = controlador.agregarFarmaceuta(cedula, nombre);
//            } else if (estado.isEditing()) {
//                controlador.eliminarFarmaceuta(((Farmaceuta) estado.getModel()).getCedula());
//                exito = controlador.agregarFarmaceuta(cedula, nombre);
//            } else {
//                exito = false;
//            }
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Farmaceuta guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                estado.setModel(null);
//                estado.changeToAddMode();
//                limpiarCampos();
//                actualizarComponentes();
//                actualizarTablaFarmaceutas();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al guardar Farmaceuta, ya existe cédula", "Error", JOptionPane.WARNING_MESSAGE);
//            }
//        } catch (HeadlessException ex) {
//            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void actualizarTablaFarmaceutas() {
//        try {
//            List<Farmaceuta> farmaceutas = controlador.ListarFarmaceutas();
//            DefaultTableModel modelo = (DefaultTableModel) TablaFarmaceutas.getModel();
//            modelo.setRowCount(0);
//            if (farmaceutas != null) {
//                for (Farmaceuta f : farmaceutas) {
//                    modelo.addRow(new Object[]{
//                        f.getCedula(),
//                        f.getNombre()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Error al cargar los farmaceutas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void cargarFarmaceutaDesdeTabla() {
//        int selectedRow = TablaFarmaceutas.getSelectedRow();
//        if (selectedRow >= 0) {
//            String cedula = TablaFarmaceutas.getValueAt(selectedRow, 0).toString();
//            Farmaceuta farmaceuta = controlador.buscarFarmaceuta(cedula);
//            if (farmaceuta != null) {
//                estado.setModel(farmaceuta);
//                actualizarComponentes();
//            }
//        }
    }

    private void eliminarFarmaceuta() {
//        String cedula = CedulaFtxt.getText().trim();
//        if (cedula.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Ingrese una cédula para eliminar", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        Farmaceuta farma = controlador.buscarFarmaceuta(cedula);
//        if (farma == null) {
//            JOptionPane.showMessageDialog(this, "No se encontró el farmaceuta", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        int confirmacion = JOptionPane.showConfirmDialog(this,
//                "¿Está seguro de eliminar al farmaceuta con cédula " + cedula + "?",
//                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
//        if (confirmacion == JOptionPane.YES_OPTION) {
//            boolean exito = controlador.eliminarFarmaceuta(cedula);
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Farmaceuta eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                estado.setModel(null);
//                estado.changeToAddMode();
//                limpiarCampos();
//                actualizarComponentes();
//                actualizarTablaFarmaceutas();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al eliminar el farmaceuta. No se encontró la cédula.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }

    public void buscarFarmaceuta() {
//        if (!estado.isSearching()) {
//            String cedula = CedulaFtxt2.getText().trim();
//            if (cedula.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Ingrese una cédula para buscar");
//                return;
//            }
//            Farmaceuta farma = controlador.buscarFarmaceuta(cedula);
//            if (farma != null) {
//                ResultadoFtxt.setText(farma.getNombre());
//                estado.setModel(farma);
//                cambiarModoVista();
//                actualizarComponentes();
//                JOptionPane.showMessageDialog(this, "Farmaceuta encontrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(this, "No se encontró el farmaceuta con esa cédula", "Error", JOptionPane.ERROR_MESSAGE);
//                ResultadoFtxt.setText("No se encontró.");
//            }
//        } else {
//            cambiarModoBuscar();
//        }
    }

    //=========Pacientes=========
    private void guardarPaciente() {
//        try {
//            String cedula = CedulaPtxt.getText().trim();
//            String nombre = NombrePtxt.getText().trim();
//            String numeroTel = TelefonoPtxt.getText().trim();
//            String fechaNac = FechaNacPtxt.getText().trim();
//            if (cedula.isEmpty() || nombre.isEmpty()
//                    || numeroTel.isEmpty() || fechaNac.isEmpty()) { //verificacion que no sean vacios
//                JOptionPane.showMessageDialog(this, "Todos los datos son obligatorios");
//                return;
//            }
//            boolean exito;
//            if (estado.isAdding()) { //si esta añadiendo en el field,entonces añadalo
//                exito = controlador.agregarPaciente(cedula, nombre, fechaNac, numeroTel);
//            } else if (estado.isEditing()) { // si está editando los espacios
//                //eliminamos lo que está en proceso
//                controlador.EliminarPaciente(((Paciente) estado.getModel()).getCedula());
//                //luego añadimos
//                exito = controlador.agregarPaciente(cedula, nombre, fechaNac, numeroTel);
//            } else {
//                exito = false;
//            }
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Paciente guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                cambiarModoVista();
//                estado.setModel(null);     // Establece el modelo como nulo para indicar un nuevo registro
//                estado.changeToAddMode();
//                limpiarCampos();
//                actualizarComponentes();
//                actualizarTablaPacientes();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al guardar al Paciente, ya existe esa cédula", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } catch (HeadlessException ex) {
//            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void actualizarTablaPacientes() {
//        try {
//            List<Paciente> Pacientes = controlador.ListarPacientes();
//            DefaultTableModel modelo = (DefaultTableModel) TablaPacientes.getModel();
//            modelo.setRowCount(0);
//            if (Pacientes != null) {
//                for (Paciente P : Pacientes) {
//                    modelo.addRow(new Object[]{
//                        P.getCedula(),
//                        P.getNombre(),
//                        P.getFechaNacimiento(),
//                        P.getTelefono()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(null, "Error al cargar los Pacientes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void cargarPacientesDesdeTabla() {
//        int selectedRow = TablaPacientes.getSelectedRow();
//        if (selectedRow >= 0) {
//            String cedula = TablaPacientes.getValueAt(selectedRow, 0).toString();
//            Paciente Pacx = controlador.buscarPaciente(cedula);
//            if (Pacx != null) {
//                estado.setModel(Pacx);
//                actualizarComponentes();
//
//            }
//        }
    }

    private void EliminarPaciente() {
//        String cedula = CedulaPtxt.getText().trim();
//        Paciente Pacx = controlador.buscarPaciente(cedula);
//        if (Pacx == null) {
//            JOptionPane.showMessageDialog(this, "No hay Paciente seleccionado", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        if (cedula.isEmpty()) {
//            JOptionPane.showMessageDialog(null, "Ingrese una cedula a eliminar", "Error", JOptionPane.OK_OPTION);
//            return;
//        }
//        int confirmacion = JOptionPane.showConfirmDialog(null,
//                "¿Está seguro de eliminar al paciente con cédula " + cedula + "?",
//                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
//        if (confirmacion == JOptionPane.YES_OPTION) {
//            boolean exito = controlador.EliminarPaciente(cedula);
//            if (exito) {
//                JOptionPane.showMessageDialog(null, "Paciente eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                // Opcional: limpiar los campos y actualizar la tabla
//                cambiarModoVista();
//                actualizarComponentes();
//                limpiarCampos();
//                actualizarTablaPacientes();
//            } else {
//                JOptionPane.showMessageDialog(null, "Error al eliminar el Paciente. No se encontró la cédula.", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }

    public void buscarPaciente() {
//        if (!estado.isSearching()) {
//            String Cedula = CedulaPtxt2.getText().trim();
//
//            if (Cedula.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Ingrese una cedula para buscar");
//                return;
//            }
//            Paciente Pacx = controlador.buscarPaciente(Cedula);
//            if (Pacx != null) {
//                ResultadoPtxt.setText(Pacx.getNombre());
//                estado.setModel(null);
//                cambiarModoVista();
//                actualizarComponentes();
//                JOptionPane.showMessageDialog(null, "Paciente encontrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//            } else {
//                JOptionPane.showMessageDialog(null, "No se encontró el Paciente con esa cédula", "Error", JOptionPane.ERROR_MESSAGE);
//                ResultadoPtxt.setText("No se encontró.");
//            }
//        } else {
//            cambiarModoBuscar();
//        }

    }
    //==============Medicamentos================

    private void guardarMedicamento() {
//        try {
//            String codigo = CodigoMtxt.getText().trim();
//            String nombre = NombreMedicamentotxt.getText().trim();
//            String presentacion = PresentacionMedicamentotxt.getText().trim();
//            if (codigo.isEmpty() || nombre.isEmpty() || presentacion.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Todos los datos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            boolean exito;
//            if (estado.isAdding()) {
//                exito = controlador.agregarMedicamento(codigo, nombre, presentacion);
//            } else if (estado.isEditing()) {
//                controlador.EliminarMedicamento(((Medicamento) estado.getModel()).getCodigo());
//                exito = controlador.agregarMedicamento(codigo, nombre, presentacion);
//            } else {
//                exito = false;
//            }
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Medicamento guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                estado.setModel(null);
//                estado.changeToAddMode(); 
//                limpiarCampos();
//                actualizarComponentes();
//                actualizarTablaMedicamentos();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al guardar medicamento", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//
//        } catch (HeadlessException ex) {
//            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void EliminarMedicamento() {
//        String codigo = CodigoMtxt.getText().trim(); // usar campo de búsqueda para eliminar
//
//        if (codigo.isEmpty()) {
//            JOptionPane.showMessageDialog(this, "Ingrese un código a eliminar", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        Medicamento med = controlador.buscarMedicamento(codigo);
//        if (med == null) {
//            JOptionPane.showMessageDialog(this, "No se encontró el medicamento", "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        int confirm = JOptionPane.showConfirmDialog(this,
//                "¿Está seguro de eliminar el medicamento con código " + codigo + "?",
//                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
//
//        if (confirm == JOptionPane.YES_OPTION) {
//            boolean exito = controlador.EliminarMedicamento(codigo);
//            if (exito) {
//                JOptionPane.showMessageDialog(this, "Medicamento eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
//                estado.setModel(null);
//                estado.changeToAddMode();    
//                limpiarCampos();
//                actualizarComponentes();
//                actualizarTablaMedicamentos();
//            } else {
//                JOptionPane.showMessageDialog(this, "Error al eliminar medicamento", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }

    private void buscarMedicamento() {
//        if (!estado.isSearching()) {
//            String codigo = CodigoMtxt2.getText().trim();
//            if (codigo.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "Ingrese un código para buscar", "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            Medicamento med = controlador.buscarMedicamento(codigo);
//            if (med != null) {
//                ResultadoMedicamentotxt.setText(med.getNombre());
//                cambiarModoVista();           // mostrar datos en modo vista
//                actualizarComponentes();
//            } else {
//                ResultadoMedicamentotxt.setText("No se encontró.");
//                JOptionPane.showMessageDialog(this, "No se encontró el medicamento", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } else {
//            cambiarModoBuscar();
//        }
    }

    private void actualizarTablaMedicamentos() {
//        try {
//            List<Medicamento> lista = controlador.ListarMedicamentos();
//            DefaultTableModel modelo = (DefaultTableModel) TablaMedicamentos.getModel();
//            modelo.setRowCount(0);
//            if (lista != null) {
//                for (Medicamento m : lista) {
//                    modelo.addRow(new Object[]{m.getCodigo(), m.getNombre(), m.getPresentacion()});
//                }
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this, "Error al cargar los medicamentos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void cargarMedicamentoDesdeTabla() {
//        int fila = TablaMedicamentos.getSelectedRow();
//        if (fila >= 0) {
//            String codigo = TablaMedicamentos.getValueAt(fila, 0).toString();
//            Medicamento med = controlador.buscarMedicamento(codigo);
//            if (med != null) {
//                estado.setModel(med);
//                cambiarModoVista();
//                actualizarComponentes();
//            }
//        }
    }

//=====================RECETAS====================
    private void cargarRecetaDesdeTabla() {
//        int fila = TablaRecetas.getSelectedRow();
//        if (fila >= 0) {
//            String codigo = TablaRecetas.getValueAt(fila, 0).toString();
//            Receta receta = controlador.buscarReceta(codigo);
//
//            if (receta != null) {
//                estado.setModel(receta);  
//                cambiarModoVista();         
//                actualizarComponentes();  
//                DefaultTableModel modelo = (DefaultTableModel) TablaIndicaciones.getModel();
//                modelo.setRowCount(0);
//                for (Indicaciones ind : receta.getIndicaciones()) {
//                    modelo.addRow(new Object[]{
//                        ind.getMedicamento().getNombre(),
//                        ind.getCantidad(),
//                        ind.getIndicaciones(),
//                        ind.getDuracion()
//                    });
//                }
//            }
//        }
    }

    private void actualizarTablaRecetas() {
//        try {
//            List<Receta> recetas = controlador.ListarRecetas();
//            DefaultTableModel modelo = (DefaultTableModel) TablaRecetas.getModel();
//            modelo.setRowCount(0); 
//            if (recetas != null) {
//                for (Receta r : recetas) {
//                    modelo.addRow(new Object[]{
//                        r.getCodReceta(),
//                        r.getPaciente() != null ? r.getPaciente().getNombre() : "Sin paciente",
//                        r.getMedico() != null ? r.getMedico().getNombre() : "Sin médico",
//                        r.getFechaEmision(),
//                        r.getFechaRetiro() != null ? r.getFechaRetiro() : "No retirado",
//                        r.getEstado()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            JOptionPane.showMessageDialog(this,
//                    "Error al cargar las recetas: " + ex.getMessage(),
//                    "Error",
//                    JOptionPane.ERROR_MESSAGE);
//        }
    }

    private void mostrarIndicacionesReceta(Receta receta) {
        DefaultTableModel modelo = (DefaultTableModel) TablaIndicaciones.getModel();
        modelo.setRowCount(0); // limpiar la tabla
        if (receta != null && receta.getIndicaciones() != null) {
            for (Indicaciones i : receta.getIndicaciones()) {
                modelo.addRow(new Object[]{
                    i.getMedicamento() != null ? i.getMedicamento().getNombre() : "Sin medicamento",
                    i.getCantidad(),
                    i.getIndicaciones(),
                    i.getDuracion()
                });
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        PestañaMedicos = new javax.swing.JPanel();
        buscartxt = new javax.swing.JPanel();
        ResultadoMtxt = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        BotonBuscarMedico = new javax.swing.JButton();
        Jlabel7 = new javax.swing.JLabel();
        cedulatxt1 = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        campoId = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        campoId1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        campoId2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        BotonGuardarMedico = new javax.swing.JButton();
        BotonLimpiarMedico = new javax.swing.JButton();
        BotonEliminarMedico = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablaMedicos = new javax.swing.JTable();
        PestañaFarmaceutas = new javax.swing.JPanel();
        PanelIngresaFarm = new javax.swing.JPanel();
        LabelCedulaF = new javax.swing.JLabel();
        LabelNombreF = new javax.swing.JLabel();
        CedulaFtxt = new javax.swing.JTextField();
        NombreFtxt = new javax.swing.JTextField();
        BotonEliminarF = new javax.swing.JButton();
        BotonLimpiarF = new javax.swing.JButton();
        BotonGuardarF1 = new javax.swing.JButton();
        PanelBusquedaF = new javax.swing.JPanel();
        LabelCedulaFB1 = new javax.swing.JLabel();
        ResultadoFtxt = new javax.swing.JTextField();
        LabelResultadoF2 = new javax.swing.JLabel();
        CedulaFtxt2 = new javax.swing.JTextField();
        BotonBuscarF = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaFarmaceutas = new javax.swing.JTable();
        PestañaPacientes = new javax.swing.JPanel();
        PanelIngresaFarm1 = new javax.swing.JPanel();
        LabelCedulaP1 = new javax.swing.JLabel();
        LabelNombreF1 = new javax.swing.JLabel();
        CedulaPtxt = new javax.swing.JTextField();
        NombrePtxt = new javax.swing.JTextField();
        BotonEliminarP = new javax.swing.JButton();
        BotonLimpiarP = new javax.swing.JButton();
        BotonGuardarP = new javax.swing.JButton();
        LabelNombreP2 = new javax.swing.JLabel();
        FechaNacPtxt = new javax.swing.JTextField();
        LabelTeltxt = new javax.swing.JLabel();
        TelefonoPtxt = new javax.swing.JTextField();
        PanelBusquedaF1 = new javax.swing.JPanel();
        LabelCedulaFB2 = new javax.swing.JLabel();
        ResultadoPtxt = new javax.swing.JTextField();
        LabelResultadoF3 = new javax.swing.JLabel();
        CedulaPtxt2 = new javax.swing.JTextField();
        BotonBuscarP = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TablaPacientes = new javax.swing.JTable();
        PestañaMedicamentos = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        CodigoMtxt = new javax.swing.JTextField();
        LabelCodigoM = new javax.swing.JLabel();
        NombreMedicamentotxt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        PresentacionMedicamentotxt = new javax.swing.JTextField();
        LabelPresentacionM = new javax.swing.JLabel();
        BotonGuardarMedicamento = new javax.swing.JButton();
        BotonLimpiarMedicamento = new javax.swing.JButton();
        BotonEliminarMedicamento = new javax.swing.JButton();
        buscartxt1 = new javax.swing.JPanel();
        ResultadoMedicamentotxt = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        BotonBuscarMedicamento = new javax.swing.JButton();
        Jlabel8 = new javax.swing.JLabel();
        CodigoMtxt2 = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        TablaMedicamentos = new javax.swing.JTable();
        PestañaDashboard = new javax.swing.JPanel();
        PanelDatos = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        AñoInicio = new javax.swing.JSpinner();
        AñoFin = new javax.swing.JSpinner();
        DiaMesInicio = new javax.swing.JSpinner();
        DiaMesFin = new javax.swing.JSpinner();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jComboBoxMedicamentos = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        tblMedicamentosGrafico = new javax.swing.JTable();
        BotonSeleccionFechas = new javax.swing.JButton();
        BotonAgregarMedicamentoComboBox = new javax.swing.JButton();
        PanelMedicamentos = new javax.swing.JPanel();
        PanelRecetas = new javax.swing.JPanel();
        PestañaHistorico = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        TablaRecetas = new javax.swing.JTable();
        BotonVerIndicaciones = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        TablaIndicaciones = new javax.swing.JTable();
        PestañaAcercaDe = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Administrador");

        jTabbedPane1.setToolTipText("");
        jTabbedPane1.setName("Admisni"); // NOI18N
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(900, 600));

        buscartxt.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Búsqueda", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        buscartxt.setLayout(new java.awt.GridBagLayout());

        ResultadoMtxt.setEnabled(false);
        ResultadoMtxt.setPreferredSize(new java.awt.Dimension(96, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 116;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 6, 0, 208);
        buscartxt.add(ResultadoMtxt, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel4.setText("Cédula");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(43, 154, 0, 0);
        buscartxt.add(jLabel4, gridBagConstraints);

        BotonBuscarMedico.setText("Buscar");
        BotonBuscarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBuscarMedicoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 108, 26, 0);
        buscartxt.add(BotonBuscarMedico, gridBagConstraints);

        Jlabel7.setText("Resultado:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(43, 48, 0, 0);
        buscartxt.add(Jlabel7, gridBagConstraints);

        cedulatxt1.setEnabled(false);
        cedulatxt1.setPreferredSize(new java.awt.Dimension(96, 22));
        cedulatxt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cedulatxt1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 98;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(40, 18, 0, 0);
        buscartxt.add(cedulatxt1, gridBagConstraints);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Medico", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        jPanel13.setLayout(new java.awt.GridBagLayout());

        campoId.setEnabled(false);
        campoId.setPreferredSize(new java.awt.Dimension(96, 22));
        campoId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                campoIdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 46;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(47, 18, 0, 0);
        jPanel13.add(campoId, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel1.setText("Cédula");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 152, 0, 0);
        jPanel13.add(jLabel1, gridBagConstraints);

        campoId1.setEnabled(false);
        campoId1.setPreferredSize(new java.awt.Dimension(96, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 46;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(47, 12, 0, 0);
        jPanel13.add(campoId1, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Nombre");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 34, 0, 0);
        jPanel13.add(jLabel2, gridBagConstraints);

        campoId2.setEnabled(false);
        campoId2.setPreferredSize(new java.awt.Dimension(96, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 46;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(47, 18, 0, 135);
        jPanel13.add(campoId2, gridBagConstraints);

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel3.setText("Especialidad");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(50, 35, 0, 0);
        jPanel13.add(jLabel3, gridBagConstraints);

        BotonGuardarMedico.setText("Guardar");
        BotonGuardarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarMedicoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 4, 29, 0);
        jPanel13.add(BotonGuardarMedico, gridBagConstraints);

        BotonLimpiarMedico.setText("Limpiar");
        BotonLimpiarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLimpiarMedicoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 22, 29, 0);
        jPanel13.add(BotonLimpiarMedico, gridBagConstraints);

        BotonEliminarMedico.setText("Eliminar");
        BotonEliminarMedico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEliminarMedicoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 0, 29, 0);
        jPanel13.add(BotonEliminarMedico, gridBagConstraints);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Listado", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        jPanel12.setLayout(new java.awt.GridBagLayout());

        TablaMedicos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Cedula", "Nombre", "Especialidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaMedicos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaMedicosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TablaMedicos);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 534;
        gridBagConstraints.ipady = 116;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(24, 249, 26, 153);
        jPanel12.add(jScrollPane2, gridBagConstraints);

        javax.swing.GroupLayout PestañaMedicosLayout = new javax.swing.GroupLayout(PestañaMedicos);
        PestañaMedicos.setLayout(PestañaMedicosLayout);
        PestañaMedicosLayout.setHorizontalGroup(
            PestañaMedicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaMedicosLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(PestañaMedicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, 878, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buscartxt, javax.swing.GroupLayout.PREFERRED_SIZE, 878, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 878, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        PestañaMedicosLayout.setVerticalGroup(
            PestañaMedicosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaMedicosLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(buscartxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Médicos", PestañaMedicos);

        PanelIngresaFarm.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Farmaceutas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        PanelIngresaFarm.setLayout(new java.awt.GridBagLayout());

        LabelCedulaF.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelCedulaF.setText("Cédula:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(52, 194, 0, 0);
        PanelIngresaFarm.add(LabelCedulaF, gridBagConstraints);

        LabelNombreF.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelNombreF.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 38;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(52, 41, 0, 0);
        PanelIngresaFarm.add(LabelNombreF, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(49, 12, 0, 0);
        PanelIngresaFarm.add(CedulaFtxt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(49, 6, 0, 239);
        PanelIngresaFarm.add(NombreFtxt, gridBagConstraints);

        BotonEliminarF.setText("Eliminar");
        BotonEliminarF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEliminarFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 18, 39, 0);
        PanelIngresaFarm.add(BotonEliminarF, gridBagConstraints);

        BotonLimpiarF.setText("Limpiar");
        BotonLimpiarF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLimpiarFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 32, 39, 0);
        PanelIngresaFarm.add(BotonLimpiarF, gridBagConstraints);

        BotonGuardarF1.setText("Guardar");
        BotonGuardarF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarF1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 97, 39, 0);
        PanelIngresaFarm.add(BotonGuardarF1, gridBagConstraints);

        PanelBusquedaF.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        PanelBusquedaF.setLayout(new java.awt.GridBagLayout());

        LabelCedulaFB1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelCedulaFB1.setText("Cédula:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(54, 185, 0, 0);
        PanelBusquedaF.add(LabelCedulaFB1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(51, 18, 0, 241);
        PanelBusquedaF.add(ResultadoFtxt, gridBagConstraints);

        LabelResultadoF2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelResultadoF2.setText("Resultado: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(54, 42, 0, 0);
        PanelBusquedaF.add(LabelResultadoF2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(51, 18, 0, 0);
        PanelBusquedaF.add(CedulaFtxt2, gridBagConstraints);

        BotonBuscarF.setText("Buscar");
        BotonBuscarF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBuscarFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 103, 36, 0);
        PanelBusquedaF.add(BotonBuscarF, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Listado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        TablaFarmaceutas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Cedula", " Nombre"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        TablaFarmaceutas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaFarmaceutasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaFarmaceutas);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(223, 223, 223)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 406, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 18, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PestañaFarmaceutasLayout = new javax.swing.GroupLayout(PestañaFarmaceutas);
        PestañaFarmaceutas.setLayout(PestañaFarmaceutasLayout);
        PestañaFarmaceutasLayout.setHorizontalGroup(
            PestañaFarmaceutasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaFarmaceutasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PestañaFarmaceutasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelIngresaFarm, javax.swing.GroupLayout.PREFERRED_SIZE, 883, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PanelBusquedaF, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(104, Short.MAX_VALUE))
        );
        PestañaFarmaceutasLayout.setVerticalGroup(
            PestañaFarmaceutasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaFarmaceutasLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelIngresaFarm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PanelBusquedaF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Farmaceutas", PestañaFarmaceutas);

        PanelIngresaFarm1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Pacientes", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        PanelIngresaFarm1.setLayout(new java.awt.GridBagLayout());

        LabelCedulaP1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelCedulaP1.setText("Cédula:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 142, 0, 0);
        PanelIngresaFarm1.add(LabelCedulaP1, gridBagConstraints);

        LabelNombreF1.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelNombreF1.setText("Nombre:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.ipadx = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(21, 30, 0, 0);
        PanelIngresaFarm1.add(LabelNombreF1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 24, 0, 0);
        PanelIngresaFarm1.add(CedulaPtxt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 66;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 38, 0, 236);
        PanelIngresaFarm1.add(NombrePtxt, gridBagConstraints);

        BotonEliminarP.setText("Eliminar");
        BotonEliminarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEliminarPActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 21, 5, 0);
        PanelIngresaFarm1.add(BotonEliminarP, gridBagConstraints);

        BotonLimpiarP.setText("Limpiar");
        BotonLimpiarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLimpiarPActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 38, 5, 0);
        PanelIngresaFarm1.add(BotonLimpiarP, gridBagConstraints);

        BotonGuardarP.setText("Guardar");
        BotonGuardarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarPActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(31, 39, 5, 0);
        PanelIngresaFarm1.add(BotonGuardarP, gridBagConstraints);

        LabelNombreP2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelNombreP2.setText("FechaNacimiento:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 13;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 142, 0, 0);
        PanelIngresaFarm1.add(LabelNombreP2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 24, 0, 0);
        PanelIngresaFarm1.add(FechaNacPtxt, gridBagConstraints);

        LabelTeltxt.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelTeltxt.setText("Telefono: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(15, 30, 0, 0);
        PanelIngresaFarm1.add(LabelTeltxt, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.ipadx = 66;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(13, 38, 0, 236);
        PanelIngresaFarm1.add(TelefonoPtxt, gridBagConstraints);

        PanelBusquedaF1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Búsqueda", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        PanelBusquedaF1.setLayout(new java.awt.GridBagLayout());

        LabelCedulaFB2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelCedulaFB2.setText("Cédula:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(39, 180, 0, 0);
        PanelBusquedaF1.add(LabelCedulaFB2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(36, 18, 0, 236);
        PanelBusquedaF1.add(ResultadoPtxt, gridBagConstraints);

        LabelResultadoF3.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelResultadoF3.setText("Resultado: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(39, 42, 0, 0);
        PanelBusquedaF1.add(LabelResultadoF3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(36, 2, 0, 0);
        PanelBusquedaF1.add(CedulaPtxt2, gridBagConstraints);

        BotonBuscarP.setText("Buscar");
        BotonBuscarP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBuscarPActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(22, 170, 39, 0);
        PanelBusquedaF1.add(BotonBuscarP, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Listado", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        TablaPacientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Cedula", " Nombre", "Fecha Nacimiento", "Telefono"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        TablaPacientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaPacientesMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(TablaPacientes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 496;
        gridBagConstraints.ipady = 119;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(24, 183, 21, 243);
        jPanel2.add(jScrollPane3, gridBagConstraints);

        javax.swing.GroupLayout PestañaPacientesLayout = new javax.swing.GroupLayout(PestañaPacientes);
        PestañaPacientes.setLayout(PestañaPacientesLayout);
        PestañaPacientesLayout.setHorizontalGroup(
            PestañaPacientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaPacientesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PestañaPacientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(PanelBusquedaF1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(PanelIngresaFarm1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 881, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(106, Short.MAX_VALUE))
        );
        PestañaPacientesLayout.setVerticalGroup(
            PestañaPacientesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaPacientesLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(PanelIngresaFarm1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PanelBusquedaF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pacientes", PestañaPacientes);

        PestañaMedicamentos.setLayout(new java.awt.GridBagLayout());

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Medicamentos", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        CodigoMtxt.setEnabled(false);
        CodigoMtxt.setPreferredSize(new java.awt.Dimension(96, 22));

        LabelCodigoM.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelCodigoM.setText("Código:");

        NombreMedicamentotxt.setEnabled(false);
        NombreMedicamentotxt.setPreferredSize(new java.awt.Dimension(96, 22));

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel8.setText("Nombre:");

        PresentacionMedicamentotxt.setEnabled(false);
        PresentacionMedicamentotxt.setPreferredSize(new java.awt.Dimension(96, 22));

        LabelPresentacionM.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        LabelPresentacionM.setText("Presentación:");

        BotonGuardarMedicamento.setText("Guardar");
        BotonGuardarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarMedicamentoActionPerformed(evt);
            }
        });

        BotonLimpiarMedicamento.setText("Limpiar");
        BotonLimpiarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLimpiarMedicamentoActionPerformed(evt);
            }
        });

        BotonEliminarMedicamento.setText("Eliminar");
        BotonEliminarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEliminarMedicamentoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addComponent(LabelCodigoM, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(CodigoMtxt, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(65, 65, 65)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(NombreMedicamentotxt, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(LabelPresentacionM, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(PresentacionMedicamentotxt, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(135, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BotonGuardarMedicamento)
                .addGap(44, 44, 44)
                .addComponent(BotonLimpiarMedicamento)
                .addGap(39, 39, 39)
                .addComponent(BotonEliminarMedicamento)
                .addGap(270, 270, 270))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CodigoMtxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NombreMedicamentotxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PresentacionMedicamentotxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LabelCodigoM)
                            .addComponent(jLabel8)
                            .addComponent(LabelPresentacionM))))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonGuardarMedicamento)
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(BotonEliminarMedicamento)
                        .addComponent(BotonLimpiarMedicamento)))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 129;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 6, 0, 0);
        PestañaMedicamentos.add(jPanel14, gridBagConstraints);

        buscartxt1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Búsqueda", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        ResultadoMedicamentotxt.setEnabled(false);
        ResultadoMedicamentotxt.setPreferredSize(new java.awt.Dimension(96, 22));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel10.setText("Código:");

        BotonBuscarMedicamento.setText("Buscar");
        BotonBuscarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBuscarMedicamentoActionPerformed(evt);
            }
        });

        Jlabel8.setText("Resultado:");

        CodigoMtxt2.setEnabled(false);
        CodigoMtxt2.setPreferredSize(new java.awt.Dimension(96, 22));

        javax.swing.GroupLayout buscartxt1Layout = new javax.swing.GroupLayout(buscartxt1);
        buscartxt1.setLayout(buscartxt1Layout);
        buscartxt1Layout.setHorizontalGroup(
            buscartxt1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buscartxt1Layout.createSequentialGroup()
                .addGap(78, 78, 78)
                .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CodigoMtxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BotonBuscarMedicamento)
                .addGap(50, 50, 50)
                .addComponent(Jlabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ResultadoMedicamentotxt, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(198, Short.MAX_VALUE))
        );
        buscartxt1Layout.setVerticalGroup(
            buscartxt1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(buscartxt1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(buscartxt1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(ResultadoMedicamentotxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonBuscarMedicamento)
                    .addComponent(Jlabel8)
                    .addComponent(CodigoMtxt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(71, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 192;
        gridBagConstraints.ipady = 65;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(27, 6, 0, 0);
        PestañaMedicamentos.add(buscartxt1, gridBagConstraints);

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Listado", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        TablaMedicamentos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Cedula", "Nombre", "Presentación"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaMedicamentos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaMedicamentosMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(TablaMedicamentos);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(185, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 179;
        gridBagConstraints.ipady = 15;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(26, 6, 61, 0);
        PestañaMedicamentos.add(jPanel15, gridBagConstraints);

        jTabbedPane1.addTab("Medicamentos", PestañaMedicamentos);

        PestañaDashboard.setEnabled(false);
        PestañaDashboard.setMaximumSize(new java.awt.Dimension(767, 767));

        PanelDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel9.setText("Desde");

        jLabel12.setText("Hasta");

        AñoInicio.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.YEAR));

        AñoFin.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.YEAR));

        DiaMesInicio.setModel(new javax.swing.SpinnerDateModel());

        DiaMesFin.setModel(new javax.swing.SpinnerDateModel());

        jLabel13.setText("Año");

        jLabel14.setText("Día - Mes");

        jLabel15.setText("Medicamentos");

        jComboBoxMedicamentos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("Gráfico Medicamentos");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tblMedicamentosGrafico.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(tblMedicamentosGrafico);

        BotonSeleccionFechas.setText("Gráfico Recetas");
        BotonSeleccionFechas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSeleccionFechasActionPerformed(evt);
            }
        });

        BotonAgregarMedicamentoComboBox.setText("Agregar");
        BotonAgregarMedicamentoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAgregarMedicamentoComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelDatosLayout = new javax.swing.GroupLayout(PanelDatos);
        PanelDatos.setLayout(PanelDatosLayout);
        PanelDatosLayout.setHorizontalGroup(
            PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDatosLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDatosLayout.createSequentialGroup()
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AñoInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AñoFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13))
                        .addGap(18, 18, 18)
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14)
                            .addComponent(DiaMesInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DiaMesFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelDatosLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(BotonSeleccionFechas)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDatosLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(BotonAgregarMedicamentoComboBox)
                        .addGap(100, 100, 100))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton1)
                        .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );
        PanelDatosLayout.setVerticalGroup(
            PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosLayout.createSequentialGroup()
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel14)
                    .addComponent(jLabel15)
                    .addComponent(jComboBoxMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonAgregarMedicamentoComboBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDatosLayout.createSequentialGroup()
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DiaMesInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AñoInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addGap(12, 12, 12)
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AñoFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12)
                            .addComponent(DiaMesFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonSeleccionFechas)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelMedicamentos.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Medicamentos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        javax.swing.GroupLayout PanelMedicamentosLayout = new javax.swing.GroupLayout(PanelMedicamentos);
        PanelMedicamentos.setLayout(PanelMedicamentosLayout);
        PanelMedicamentosLayout.setHorizontalGroup(
            PanelMedicamentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 342, Short.MAX_VALUE)
        );
        PanelMedicamentosLayout.setVerticalGroup(
            PanelMedicamentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        PanelRecetas.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Recetas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        javax.swing.GroupLayout PanelRecetasLayout = new javax.swing.GroupLayout(PanelRecetas);
        PanelRecetas.setLayout(PanelRecetasLayout);
        PanelRecetasLayout.setHorizontalGroup(
            PanelRecetasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        PanelRecetasLayout.setVerticalGroup(
            PanelRecetasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PestañaDashboardLayout = new javax.swing.GroupLayout(PestañaDashboard);
        PestañaDashboard.setLayout(PestañaDashboardLayout);
        PestañaDashboardLayout.setHorizontalGroup(
            PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaDashboardLayout.createSequentialGroup()
                .addGroup(PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PestañaDashboardLayout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(PanelMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PanelRecetas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PestañaDashboardLayout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(PanelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(116, 116, 116))
        );
        PestañaDashboardLayout.setVerticalGroup(
            PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaDashboardLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(PanelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelRecetas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelMedicamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 48, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dashboard", PestañaDashboard);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Listado", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        TablaRecetas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Paciente", "Medico", "Fecha Emision", "Fecha Retiro", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        TablaRecetas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaRecetasMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(TablaRecetas);

        BotonVerIndicaciones.setText("Ver Indicaciones");
        BotonVerIndicaciones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonVerIndicacionesActionPerformed(evt);
            }
        });

        TablaIndicaciones.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Medicamento", "Cantidad", "Indicaciones", "Duración"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane7.setViewportView(TablaIndicaciones);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(BotonVerIndicaciones)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(54, 54, 54)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(142, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BotonVerIndicaciones)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        javax.swing.GroupLayout PestañaHistoricoLayout = new javax.swing.GroupLayout(PestañaHistorico);
        PestañaHistorico.setLayout(PestañaHistoricoLayout);
        PestañaHistoricoLayout.setHorizontalGroup(
            PestañaHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaHistoricoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PestañaHistoricoLayout.setVerticalGroup(
            PestañaHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaHistoricoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(177, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Historico", PestañaHistorico);

        PestañaAcercaDe.setLayout(new java.awt.GridBagLayout());

        jPanel9.setPreferredSize(new java.awt.Dimension(224, 300));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 139, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 439, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.ipadx = 139;
        gridBagConstraints.ipady = 439;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 6, 16, 0);
        PestañaAcercaDe.add(jPanel9, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Hospital Benjamín Nuñez");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 309;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 63, 0, 0);
        PestañaAcercaDe.add(jLabel6, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Sistema de Prescripción y Despacho de Medicamentos ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 36;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 63, 0, 0);
        PestañaAcercaDe.add(jLabel5, gridBagConstraints);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/3.png"))); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = -54;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 18, 0, 167);
        PestañaAcercaDe.add(jLabel7, gridBagConstraints);

        jTabbedPane1.addTab("Acerca de", PestañaAcercaDe);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          


    private void BotonLimpiarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLimpiarMedicoActionPerformed
      limpiarCampos();
       cambiarModoVista();
    }//GEN-LAST:event_BotonLimpiarMedicoActionPerformed

    private void BotonLimpiarFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLimpiarFActionPerformed
    limpiarCampos();
       cambiarModoVista();
    }//GEN-LAST:event_BotonLimpiarFActionPerformed

    private void BotonBuscarFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBuscarFActionPerformed
        buscarFarmaceuta();
    }//GEN-LAST:event_BotonBuscarFActionPerformed

    private void BotonGuardarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarMedicoActionPerformed
        guardarMedico();
    }//GEN-LAST:event_BotonGuardarMedicoActionPerformed

    private void BotonEliminarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarMedicoActionPerformed
        eliminarMedico();
    }//GEN-LAST:event_BotonEliminarMedicoActionPerformed

    private void BotonBuscarMedicoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBuscarMedicoActionPerformed
        buscarMedico();
    }//GEN-LAST:event_BotonBuscarMedicoActionPerformed

    private void BotonGuardarF1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarF1ActionPerformed
        guardarFarmaceuta();
    }//GEN-LAST:event_BotonGuardarF1ActionPerformed

    private void BotonEliminarFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarFActionPerformed
        eliminarFarmaceuta();
    }//GEN-LAST:event_BotonEliminarFActionPerformed

    private void cedulatxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cedulatxt1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cedulatxt1ActionPerformed

    private void BotonGuardarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarPActionPerformed
        guardarPaciente();
    }//GEN-LAST:event_BotonGuardarPActionPerformed

    private void BotonLimpiarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLimpiarPActionPerformed
       limpiarCampos();
       cambiarModoVista();
    }//GEN-LAST:event_BotonLimpiarPActionPerformed

    private void BotonEliminarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarPActionPerformed
        EliminarPaciente();
    }//GEN-LAST:event_BotonEliminarPActionPerformed

    private void BotonBuscarPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBuscarPActionPerformed
        buscarPaciente();
    }//GEN-LAST:event_BotonBuscarPActionPerformed

    private void BotonGuardarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarMedicamentoActionPerformed
        guardarMedicamento();
    }//GEN-LAST:event_BotonGuardarMedicamentoActionPerformed

    private void BotonLimpiarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLimpiarMedicamentoActionPerformed
       limpiarCampos();
       cambiarModoVista();
    }//GEN-LAST:event_BotonLimpiarMedicamentoActionPerformed

    private void BotonEliminarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarMedicamentoActionPerformed
        EliminarMedicamento();
    }//GEN-LAST:event_BotonEliminarMedicamentoActionPerformed

    private void BotonBuscarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBuscarMedicamentoActionPerformed
        buscarMedicamento();
    }//GEN-LAST:event_BotonBuscarMedicamentoActionPerformed

    private void BotonVerIndicacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonVerIndicacionesActionPerformed
//        int fila = TablaRecetas.getSelectedRow();
//        if (fila >= 0) {
//            String codigo = TablaRecetas.getValueAt(fila, 0).toString(); // código de la receta
//            Receta receta = controlador.buscarReceta(codigo); // debes tener este método en tu controlador
//            if (receta != null) {
//                mostrarIndicacionesReceta(receta);
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "No se encontró la receta seleccionada.",
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//            }
//        } else {
//            JOptionPane.showMessageDialog(this,
//                    "Seleccione una receta de la tabla.",
//                    "Aviso",
//                    JOptionPane.WARNING_MESSAGE);
//        }
    }//GEN-LAST:event_BotonVerIndicacionesActionPerformed

    //DashBoard=========================================================
    private final List<String> medicamentosSeleccionados = new ArrayList<>();

    private void configurarSpinnersDashboard() {
        // Spinner solo año
        AñoInicio.setModel(new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.YEAR));
        AñoFin.setModel(new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.YEAR));

        // Formateo para mostrar solo el año
        JSpinner.DateEditor editorAñoInicio = new JSpinner.DateEditor(AñoInicio, "yyyy");
        AñoInicio.setEditor(editorAñoInicio);

        JSpinner.DateEditor editorAñoFin = new JSpinner.DateEditor(AñoFin, "yyyy");
        AñoFin.setEditor(editorAñoFin);

        // Spinner día-mes
        DiaMesInicio.setModel(new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));
        DiaMesFin.setModel(new javax.swing.SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH));

        // Formateo para mostrar solo día y mes
        JSpinner.DateEditor editorDiaMesInicio = new JSpinner.DateEditor(DiaMesInicio, "dd-MMM");
        DiaMesInicio.setEditor(editorDiaMesInicio);

        JSpinner.DateEditor editorDiaMesFin = new JSpinner.DateEditor(DiaMesFin, "dd-MMM");
        DiaMesFin.setEditor(editorDiaMesFin);
    }

    private void confirmarSeleccionFechasPastel() {
        // 1. Capturar los valores de los Spinners
        Date fechaAñoInicio = (Date) AñoInicio.getValue();
        Date fechaAñoFin = (Date) AñoFin.getValue();
        Date fechaDiaMesInicio = (Date) DiaMesInicio.getValue();
        Date fechaDiaMesFin = (Date) DiaMesFin.getValue();

        // 2. Convertir a LocalDate (opcional, según tu método)
        LocalDate inicio = LocalDate.of(
                fechaAñoInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear(),
                fechaDiaMesInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonth(),
                fechaDiaMesInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth()
        );

        LocalDate fin = LocalDate.of(
                fechaAñoFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear(),
                fechaDiaMesFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonth(),
                fechaDiaMesFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth()
        );

        // 3. Llamar al método del controlador para crear el gráfico
//        JFreeChart chart = controlador.crearGraficoPastelRecetasPorEstado(inicio, fin);

        // 4. Mostrarlo en el PanelRecetas
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setPreferredSize(new java.awt.Dimension(
//                PanelRecetas.getWidth(),
//                PanelRecetas.getHeight()
//        ));

        PanelRecetas.removeAll();
        PanelRecetas.setLayout(new java.awt.BorderLayout());
//        PanelRecetas.add(chartPanel, java.awt.BorderLayout.CENTER);
        PanelRecetas.validate();
        PanelRecetas.repaint();
    }

    private void crearGraficoPastelRecetasPorEstado(LocalDate fI, LocalDate fF) {
//        // Pedimos el gráfico al controlador
//        JFreeChart chart = controlador.crearGraficoPastelRecetasPorEstado(fI, fF);
//
//        // Lo metemos en un ChartPanel
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setMouseWheelEnabled(true);
//
//        chartPanel.setPreferredSize(null);
//        chartPanel.setPreferredSize(new java.awt.Dimension(
//                PanelRecetas.getWidth(),
//                PanelRecetas.getHeight()
//        ));
//
//        // Limpiamos y agregamos al PanelRecetas
//        PanelRecetas.removeAll();
//        PanelRecetas.setLayout(new java.awt.BorderLayout());
//        PanelRecetas.add(chartPanel, java.awt.BorderLayout.CENTER);
//
//        // Forzar actualización visual
//        PanelRecetas.validate();
//        PanelRecetas.repaint();
    }

//    public DefaultTableModel crearTablaMedicamentosPorMes(
//            LocalDate inicio, LocalDate fin, List<String> seleccionados, List<Receta> listaRecetas) {
//
//        // Construir los títulos de las columnas dinámicamente: Año-Mes
//        List<String> columnas = new ArrayList<>();
//        columnas.add("Medicamento");
//
//        LocalDate fecha = inicio.withDayOfMonth(1);
//        while (!fecha.isAfter(fin)) {
//            columnas.add(fecha.getYear() + "-" + String.format("%02d", fecha.getMonthValue()));
//            fecha = fecha.plusMonths(1);
//        }
//
//        DefaultTableModel modelo = new DefaultTableModel(columnas.toArray(), 0);
//
//        // Llenar filas
//        for (String med : seleccionados) {
//            List<Object> fila = new ArrayList<>();
//            fila.add(med);
//
//            fecha = inicio.withDayOfMonth(1);
//            while (!fecha.isAfter(fin)) {
//                int cantidad = 0;
//                for (Receta r : listaRecetas) {
//                    Date fechaEmision = r.getFechaEmision();
//                    if ((fechaEmision.getYear() == fecha.getYear()) && (fechaEmision.getMonthValue() == fecha.getMonthValue())) {
//                        for (Indicaciones i : r.getIndicaciones()) {
//                            if (i.getMedicamento().getNombre().equals(med)) {
//                                cantidad += i.getCantidad();
//                            }
//                        }
//                    }
//                }
//                fila.add(cantidad);
//                fecha = fecha.plusMonths(1);
//            }
//
//            modelo.addRow(fila.toArray());
//        }
//
//        return modelo;
//    }

    private void cargarMedicamentosComboBox() {
//        jComboBoxMedicamentos.removeAllItems();
//        for (Medicamento m : controlador.ListarMedicamentos()) {
//            jComboBoxMedicamentos.addItem(m.getNombre());
//        }
    }

    // Acción del botón "Agregar medicamento"
    private void agregarMedicamentoSeleccionado() {
        String seleccionado = (String) jComboBoxMedicamentos.getSelectedItem();
        if (seleccionado != null && !medicamentosSeleccionados.contains(seleccionado)) {
            medicamentosSeleccionados.add(seleccionado);

            // Refrescar tabla y gráfico
            refrescarTablaMedicamentos();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ya has agregado este medicamento o no hay selección válida.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refrescarTablaMedicamentos() {
        LocalDate inicio = LocalDate.of(
                ((Date) AñoInicio.getValue()).toInstant().atZone(ZoneId.systemDefault()).getYear(),
                ((Date) DiaMesInicio.getValue()).toInstant().atZone(ZoneId.systemDefault()).getMonth(),
                1
        );

        LocalDate fin = LocalDate.of(
                ((Date) AñoFin.getValue()).toInstant().atZone(ZoneId.systemDefault()).getYear(),
                ((Date) DiaMesFin.getValue()).toInstant().atZone(ZoneId.systemDefault()).getMonth(),
                1
        );

//        DefaultTableModel modelo = crearTablaMedicamentosPorMes(
//                inicio,
//                fin,
//                medicamentosSeleccionados,
//                controlador.ListarRecetas()
//        );
//        tblMedicamentosGrafico.setModel(modelo);
    }

    private void generarGraficoMedicamentos() {
        if (medicamentosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe agregar al menos un medicamento para generar el gráfico.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate inicio = LocalDate.of(
                ((Date) AñoInicio.getValue()).toInstant().atZone(ZoneId.systemDefault()).getYear(),
                ((Date) DiaMesInicio.getValue()).toInstant().atZone(ZoneId.systemDefault()).getMonth(),
                1
        );

        LocalDate fin = LocalDate.of(
                ((Date) AñoFin.getValue()).toInstant().atZone(ZoneId.systemDefault()).getYear(),
                ((Date) DiaMesFin.getValue()).toInstant().atZone(ZoneId.systemDefault()).getMonth(),
                1
        );
//
//        JFreeChart chart = controlador.crearGraficoLineaMedicamentos(
//                inicio,
//                fin,
//                medicamentosSeleccionados,
//                controlador.ListarRecetas()
//        );

        PanelMedicamentos.removeAll();
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setPreferredSize(new Dimension(PanelMedicamentos.getWidth(), PanelMedicamentos.getHeight()));
//        PanelMedicamentos.setLayout(new BorderLayout());
//        PanelMedicamentos.add(chartPanel, BorderLayout.CENTER);
//        PanelMedicamentos.validate();
//        PanelMedicamentos.repaint();
    }

    private void confirmarSeleccionFechasLineal() {
        // 1. Capturar los valores de los Spinners
        Date fechaAñoInicio = (Date) AñoInicio.getValue();
        Date fechaAñoFin = (Date) AñoFin.getValue();
        Date fechaDiaMesInicio = (Date) DiaMesInicio.getValue();
        Date fechaDiaMesFin = (Date) DiaMesFin.getValue();

        // 2. Convertir a LocalDate
        LocalDate inicio = LocalDate.of(
                fechaAñoInicio.toInstant().atZone(ZoneId.systemDefault()).getYear(),
                fechaDiaMesInicio.toInstant().atZone(ZoneId.systemDefault()).getMonth(),
                1 // siempre el primer día del mes
        );

        LocalDate fin = LocalDate.of(
                fechaAñoFin.toInstant().atZone(ZoneId.systemDefault()).getYear(),
                fechaDiaMesFin.toInstant().atZone(ZoneId.systemDefault()).getMonth(),
                1 // primer día del mes
        );

        // ⚠️ Usar la lista global, NO crear una nueva
        if (medicamentosSeleccionados.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Debe agregar al menos un medicamento.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 3. Crear el gráfico de líneas usando el controlador
//        JFreeChart chart = controlador.crearGraficoLineaMedicamentos(
//                inicio,
//                fin,
//                medicamentosSeleccionados, // usar la lista global
//                controlador.ListarRecetas()
//        );

        // 4. Mostrarlo en el PanelMedicamentos
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setPreferredSize(new java.awt.Dimension(
//                PanelMedicamentos.getWidth(),
//                PanelMedicamentos.getHeight()
//        ));

        PanelMedicamentos.removeAll();
        PanelMedicamentos.setLayout(new java.awt.BorderLayout());
//        PanelMedicamentos.add(chartPanel, java.awt.BorderLayout.CENTER);
        PanelMedicamentos.validate();
        PanelMedicamentos.repaint();
    }


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        generarGraficoMedicamentos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void BotonSeleccionFechasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSeleccionFechasActionPerformed
        // TODO add your handling code here:
        confirmarSeleccionFechasPastel();
    }//GEN-LAST:event_BotonSeleccionFechasActionPerformed

    private void BotonAgregarMedicamentoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAgregarMedicamentoComboBoxActionPerformed
        // TODO add your handling code here:
        agregarMedicamentoSeleccionado();
    }//GEN-LAST:event_BotonAgregarMedicamentoComboBoxActionPerformed

    private void campoIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_campoIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_campoIdActionPerformed

    private void TablaMedicosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaMedicosMouseClicked
        if (estado.isViewing()) {
            cargarMedicoDesdeTabla();
        }
    }//GEN-LAST:event_TablaMedicosMouseClicked

    private void TablaFarmaceutasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaFarmaceutasMouseClicked
        if (estado.isViewing()) {
            cargarFarmaceutaDesdeTabla();
        }
    }//GEN-LAST:event_TablaFarmaceutasMouseClicked

    private void TablaPacientesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaPacientesMouseClicked
        if (estado.isViewing()) {
            cargarPacientesDesdeTabla();
        }
    }//GEN-LAST:event_TablaPacientesMouseClicked

    private void TablaMedicamentosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaMedicamentosMouseClicked
        if (estado.isViewing()) {
            cargarMedicamentoDesdeTabla();
        }
    }//GEN-LAST:event_TablaMedicamentosMouseClicked

    private void TablaRecetasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaRecetasMouseClicked
        if (estado.isViewing()) {
            cargarRecetaDesdeTabla();
        }
    }//GEN-LAST:event_TablaRecetasMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            modelo modelo = null;
            try {
                modelo = new modelo();
            } catch (SQLException ex) {
                Logger.getLogger(VentanaAdministrador.class.getName()).log(Level.SEVERE, null, ex);
            }
            Control controlador = new Control(modelo);
//            try {
//                modelo.cargarDatos(); // ✅ carga médicos, pacientes, farmaceutas, etc.
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            new VentanaAdministrador(controlador).setVisible(true);
        });
    }

    private void asignarIconosPestanas() {
        int tamañoIcono = 18;

        // Mapa de panel -> icono
        Map<javax.swing.JPanel, FontAwesomeSolid> iconos = new HashMap<>();
        iconos.put(PestañaAcercaDe, FontAwesomeSolid.INFO_CIRCLE);
        iconos.put(PestañaDashboard, FontAwesomeSolid.TACHOMETER_ALT);
        iconos.put(PestañaFarmaceutas, FontAwesomeSolid.USER_NURSE);
        iconos.put(PestañaHistorico, FontAwesomeSolid.HISTORY);
        iconos.put(PestañaMedicamentos, FontAwesomeSolid.PILLS);
        iconos.put(PestañaMedicos, FontAwesomeSolid.USER_MD);
        iconos.put(PestañaPacientes, FontAwesomeSolid.USERS);

        // Asignamos iconos
        for (int i = 0; i < jTabbedPane1.getTabCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) jTabbedPane1.getComponentAt(i);
            if (iconos.containsKey(panel)) {
                FontIcon icon = FontIcon.of(iconos.get(panel), tamañoIcono);
                jTabbedPane1.setIconAt(i, icon);
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner AñoFin;
    private javax.swing.JSpinner AñoInicio;
    private javax.swing.JButton BotonAgregarMedicamentoComboBox;
    private javax.swing.JButton BotonBuscarF;
    private javax.swing.JButton BotonBuscarMedicamento;
    private javax.swing.JButton BotonBuscarMedico;
    private javax.swing.JButton BotonBuscarP;
    private javax.swing.JButton BotonEliminarF;
    private javax.swing.JButton BotonEliminarMedicamento;
    private javax.swing.JButton BotonEliminarMedico;
    private javax.swing.JButton BotonEliminarP;
    private javax.swing.JButton BotonGuardarF1;
    private javax.swing.JButton BotonGuardarMedicamento;
    private javax.swing.JButton BotonGuardarMedico;
    private javax.swing.JButton BotonGuardarP;
    private javax.swing.JButton BotonLimpiarF;
    private javax.swing.JButton BotonLimpiarMedicamento;
    private javax.swing.JButton BotonLimpiarMedico;
    private javax.swing.JButton BotonLimpiarP;
    private javax.swing.JButton BotonSeleccionFechas;
    private javax.swing.JButton BotonVerIndicaciones;
    private javax.swing.JTextField CedulaFtxt;
    private javax.swing.JTextField CedulaFtxt2;
    private javax.swing.JTextField CedulaPtxt;
    private javax.swing.JTextField CedulaPtxt2;
    private javax.swing.JTextField CodigoMtxt;
    private javax.swing.JTextField CodigoMtxt2;
    private javax.swing.JSpinner DiaMesFin;
    private javax.swing.JSpinner DiaMesInicio;
    private javax.swing.JTextField FechaNacPtxt;
    private javax.swing.JLabel Jlabel7;
    private javax.swing.JLabel Jlabel8;
    private javax.swing.JLabel LabelCedulaF;
    private javax.swing.JLabel LabelCedulaFB1;
    private javax.swing.JLabel LabelCedulaFB2;
    private javax.swing.JLabel LabelCedulaP1;
    private javax.swing.JLabel LabelCodigoM;
    private javax.swing.JLabel LabelNombreF;
    private javax.swing.JLabel LabelNombreF1;
    private javax.swing.JLabel LabelNombreP2;
    private javax.swing.JLabel LabelPresentacionM;
    private javax.swing.JLabel LabelResultadoF2;
    private javax.swing.JLabel LabelResultadoF3;
    private javax.swing.JLabel LabelTeltxt;
    private javax.swing.JTextField NombreFtxt;
    private javax.swing.JTextField NombreMedicamentotxt;
    private javax.swing.JTextField NombrePtxt;
    private javax.swing.JPanel PanelBusquedaF;
    private javax.swing.JPanel PanelBusquedaF1;
    private javax.swing.JPanel PanelDatos;
    private javax.swing.JPanel PanelIngresaFarm;
    private javax.swing.JPanel PanelIngresaFarm1;
    private javax.swing.JPanel PanelMedicamentos;
    private javax.swing.JPanel PanelRecetas;
    private javax.swing.JPanel PestañaAcercaDe;
    private javax.swing.JPanel PestañaDashboard;
    private javax.swing.JPanel PestañaFarmaceutas;
    private javax.swing.JPanel PestañaHistorico;
    private javax.swing.JPanel PestañaMedicamentos;
    private javax.swing.JPanel PestañaMedicos;
    private javax.swing.JPanel PestañaPacientes;
    private javax.swing.JTextField PresentacionMedicamentotxt;
    private javax.swing.JTextField ResultadoFtxt;
    private javax.swing.JTextField ResultadoMedicamentotxt;
    private javax.swing.JTextField ResultadoMtxt;
    private javax.swing.JTextField ResultadoPtxt;
    private javax.swing.JTable TablaFarmaceutas;
    private javax.swing.JTable TablaIndicaciones;
    private javax.swing.JTable TablaMedicamentos;
    private javax.swing.JTable TablaMedicos;
    private javax.swing.JTable TablaPacientes;
    private javax.swing.JTable TablaRecetas;
    private javax.swing.JTextField TelefonoPtxt;
    private javax.swing.JPanel buscartxt;
    private javax.swing.JPanel buscartxt1;
    private javax.swing.JTextField campoId;
    private javax.swing.JTextField campoId1;
    private javax.swing.JTextField campoId2;
    private javax.swing.JTextField cedulatxt1;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBoxMedicamentos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable tblMedicamentosGrafico;
    // End of variables declaration//GEN-END:variables

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaAdministrador.class.getName());

    private final Control controlador;
    private FormHandler estado;

};
