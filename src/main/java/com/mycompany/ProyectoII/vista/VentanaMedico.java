package com.mycompany.ProyectoII.vista;

import com.mycompany.ProyectoII.Conexión.Protocolo;
import com.mycompany.ProyectoII.Conexión.ServiceProxy;
import com.mycompany.ProyectoII.Indicaciones;
import com.mycompany.ProyectoII.Medicamento;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Paciente;
import com.mycompany.ProyectoII.Receta;
import com.mycompany.ProyectoII.control.Control;
import com.mycompany.ProyectoII.modelo.Modelo;
import cr.ac.una.gui.FormHandler;
import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.kordamp.ikonli.fontawesome6.FontAwesomeSolid;
import org.kordamp.ikonli.swing.FontIcon;

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
* versión 2.0.0 06-11-2025                                               |
*                                                                     |
* --------------------------------------------------------------------+
 */
public class VentanaMedico extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaMedico.class.getName());

    public VentanaMedico(Control controlador, Medico med) throws SQLException {
        if (controlador == null) {
            throw new IllegalArgumentException("El control no puede ser null");
        }
        this.control = controlador;
        this.estado = new FormHandler();
        this.medicoActual = med;
        initComponents();
        recetaActual = new Receta();
        nuevasIndicaciones = new Indicaciones();
        this.setLocationRelativeTo(null);
        //configurarListeners();
        try {
            proxy = new ServiceProxy("localhost", Protocolo.PUERTO);

            //  Hacer login con el usuario actual
            proxy.login(control.getUsuarioActual().getNombre());
            this.setTitle("Ventana Médico - " + control.getUsuarioActual().getNombre());
            //  Hilo que actualiza la tabla cuando lleguen notificaciones
            refrescarUsuarios(proxy);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar al servidor: " + ex.getMessage());
        }

        init();
    }

    public void init() throws SQLException {

        configurarSpinnersDashboard();
        cargarMedicamentosComboBox();
        VentanaMedico.addChangeListener(e -> {
            int index = VentanaMedico.getSelectedIndex();

            // Solo nos interesa la pestaña 2
            if (index == 2) {
                actualizarTablaRecetas();
            }
            actualizarControles();
        });

        asignarIconosPestanas();
        actualizarTablaRecetas();
        actualizarTablaMedicamentos();
        cambiarModoVista();
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
        if (estado.isViewing()) {
            estado.changeToAddMode();
            actualizarComponentes();
        }
    }

    private void cambiarModoEditar() {
        if (estado.isViewing() && estado.getModel() != null) {
            estado.changeToEditMode();
            actualizarComponentes();
        }
    }

    private void cambiarModoBuscar() {
        estado.changeToSearchMode();
        actualizarComponentes();
    }

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // ACTUALIZACIÓN DE COMPONENTES
    // -------------------------------------------------------------------------
    private void actualizarComponentes() {
        actualizarControles();
    }

    private void actualizarControles() {
        boolean hayPaciente = recetaActual.getPaciente() != null;
        boolean hayMedicamento
                = (recetaActual.getIndicaciones() != null && !recetaActual.getIndicaciones().isEmpty())
                || (recetaActual.getInditemporal() != null && !recetaActual.getInditemporal().isEmpty());

        BotonBuscarPaciente.setEnabled(true); // siempre debe poder buscar
        BotonAgregarMedicamento.setEnabled(hayPaciente); // solo después de tener paciente
        BotonGuardarPresc.setEnabled(hayPaciente && hayMedicamento);
        BotonEliminarPresc.setEnabled(hayPaciente && hayMedicamento);
        BotonDetallesPresc.setEnabled(hayPaciente && hayMedicamento);
        BotonLimpiarPresc.setEnabled(true);
    }

    // -------------------------------------------------------------------------
    // OPERACIONES CRUD
    // -------------------------------------------------------------------------
    private void limpiarCampos() {
        estado.setModel(null);
        recetaActual = new Receta();
        mostrarNombre.setText("");
        actualizarTabla(recetaActual);
        actualizarControles();
    }

    private void indicarCambios() {
        estado.setModified(true);
        actualizarControles();
    }

    private void cancelarOperacion() {
        cambiarModoVista();
        actualizarControles();
    }

    //Tabla usuarios conectados
    private void actualizarTablaUsuarios() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Mensaje"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 1) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // solo la columna "Mensaje" (checkbox)
            }
        };

        for (String usuario : proxy.getUsuariosActivos()) {
            model.addRow(new Object[]{usuario, false});
        }

        UsuariosConectados.setModel(model);
    }

    public void refrescarUsuarios(ServiceProxy proxy) {
        new Thread(() -> {
            while (true) {
                List<String> usuarios = proxy.getUsuariosActivos();

                SwingUtilities.invokeLater(() -> {
                    DefaultTableModel model = (DefaultTableModel) UsuariosConectados.getModel();

                    // Guardar estado actual de los checkboxes
                    Map<String, Boolean> estadoCheck = new HashMap<>();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        String usuario = (String) model.getValueAt(i, 0);
                        Boolean seleccionado = (Boolean) model.getValueAt(i, 1);
                        estadoCheck.put(usuario, seleccionado);
                    }

                    // Limpiar y volver a llenar la tabla
                    model.setRowCount(0);
                    for (String u : usuarios) {
                        Boolean sel = estadoCheck.getOrDefault(u, false); // restaurar checkbox si existía
                        model.addRow(new Object[]{u, sel});
                    }
                });

                try {
                    Thread.sleep(1000); // refresca cada segundo
                } catch (InterruptedException e) {
                    break;
                }
            }
        }).start();
    }

    private void abrirBuscarPaciente() {
        buscarPaciente ventana = new buscarPaciente(control, this);
        ventana.setVisible(true);
    }

    public void pacienteSeleccionado(Paciente paciente) {
        if (paciente != null) {
            recetaActual.setPaciente(paciente);
            mostrarNombre.setText(paciente.getCedula() + " - " + paciente.getNombre());
            indicarCambios();
            cambiarModoEditar();
            actualizarControles();
        }
    }

    private void abrirBuscarMedicamento() {
        buscarMedicamento ventana = new buscarMedicamento(control, this);
        ventana.setVisible(true);
    }

    public void medicamentoSeleccionado(Medicamento medicamento, int can, String indica, int dura) {
        //  crear la indicación
        Indicaciones nuevasIndicaciones = new Indicaciones();
        nuevasIndicaciones.setMedicamento(medicamento);
        nuevasIndicaciones.setCantidad(can);
        nuevasIndicaciones.setIndicaciones(indica);
        nuevasIndicaciones.setDuracion(dura);

        //  agregarla a la receta actual
        recetaActual.agregarIndicaciones(nuevasIndicaciones);

        //  actualizar la tabla en la UI
        actualizarTabla(recetaActual);
        indicarCambios();
        cambiarModoEditar();
        actualizarControles();
    }

    private void actualizarTabla(Receta receta) {
        DefaultTableModel model = (DefaultTableModel) TablaMedicamentosReceta.getModel();
        model.setRowCount(0);

        // Si la receta viene de BD, usa la colección persistida
        if (receta.getIndicaciones() != null && !receta.getIndicaciones().isEmpty()) {
            for (Indicaciones i : receta.getIndicaciones()) {
                model.addRow(new Object[]{
                    i.getMedicamento().getNombre(),
                    i.getMedicamento().getPresentacion(),
                    i.getCantidad(),
                    i.getIndicaciones(),
                    i.getDuracion(),});
            }
        } // Si la receta es nueva (no guardada), usa las temporales
        else if (!receta.getInditemporal().isEmpty()) {
            for (Indicaciones i : receta.getInditemporal()) {
                model.addRow(new Object[]{
                    i.getMedicamento().getNombre(),
                    i.getMedicamento().getPresentacion(),
                    i.getCantidad(),
                    i.getIndicaciones(),
                    i.getDuracion(),});
            }
        }
    }

    private void actualizarTablaMedicamentos() {
        DefaultTableModel modelo = (DefaultTableModel) TablaMedicamentosReceta.getModel();
        modelo.setRowCount(0); // limpia la tabla

        if (recetaActual != null && recetaActual.getInditemporal() != null) {
            for (Indicaciones ind : recetaActual.getInditemporal()) {
                modelo.addRow(new Object[]{
                    ind.getMedicamento().getCodigo(),
                    ind.getMedicamento().getNombre(),
                    ind.getCantidad(),
                    ind.getDuracion(),
                    ind.getIndicaciones()
                });
            }
        }
    }

    private void guardarPrescripcion() {
        try {
            if (recetaActual == null) {
                JOptionPane.showMessageDialog(this, "No hay receta activa.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (recetaActual.getPaciente() == null) {
                JOptionPane.showMessageDialog(this, "Debe seleccionar un paciente antes de guardar.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if ((recetaActual.getIndicaciones() == null || recetaActual.getIndicaciones().isEmpty())
                    && (recetaActual.getInditemporal() == null || recetaActual.getInditemporal().isEmpty())) {
                JOptionPane.showMessageDialog(this, "Debe agregar al menos un medicamento a la receta.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Datos base de la receta
            recetaActual.setMedico(medicoActual);
            recetaActual.setCodReceta("R" + (control.cantidadRecetas() + 1));
            recetaActual.setFechaEmision(java.sql.Date.valueOf(LocalDate.now()));

            java.util.Date fechaSeleccionada = (java.util.Date) SpinnerFechaRetiro.getValue();
            recetaActual.setFechaRetiro(new java.sql.Date(fechaSeleccionada.getTime()));
            recetaActual.setEstado("CONFECCIONADA");

            // Guardar receta
            control.agregarReceta(recetaActual);

            // Guardar indicaciones asociadas
            if (recetaActual.getInditemporal() != null && !recetaActual.getInditemporal().isEmpty()) {
                for (Indicaciones ind : recetaActual.getInditemporal()) {
                    ind.setReceta(recetaActual); // Enlazar con la receta
                    control.agregarIndicacion(recetaActual, ind);
                }
                recetaActual.getInditemporal().clear(); // Limpieza
            }

            JOptionPane.showMessageDialog(this, "Receta y sus indicaciones guardadas correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            cambiarModoVista();
            limpiarCampos();
            indicarCambios();
            actualizarControles();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al guardar la receta: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void mostrarDetalles() throws SQLException {
        if (recetaActual == null) {
            JOptionPane.showMessageDialog(this, "No hay receta seleccionada.", "Detalles de la receta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar que tenga paciente y al menos una indicación (ya sea en BD o en memoria)
        boolean sinIndicaciones = (recetaActual.getIndicaciones() == null || recetaActual.getIndicaciones().isEmpty())
                && (recetaActual.getInditemporal() == null || recetaActual.getInditemporal().isEmpty());

        if (recetaActual.getPaciente() == null || sinIndicaciones) {
            JOptionPane.showMessageDialog(this, "La receta está incompleta o vacía.", "Detalles de la receta", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Completar datos si faltan
        if (recetaActual.getCodReceta() == null || recetaActual.getCodReceta().isEmpty()) {
            recetaActual.setCodReceta("R" + (control.cantidadRecetas() + 1));
        }

        if (recetaActual.getFechaEmision() == null) {
            recetaActual.setFechaEmision(Date.valueOf(LocalDate.now()));
        }

        recetaActual.setMedico(medicoActual);
        recetaActual.setEstado("CONFECCIONADA");

        // Construir el texto con los detalles
        StringBuilder detalles = new StringBuilder();
        detalles.append("Código de Receta: ").append(recetaActual.getCodReceta()).append("\n")
                .append("Paciente: ").append(recetaActual.getPaciente().getCedula())
                .append(" - ").append(recetaActual.getPaciente().getNombre()).append("\n")
                .append("Médico: ").append(recetaActual.getMedico().getNombre()).append("\n")
                .append("Fecha de emisión: ").append(recetaActual.getFechaEmision()).append("\n");

        if (recetaActual.getFechaRetiro() != null) {
            detalles.append("Fecha de retiro: ").append(recetaActual.getFechaRetiro()).append("\n");
        }

        detalles.append("Estado: ").append(recetaActual.getEstado()).append("\n\n");

        detalles.append("Medicamentos:\n");

        // Combinar las dos listas (de BD y temporal)
        List<Indicaciones> todasIndicaciones = new ArrayList<>();
        if (recetaActual.getIndicaciones() != null) {
            todasIndicaciones.addAll(recetaActual.getIndicaciones());
        }
        if (recetaActual.getInditemporal() != null) {
            todasIndicaciones.addAll(recetaActual.getInditemporal());
        }

        for (Indicaciones i : todasIndicaciones) {
            if (i.getMedicamento() == null) {
                continue;
            }

            detalles.append("- ").append(i.getMedicamento().getNombre())
                    .append(" | Presentación: ").append(i.getMedicamento().getPresentacion())
                    .append(" | Cantidad: ").append(i.getCantidad())
                    .append(" | Indicaciones: ").append(i.getIndicaciones())
                    .append(" | Duración: ").append(i.getDuracion()).append(" días\n");
        }

        JOptionPane.showMessageDialog(this, detalles.toString(), "Detalles de la Receta", JOptionPane.INFORMATION_MESSAGE);
    }
//==================================== DashBoard ========================================

    /**
     * Convierte java.util.Date / java.sql.Date a java.time.LocalDate de forma
     * segura
     */
    private static LocalDate toLocalDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Convierte java.util.Date a java.sql.Date
     */
    private static java.sql.Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
 
    
    private void configurarSpinnersDashboard() {
        java.util.Date hoy = new java.util.Date();

        // Spinner de años
        AñoInicio.setModel(new javax.swing.SpinnerDateModel(hoy, null, null, java.util.Calendar.YEAR));
        AñoFin.setModel(new javax.swing.SpinnerDateModel(hoy, null, null, java.util.Calendar.YEAR));

        JSpinner.DateEditor editorAñoInicio = new JSpinner.DateEditor(AñoInicio, "yyyy");
        AñoInicio.setEditor(editorAñoInicio);
        JSpinner.DateEditor editorAñoFin = new JSpinner.DateEditor(AñoFin, "yyyy");
        AñoFin.setEditor(editorAñoFin);

        // Spinner de día/mes
        DiaMesInicio.setModel(new javax.swing.SpinnerDateModel(hoy, null, null, java.util.Calendar.DAY_OF_MONTH));
        DiaMesFin.setModel(new javax.swing.SpinnerDateModel(hoy, null, null, java.util.Calendar.DAY_OF_MONTH));

        JSpinner.DateEditor editorDiaMesInicio = new JSpinner.DateEditor(DiaMesInicio, "dd-MMM");
        DiaMesInicio.setEditor(editorDiaMesInicio);
        JSpinner.DateEditor editorDiaMesFin = new JSpinner.DateEditor(DiaMesFin, "dd-MMM");
        DiaMesFin.setEditor(editorDiaMesFin);
    }

    private void confirmarSeleccionFechasPastel() {
        try {
            java.util.Date fechaAñoInicio = (java.util.Date) AñoInicio.getValue();
            java.util.Date fechaAñoFin = (java.util.Date) AñoFin.getValue();
            java.util.Date fechaDiaMesInicio = (java.util.Date) DiaMesInicio.getValue();
            java.util.Date fechaDiaMesFin = (java.util.Date) DiaMesFin.getValue();

            LocalDate inicio = LocalDate.of(
                    toLocalDate(fechaAñoInicio).getYear(),
                    toLocalDate(fechaDiaMesInicio).getMonth(),
                    toLocalDate(fechaDiaMesInicio).getDayOfMonth()
            );

            LocalDate fin = LocalDate.of(
                    toLocalDate(fechaAñoFin).getYear(),
                    toLocalDate(fechaDiaMesFin).getMonth(),
                    toLocalDate(fechaDiaMesFin).getDayOfMonth()
            );

            if (inicio.isAfter(fin)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de inicio no puede ser posterior a la fecha final.",
                        "Rango inválido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            crearGraficoPastelRecetasPorEstado(inicio, fin);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar las fechas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            //  Llamar al controlador para crear el gráfico
            JFreeChart chart = control.crearGraficoPastelRecetasPorEstado(fechaInicio, fechaFin);

            // Crear un ChartPanel que lo contenga
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setMouseWheelEnabled(true);
            chartPanel.setPreferredSize(new java.awt.Dimension(
                    PanelRecetas.getWidth(),
                    PanelRecetas.getHeight()
            ));

            // Reemplazar el contenido del panel
            PanelRecetas.removeAll();
            PanelRecetas.setLayout(new java.awt.BorderLayout());
            PanelRecetas.add(chartPanel, java.awt.BorderLayout.CENTER);

            //  Refrescar el panel
            PanelRecetas.validate();
            PanelRecetas.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el gráfico: " + ex.getMessage(),
                    "Error de base de datos",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public DefaultTableModel crearTablaMedicamentosPorMes(
            LocalDate inicio, LocalDate fin,
            List<String> seleccionados, List<Receta> listaRecetas) {

        // Validar entradas
        if (listaRecetas == null || listaRecetas.isEmpty()) {
            return new DefaultTableModel(new Object[][]{}, new String[]{"Medicamento"});
        }

        //  Construcción dinámica de columnas: Año-Mes
        List<String> columnas = new ArrayList<>();
        columnas.add("Medicamento");

        LocalDate fecha = inicio.withDayOfMonth(1);
        while (!fecha.isAfter(fin)) {
            columnas.add(fecha.getYear() + "-" + String.format("%02d", fecha.getMonthValue()));
            fecha = fecha.plusMonths(1);
        }

        DefaultTableModel modelo = new DefaultTableModel(columnas.toArray(), 0);

        //  Llenar las filas por medicamento
        for (String med : seleccionados) {
            List<Object> fila = new ArrayList<>();
            fila.add(med);

            fecha = inicio.withDayOfMonth(1);
            while (!fecha.isAfter(fin)) {
                int cantidad = 0;

                for (Receta r : listaRecetas) {
                    if (r.getFechaEmision() == null) {
                        continue;
                    }

                    // Convertir a LocalDate (según el tipo real)
                    LocalDate fechaEmision;
                    if (r.getFechaEmision() instanceof java.sql.Date) {
                        fechaEmision = ((java.sql.Date) r.getFechaEmision()).toLocalDate();
                    } else {
                        fechaEmision = r.getFechaEmision().toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                    }

                    // Comparar mes y año
                    if (fechaEmision.getYear() == fecha.getYear()
                            && fechaEmision.getMonthValue() == fecha.getMonthValue()) {

                        // Buscar el medicamento en las indicaciones
                        for (Indicaciones i : r.getIndicaciones()) {
                            if (i.getMedicamento() != null
                                    && med.equals(i.getMedicamento().getNombre())) {
                                cantidad += i.getCantidad();
                            }
                        }
                    }
                }

                fila.add(cantidad);
                fecha = fecha.plusMonths(1);
            }

            modelo.addRow(fila.toArray());
        }

        return modelo;
    }

    private void cargarMedicamentosComboBox() {
        jComboBoxMedicamentos.removeAllItems();
        for (Medicamento m : control.obtenerTodosMedicamentos()) {
            jComboBoxMedicamentos.addItem(m.getNombre());
        }
    }

    private void agregarMedicamentoSeleccionado() throws SQLException {
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

    private void refrescarTablaMedicamentos() throws SQLException {
        LocalDate inicio = LocalDate.of(
                toLocalDate((java.util.Date) AñoInicio.getValue()).getYear(),
                toLocalDate((java.util.Date) DiaMesInicio.getValue()).getMonth(),
                1
        );

        LocalDate fin = LocalDate.of(
                toLocalDate((java.util.Date) AñoFin.getValue()).getYear(),
                toLocalDate((java.util.Date) DiaMesFin.getValue()).getMonth(),
                1
        );

        DefaultTableModel modelo = crearTablaMedicamentosPorMes(
                inicio,
                fin,
                medicamentosSeleccionados,
                control.obtenerTodasRecetas()
        );
        tblMedicamentosGrafico.setModel(modelo);
    }

    private void generarGraficoMedicamentos() {
        try {
            if (medicamentosSeleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debe agregar al menos un medicamento para generar el gráfico.",
                        "Aviso",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.util.Date fechaAñoInicio = (java.util.Date) AñoInicio.getValue();
            java.util.Date fechaAñoFin = (java.util.Date) AñoFin.getValue();
            java.util.Date fechaDiaMesInicio = (java.util.Date) DiaMesInicio.getValue();
            java.util.Date fechaDiaMesFin = (java.util.Date) DiaMesFin.getValue();

            LocalDate inicio = LocalDate.of(
                    toLocalDate(fechaAñoInicio).getYear(),
                    toLocalDate(fechaDiaMesInicio).getMonth(),
                    1
            );

            LocalDate fin = LocalDate.of(
                    toLocalDate(fechaAñoFin).getYear(),
                    toLocalDate(fechaDiaMesFin).getMonth(),
                    1
            );

            if (inicio.isAfter(fin)) {
                JOptionPane.showMessageDialog(this,
                        "La fecha de inicio no puede ser posterior a la fecha final.",
                        "Rango inválido",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFreeChart chart = control.crearGraficoLineaMedicamentos(
                    inicio,
                    fin,
                    medicamentosSeleccionados,
                    control.obtenerTodasRecetas()
            );

            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setMouseWheelEnabled(true);
            chartPanel.setPreferredSize(new java.awt.Dimension(
                    PanelMedicamentos.getWidth(),
                    PanelMedicamentos.getHeight()
            ));

            PanelMedicamentos.removeAll();
            PanelMedicamentos.setLayout(new java.awt.BorderLayout());
            PanelMedicamentos.add(chartPanel, java.awt.BorderLayout.CENTER);
            PanelMedicamentos.validate();
            PanelMedicamentos.repaint();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al generar el gráfico: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


//====================Historico==================
    private void cargarRecetaDesdeTabla() {
        int fila = TablaRecetas.getSelectedRow();
        if (fila >= 0) {
            String codigo = TablaRecetas.getValueAt(fila, 0).toString();
            Receta receta = control.buscarReceta(codigo);

            if (receta != null) {
                estado.setModel(receta);
                cambiarModoVista();
                actualizarComponentes();
                DefaultTableModel modelo = (DefaultTableModel) TablaIndicaciones.getModel();
                modelo.setRowCount(0);
                for (Indicaciones ind : receta.getIndicaciones()) {
                    modelo.addRow(new Object[]{
                        ind.getMedicamento().getNombre(),
                        ind.getCantidad(),
                        ind.getIndicaciones(),
                        ind.getDuracion()
                    });
                }
            }
        }
    }

    private void actualizarTablaRecetas() {
        try {
            List<Receta> recetas = control.obtenerTodasRecetas();
            DefaultTableModel modelo = (DefaultTableModel) TablaRecetas.getModel();
            modelo.setRowCount(0);
            if (recetas != null) {
                for (Receta r : recetas) {
                    modelo.addRow(new Object[]{
                        r.getCodReceta(),
                        r.getPaciente() != null ? r.getPaciente().getNombre() : "Sin paciente",
                        r.getMedico() != null ? r.getMedico().getNombre() : "Sin médico",
                        r.getFechaEmision(),
                        r.getFechaRetiro() != null ? r.getFechaRetiro() : "No retirado",
                        r.getEstado()
                    });
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar las recetas: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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

    private void asignarIconosPestanas() {
        int tamañoIcono = 18;

        // Mapa de panel -> icono
        Map<javax.swing.JPanel, FontAwesomeSolid> iconos = new HashMap<>();
        iconos.put(PestañaAcercaDe, FontAwesomeSolid.INFO_CIRCLE);
        iconos.put(PestañaDashboard, FontAwesomeSolid.TACHOMETER_ALT);
        iconos.put(PestañaHistorico, FontAwesomeSolid.HISTORY);
        iconos.put(PestañaPrescribir, FontAwesomeSolid.FILE_PRESCRIPTION);

        // Asignamos iconos
        for (int i = 0; i < VentanaMedico.getTabCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) VentanaMedico.getComponentAt(i);
            if (iconos.containsKey(panel)) {
                FontIcon icon = FontIcon.of(iconos.get(panel), tamañoIcono);
                VentanaMedico.setIconAt(i, icon);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        BotonEnviar = new javax.swing.JButton();
        BotonRecibir = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        UsuariosConectados = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        VentanaMedico = new javax.swing.JTabbedPane();
        PestañaPrescribir = new javax.swing.JPanel();
        RecetaMedica = new javax.swing.JPanel();
        FechaRetiro = new javax.swing.JLabel();
        NomPaciente = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        mostrarNombre = new javax.swing.JTextPane();
        listMedicamentos = new javax.swing.JScrollPane();
        TablaMedicamentosReceta = new javax.swing.JTable();
        SpinnerFechaRetiro = new javax.swing.JSpinner();
        Control = new javax.swing.JPanel();
        BotonBuscarPaciente = new javax.swing.JButton();
        BotonAgregarMedicamento = new javax.swing.JButton();
        AjustePrescrib = new javax.swing.JPanel();
        BotonEliminarPresc = new javax.swing.JButton();
        BotonGuardarPresc = new javax.swing.JButton();
        BotonLimpiarPresc = new javax.swing.JButton();
        BotonDetallesPresc = new javax.swing.JButton();
        PestañaDashboard = new javax.swing.JPanel();
        PanelDatos = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        AñoInicio = new javax.swing.JSpinner();
        AñoFin = new javax.swing.JSpinner();
        DiaMesInicio = new javax.swing.JSpinner();
        DiaMesFin = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jComboBoxMedicamentos = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
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
        jLabel6 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Médico");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), "Usuarios", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        jPanel1.setMaximumSize(new java.awt.Dimension(243, 325));
        jPanel1.setPreferredSize(new java.awt.Dimension(243, 200));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Activos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        BotonEnviar.setText("Enviar");
        BotonEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEnviarActionPerformed(evt);
            }
        });

        BotonRecibir.setText("Recibir");
        BotonRecibir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonRecibirActionPerformed(evt);
            }
        });

        UsuariosConectados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ID", "Mensaje"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(UsuariosConectados);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BotonEnviar)
                        .addGap(18, 18, 18)
                        .addComponent(BotonRecibir))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonEnviar)
                    .addComponent(BotonRecibir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(67, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 243, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 20, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        VentanaMedico.setToolTipText("");
        VentanaMedico.setMaximumSize(new java.awt.Dimension(800, 600));
        VentanaMedico.setMinimumSize(new java.awt.Dimension(900, 600));
        VentanaMedico.setName("Admisni"); // NOI18N
        VentanaMedico.setPreferredSize(new java.awt.Dimension(900, 600));
        VentanaMedico.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                VentanaMedicoAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });

        PestañaPrescribir.setLayout(new java.awt.GridBagLayout());

        RecetaMedica.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Receta Médica", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        FechaRetiro.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        FechaRetiro.setText("Fecha de retiro");

        NomPaciente.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        NomPaciente.setText("Paciente ");

        mostrarNombre.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        mostrarNombre.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        mostrarNombre.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                mostrarNombreAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        jScrollPane3.setViewportView(mostrarNombre);

        TablaMedicamentosReceta.setBorder(new javax.swing.border.MatteBorder(null));
        TablaMedicamentosReceta.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Medicamento", "Presentación", "Cantidad", "Indicación", "Días"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        listMedicamentos.setViewportView(TablaMedicamentosReceta);

        SpinnerFechaRetiro.setModel(new javax.swing.SpinnerDateModel());

        javax.swing.GroupLayout RecetaMedicaLayout = new javax.swing.GroupLayout(RecetaMedica);
        RecetaMedica.setLayout(RecetaMedicaLayout);
        RecetaMedicaLayout.setHorizontalGroup(
            RecetaMedicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RecetaMedicaLayout.createSequentialGroup()
                .addGap(79, 79, 79)
                .addComponent(FechaRetiro)
                .addGap(18, 18, 18)
                .addComponent(SpinnerFechaRetiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(166, 166, 166)
                .addComponent(NomPaciente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, RecetaMedicaLayout.createSequentialGroup()
                .addContainerGap(165, Short.MAX_VALUE)
                .addComponent(listMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(163, 163, 163))
        );
        RecetaMedicaLayout.setVerticalGroup(
            RecetaMedicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(RecetaMedicaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(RecetaMedicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(RecetaMedicaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(FechaRetiro)
                        .addComponent(NomPaciente))
                    .addComponent(SpinnerFechaRetiro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(listMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 159;
        gridBagConstraints.ipady = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 6, 0, 6);
        PestañaPrescribir.add(RecetaMedica, gridBagConstraints);

        Control.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Control", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N
        Control.setLayout(new java.awt.GridBagLayout());

        BotonBuscarPaciente.setText("Buscar Paciente");
        BotonBuscarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBuscarPacienteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 184, 30, 0);
        Control.add(BotonBuscarPaciente, gridBagConstraints);

        BotonAgregarMedicamento.setText("Agregar Medicamento");
        BotonAgregarMedicamento.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonAgregarMedicamentoActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(24, 150, 30, 189);
        Control.add(BotonAgregarMedicamento, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 93;
        gridBagConstraints.ipady = -23;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(16, 6, 0, 6);
        PestañaPrescribir.add(Control, gridBagConstraints);
        Control.getAccessibleContext().setAccessibleName("Prescribir");

        AjustePrescrib.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Ajustar Prescripción", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        BotonEliminarPresc.setText("Eliminar Medicamento");
        BotonEliminarPresc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEliminarPrescActionPerformed(evt);
            }
        });

        BotonGuardarPresc.setText("Guardar");
        BotonGuardarPresc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarPrescActionPerformed(evt);
            }
        });

        BotonLimpiarPresc.setText("Limpiar ");
        BotonLimpiarPresc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonLimpiarPrescActionPerformed(evt);
            }
        });

        BotonDetallesPresc.setText("Detalles");
        BotonDetallesPresc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonDetallesPrescActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout AjustePrescribLayout = new javax.swing.GroupLayout(AjustePrescrib);
        AjustePrescrib.setLayout(AjustePrescribLayout);
        AjustePrescribLayout.setHorizontalGroup(
            AjustePrescribLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(AjustePrescribLayout.createSequentialGroup()
                .addGap(169, 169, 169)
                .addComponent(BotonGuardarPresc)
                .addGap(18, 18, 18)
                .addComponent(BotonEliminarPresc)
                .addGap(18, 18, 18)
                .addComponent(BotonLimpiarPresc)
                .addGap(18, 18, 18)
                .addComponent(BotonDetallesPresc)
                .addContainerGap(287, Short.MAX_VALUE))
        );
        AjustePrescribLayout.setVerticalGroup(
            AjustePrescribLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, AjustePrescribLayout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(AjustePrescribLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonEliminarPresc)
                    .addComponent(BotonGuardarPresc)
                    .addComponent(BotonLimpiarPresc)
                    .addComponent(BotonDetallesPresc))
                .addGap(37, 37, 37))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 281;
        gridBagConstraints.ipady = 34;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(18, 6, 111, 6);
        PestañaPrescribir.add(AjustePrescrib, gridBagConstraints);

        VentanaMedico.addTab("Prescribir", PestañaPrescribir);

        PestañaDashboard.setEnabled(false);
        PestañaDashboard.setMaximumSize(new java.awt.Dimension(767, 767));

        PanelDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        jLabel1.setText("Desde");

        jLabel2.setText("Hasta");

        AñoInicio.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.YEAR));

        AñoFin.setModel(new javax.swing.SpinnerDateModel(new java.util.Date(), null, null, java.util.Calendar.YEAR));

        DiaMesInicio.setModel(new javax.swing.SpinnerDateModel());

        DiaMesFin.setModel(new javax.swing.SpinnerDateModel());

        jLabel3.setText("Año");

        jLabel4.setText("Día - Mes");

        jLabel8.setText("Medicamentos");

        jComboBoxMedicamentos.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton1.setText("Gráfrico Medicamentos");
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
        jScrollPane1.setViewportView(tblMedicamentosGrafico);

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
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(AñoInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AñoFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(DiaMesInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(DiaMesFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelDatosLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(BotonSeleccionFechas)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 105, Short.MAX_VALUE)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDatosLayout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBoxMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(BotonAgregarMedicamentoComboBox)
                        .addGap(100, 100, 100))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jButton1)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24))
        );
        PanelDatosLayout.setVerticalGroup(
            PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelDatosLayout.createSequentialGroup()
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel8)
                    .addComponent(jComboBoxMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BotonAgregarMedicamentoComboBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelDatosLayout.createSequentialGroup()
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(DiaMesInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(AñoInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(12, 12, 12)
                        .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(AñoFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(DiaMesFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PanelDatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BotonSeleccionFechas)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        PanelMedicamentos.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Medicamentos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

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

        PanelRecetas.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Recetas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        javax.swing.GroupLayout PanelRecetasLayout = new javax.swing.GroupLayout(PanelRecetas);
        PanelRecetas.setLayout(PanelRecetasLayout);
        PanelRecetasLayout.setHorizontalGroup(
            PanelRecetasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );
        PanelRecetasLayout.setVerticalGroup(
            PanelRecetasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 284, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PestañaDashboardLayout = new javax.swing.GroupLayout(PestañaDashboard);
        PestañaDashboard.setLayout(PestañaDashboardLayout);
        PestañaDashboardLayout.setHorizontalGroup(
            PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaDashboardLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PestañaDashboardLayout.createSequentialGroup()
                        .addComponent(PanelMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(73, 73, 73)
                        .addComponent(PanelRecetas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(PanelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PestañaDashboardLayout.setVerticalGroup(
            PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaDashboardLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(PanelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(PanelRecetas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(PanelMedicamentos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(107, Short.MAX_VALUE))
        );

        VentanaMedico.addTab("Dashboard", PestañaDashboard);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Listado", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 1, 12))); // NOI18N

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap(99, Short.MAX_VALUE)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 682, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(94, 94, 94))
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PestañaHistoricoLayout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PestañaHistoricoLayout.setVerticalGroup(
            PestañaHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaHistoricoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(215, Short.MAX_VALUE))
        );

        VentanaMedico.addTab("Historico ", PestañaHistorico);

        PestañaAcercaDe.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Hospital Benjamín Nuñez");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 309;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(12, 170, 0, 0);
        PestañaAcercaDe.add(jLabel6, gridBagConstraints);

        jLabel5.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Sistema de Prescripción y Despacho de Medicamentos ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 36;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 170, 0, 0);
        PestañaAcercaDe.add(jLabel5, gridBagConstraints);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/3.png"))); // NOI18N
        jLabel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipady = -9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 88, 42, 89);
        PestañaAcercaDe.add(jLabel7, gridBagConstraints);

        VentanaMedico.addTab("Acerca de", PestañaAcercaDe);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(VentanaMedico, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(VentanaMedico, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 511, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void VentanaMedicoAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_VentanaMedicoAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_VentanaMedicoAncestorAdded

    private void BotonVerIndicacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonVerIndicacionesActionPerformed
        int fila = TablaRecetas.getSelectedRow();
        if (fila >= 0) {
            String codigo = TablaRecetas.getValueAt(fila, 0).toString();
            Receta receta = control.buscarReceta(codigo);
            if (receta != null) {
                mostrarIndicacionesReceta(receta);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No se encontró la receta seleccionada.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una receta de la tabla.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BotonVerIndicacionesActionPerformed

    private void BotonAgregarMedicamentoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAgregarMedicamentoComboBoxActionPerformed
        try {
            // TODO add your handling code here:
            agregarMedicamentoSeleccionado();
        } catch (SQLException ex) {
            System.getLogger(VentanaMedico.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_BotonAgregarMedicamentoComboBoxActionPerformed

    private void BotonSeleccionFechasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSeleccionFechasActionPerformed
        // TODO add your handling code here:
        confirmarSeleccionFechasPastel();
    }//GEN-LAST:event_BotonSeleccionFechasActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        generarGraficoMedicamentos();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void BotonDetallesPrescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonDetallesPrescActionPerformed
        try {
            // TODO add your handling code here:
            mostrarDetalles();
        } catch (SQLException ex) {
            System.getLogger(VentanaMedico.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }//GEN-LAST:event_BotonDetallesPrescActionPerformed

    private void BotonLimpiarPrescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonLimpiarPrescActionPerformed
        // TODO add your handling code here:
        limpiarCampos();
    }//GEN-LAST:event_BotonLimpiarPrescActionPerformed

    private void BotonGuardarPrescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarPrescActionPerformed
        // TODO add your handling code here:
        guardarPrescripcion();
    }//GEN-LAST:event_BotonGuardarPrescActionPerformed

    private void BotonEliminarPrescActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEliminarPrescActionPerformed
        int filaSeleccionada = TablaMedicamentosReceta.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un medicamento para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmar la eliminación
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar este medicamento de la receta?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Eliminar de la lista temporal
        if (recetaActual.getInditemporal() != null && filaSeleccionada < recetaActual.getInditemporal().size()) {
            recetaActual.getInditemporal().remove(filaSeleccionada);
        }

        // Eliminar del modelo de la tabla
        DefaultTableModel modelo = (DefaultTableModel) TablaMedicamentosReceta.getModel();
        modelo.removeRow(filaSeleccionada);
    }//GEN-LAST:event_BotonEliminarPrescActionPerformed

    private void BotonAgregarMedicamentoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonAgregarMedicamentoActionPerformed
        // TODO add your handling code here:
        abrirBuscarMedicamento();
    }//GEN-LAST:event_BotonAgregarMedicamentoActionPerformed

    private void BotonBuscarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBuscarPacienteActionPerformed
        // TODO add your handling code here:
        abrirBuscarPaciente();
    }//GEN-LAST:event_BotonBuscarPacienteActionPerformed

    private void mostrarNombreAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_mostrarNombreAncestorAdded
        // TODO add your handling code here:
    }//GEN-LAST:event_mostrarNombreAncestorAdded

    private void BotonEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEnviarActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) UsuariosConectados.getModel();
        String destinatario = null;

        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean seleccionado = (Boolean) model.getValueAt(i, 1);
            if (seleccionado != null && seleccionado) {
                if (destinatario != null) {
                    JOptionPane.showMessageDialog(this, "Seleccione solo un usuario a la vez.");
                    return; // sale porque hay más de uno marcado
                }
                destinatario = (String) model.getValueAt(i, 0);
            }
        }

        if (destinatario == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario para enviar el mensaje.");
            return;
        }

        String mensaje = JOptionPane.showInputDialog(this, "Ingrese el mensaje para " + destinatario + ":");
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return; // si cancela o está vacío, no hace nada
        }

        proxy.enviarMensaje(destinatario, mensaje);
        JOptionPane.showMessageDialog(this, "Mensaje enviado a " + destinatario);

        // Limpiar el checkbox para que quede desmarcado
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(false, i, 1);
        }
    }//GEN-LAST:event_BotonEnviarActionPerformed

    private void BotonRecibirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonRecibirActionPerformed
        // TODO add your handling code here:
        List<String> msgs = proxy.getMensajesPendientes();
        if (msgs.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay mensajes nuevos.");
            return;
        }
        String todos = String.join("\n", msgs);
        JOptionPane.showMessageDialog(this, todos, "Mensajes recibidos", JOptionPane.INFORMATION_MESSAGE);
        proxy.limpiarMensajesPendientes();
    }//GEN-LAST:event_BotonRecibirActionPerformed
    /*    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
     */

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
            Modelo modelo = null;
            try {
                modelo = new Modelo();
            } catch (SQLException ex) {
                Logger.getLogger(VentanaMedico.class.getName()).log(Level.SEVERE, null, ex);
            }
            Control controlador = new Control(modelo);
            Medico med = new Medico();
            try {
                new VentanaMedico(controlador, med).setVisible(true);
            } catch (SQLException ex) {
                System.getLogger(VentanaMedico.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel AjustePrescrib;
    private javax.swing.JSpinner AñoFin;
    private javax.swing.JSpinner AñoInicio;
    private javax.swing.JButton BotonAgregarMedicamento;
    private javax.swing.JButton BotonAgregarMedicamentoComboBox;
    private javax.swing.JButton BotonBuscarPaciente;
    private javax.swing.JButton BotonDetallesPresc;
    private javax.swing.JButton BotonEliminarPresc;
    private javax.swing.JButton BotonEnviar;
    private javax.swing.JButton BotonGuardarPresc;
    private javax.swing.JButton BotonLimpiarPresc;
    private javax.swing.JButton BotonRecibir;
    private javax.swing.JButton BotonSeleccionFechas;
    private javax.swing.JButton BotonVerIndicaciones;
    private javax.swing.JPanel Control;
    private javax.swing.JSpinner DiaMesFin;
    private javax.swing.JSpinner DiaMesInicio;
    private javax.swing.JLabel FechaRetiro;
    private javax.swing.JLabel NomPaciente;
    private javax.swing.JPanel PanelDatos;
    private javax.swing.JPanel PanelMedicamentos;
    private javax.swing.JPanel PanelRecetas;
    private javax.swing.JPanel PestañaAcercaDe;
    private javax.swing.JPanel PestañaDashboard;
    private javax.swing.JPanel PestañaHistorico;
    private javax.swing.JPanel PestañaPrescribir;
    private javax.swing.JPanel RecetaMedica;
    private javax.swing.JSpinner SpinnerFechaRetiro;
    private javax.swing.JTable TablaIndicaciones;
    private javax.swing.JTable TablaMedicamentosReceta;
    private javax.swing.JTable TablaRecetas;
    private javax.swing.JTable UsuariosConectados;
    private javax.swing.JTabbedPane VentanaMedico;
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBoxMedicamentos;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane listMedicamentos;
    private javax.swing.JTextPane mostrarNombre;
    private javax.swing.JTable tblMedicamentosGrafico;
    // End of variables declaration//GEN-END:variables

    private ServiceProxy proxy;
    private final Control control;
    private FormHandler estado;
    private Medico medicoActual;
    private Receta recetaActual;  //instancia local
    private Indicaciones nuevasIndicaciones; //instancia local
    private final List<String> medicamentosSeleccionados = new ArrayList<>();

};
