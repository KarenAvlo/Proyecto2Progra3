package com.mycompany.ProyectoII.vista;

import com.mycompany.ProyectoII.Indicaciones;
import com.mycompany.ProyectoII.Medicamento;

import com.mycompany.ProyectoII.Receta;
import com.mycompany.ProyectoII.control.Control;
import com.mycompany.ProyectoII.modelo.Modelo;
import cr.ac.una.gui.FormHandler;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
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
* Proyecto 1                                                          |
*                                                                     |
* 2-0816-0954; Avilés López, Karen Minards                            |
* 4-0232-0641; Zárate Hernández, Nicolas Alfredo                      |
*                                                                     |
* versión 1.0.0 13-09-2005                                            |
*                                                                     |
* --------------------------------------------------------------------+
*/
public class VentanaFarmaceuta extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VentanaFarmaceuta.class.getName());

    /**
     * Creates new form VentanaFarmaceutico
     */
    public VentanaFarmaceuta(Control control) {
        this.control = control;
        this.estado = new FormHandler();
        
        initComponents();
        this.setLocationRelativeTo(null); // aparece en el centro
        configurarListeners();
        init();
        
    }
    
    private void configurarListeners() {

        //================= Despacho ==================
        PestañaDespacho.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (estado.isViewing()) {
                    cargarRecetaDesdeTabla();
                }
            }
        });
        
        tblRecetas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarControles();
            }
        });
        
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
                filtrarReceta();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarControles();
                filtrarReceta();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarControles();
                filtrarReceta();
            }
            
        };

        // ====== Campos Despacho ======
        areaTxtBusCodigo.getDocument().addDocumentListener(listenerEdicion);
        areaTxtBusCodigo.getDocument().addDocumentListener(listenerBusqueda);

        // ====== Actualizar tablas al cambiar pestaña ======
        VentanaFarmaceuta.addChangeListener(e -> {
            int index = VentanaFarmaceuta.getSelectedIndex();
            actualizarControles(); // Mantener botones sincronizados
            actualizarCampos();
            switch (index) {
                case 0:
                    actualizarTablaRecetas();
                    break;
                case 1:
                    actualizarTablaMedicamentosDashboard();
                    break;
                case 2:
                    actualizarTablaRecetas2();
                    break;
            }
            actualizarControles(); // Mantener botones sincronizados
        });

        // ====== Inicializar tablas ======
        actualizarTablaMedicamentosDashboard();
        configurarTablaRecetaSelec();
        actualizarTablaRecetas();

        // ====== Cambiar a modo agregar al abrir ======
        asignarIconosPestanas();
        cambiarModoAgregar();
        actualizarComponentes();
        configurarSpinnersDashboard();
        cargarMedicamentosComboBox();
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
        
        int pestanaSeleccionada = VentanaFarmaceuta.getSelectedIndex();
        switch (pestanaSeleccionada) {
            case 0: // Despacho
                areaTxtBusCodigo.requestFocusInWindow();
                areaTxtBusCodigo.selectAll();
                break;
            case 1: // Dashboard
                //CedulaFtxt.requestFocusInWindow();
                //CedulaFtxt.selectAll();
                break;
            
        }
    }
    
    private void cambiarModoEditar() { //modo edicion
        if (estado.isViewing() && estado.getModel() != null) {
            estado.changeToEditMode();
            actualizarComponentes();
            
            int pestanaSeleccionada = VentanaFarmaceuta.getSelectedIndex();
            if (pestanaSeleccionada == 0) { // Médicos
                areaTxtBusCodigo.requestFocusInWindow();
                areaTxtBusCodigo.selectAll();
            }
        }
    }
    
    private void cambiarModoBuscar() { // no usado
//        estado.changeToSearchMode();
//        actualizarComponentes();
//        
//        int pestanaSeleccionada = VentanaFarmaceuta.getSelectedIndex();
//        switch (pestanaSeleccionada) {
//            case 0:
//                break;
//
//        }
    }

    // -------------------------------------------------------------------------
    // ACTUALIZACIÓN DE COMPONENTES
    // -------------------------------------------------------------------------
    private void actualizarComponentes() {
        actualizarControles();
        actualizarCampos();
    }
    
    private void actualizarControles() {
        int pestanaSeleccionada = VentanaFarmaceuta.getSelectedIndex();
        
        switch (pestanaSeleccionada) {
            case 0: // Despacho
                boolean NohaytextoMedicoid = areaTxtBusCodigo.getText().trim().isEmpty();
                boolean SeleccionoReceta = tblRecetas.getSelectedRow() >= 0;
                boolean AgregoReceta = tblRecetaSelec.getRowCount() > 0;
                BotonSeleccionar.setEnabled(SeleccionoReceta);
                BotonGuardarCambio.setEnabled(AgregoReceta);
                BotonCancelarCambio.setEnabled(AgregoReceta);
                
                break;
            
            case 1: // DashBoard
                //boolean noHayTextoFarma = tblMedicamentosGrafico.i .isEmpty();
                break;

//            case 5: // Recetas
//                boolean NohaytextoRec = CodigoRecetastxt.getText().trim().isEmpty();
//                BotonBuscarReceta.setEnabled(!NohaytextoRec);
//                break;
        }
    }
    
    private void actualizarCampos() {
        int pestanaSeleccionada = VentanaFarmaceuta.getSelectedIndex();

        // Declarar todas las variables de modoEdicion antes del switch
        boolean modoEdicionMed = !estado.isViewing();
        boolean modoEdicionFarma = !estado.isViewing();
        boolean modoEdicionPac = !estado.isViewing();
        boolean modoEdicionMedic = !estado.isViewing();
        
        switch (pestanaSeleccionada) {
            case 0: // Médicos
                areaTxtBusCodigo.setEnabled(estado.getModel() == null || modoEdicionMed || estado.isModified());
                
                break;
            
            case 1: // Dashboard
//            BotonBuscarF.setEnabled(!CedulaFtxt2.getText().trim().isEmpty());
                
                break;

//            case 5: // Recetas falta
//                CodigoRecetastxt.setEnabled(true);
        
    

    ////                ResultadoRecetastxt.setEnabled(estado.isAdding() || estado.isSearching() || !estado.isViewing());
//                BotonBuscarReceta.setEnabled(!CodigoRecetastxt.getText().trim().isEmpty());
//                break;
        }
    }

    private void indicarCambios() {
        estado.setModified(true);
        actualizarControles();
    }
    
    private void limpiarCampos() {
        int pestanaSeleccionada = VentanaFarmaceuta.getSelectedIndex();
        switch (pestanaSeleccionada) {
            case 0:
                estado.setModel(null);
                areaTxtBusCodigo.setText("");
                DefaultTableModel modelo = (DefaultTableModel) tblRecetaSelec.getModel();
                modelo.setRowCount(0);
                actualizarTablaRecetas();
                cambiarModoAgregar();
                break;
            case 1: // DashBoard

                estado.setModel(null);
                estado.setModified(false); // Reinicia el estado para que botones no se habiliten solos
                cambiarModoAgregar();
                actualizarComponentes(); // Fuerza actualización de botones                
                break;
            
        }
    }

    // -------------------------------------------------------------------------
    // OPERACIONES CRUD
    // -------------------------------------------------------------------------
    private void cancelarOperacion() {
        
        areaTxtBusCodigo.setText("R");
        //quitamos la seleccion
        tblRecetas.clearSelection();
        
        //limpiamos la tabla de edicion
        DefaultTableModel modeloSelec=(DefaultTableModel) tblRecetaSelec.getModel();
        modeloSelec.setRowCount(0);
        
        //Reseteamos el estado actual
        estado.setModel(null);
        
        //actualizamos la interfaz
        cambiarModoVista();
        actualizarComponentes();
        
    }

    // =========================== Recetas ================================
    private void actualizarTablaRecetas() {
//        try {
//            List<Receta> recetas = control.listarRecetas();
//            DefaultTableModel modelo = (DefaultTableModel) tblRecetas.getModel();
//            modelo.setRowCount(0);
//            
//            if (recetas != null) {
//                for (Receta r : recetas) {
//                    modelo.addRow(new Object[]{
//                        r.getCodReceta(),
//                        r.getPaciente().getNombre(),
//                        r.getMedico().getNombre(),
//                        r.getFechaEmision(),
//                        r.getFechaRetiro(),
//                        r.getEstado()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            logger.log(java.util.logging.Level.SEVERE, "Error al actualizar tabla de recetas", ex);
//            JOptionPane.showMessageDialog(this, "Error al cargar las recetas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }
    
    private void actualizarTablaRecetas2() {
//        try {
//            List<Receta> recetas = control.listarRecetas();
//            DefaultTableModel modelo = (DefaultTableModel) TablaRecetas.getModel();
//            modelo.setRowCount(0);
//            
//            if (recetas != null) {
//                for (Receta r : recetas) {
//                    modelo.addRow(new Object[]{
//                        r.getCodReceta(),
//                        r.getPaciente().getNombre(),
//                        r.getMedico().getNombre(),
//                        r.getFechaEmision(),
//                        r.getFechaRetiro(),
//                        r.getEstado()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            logger.log(java.util.logging.Level.SEVERE, "Error al actualizar tabla de recetas", ex);
//            JOptionPane.showMessageDialog(this, "Error al cargar las recetas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
    }
    
    private void cargarRecetaDesdeTabla() {
//        int fila = TablaRecetas.getSelectedRow();
//        if (fila >= 0) {
//            String codigo = TablaRecetas.getValueAt(fila, 0).toString();
//            Receta receta = control.buscarReceta(codigo);
//            
//            if (receta != null) {
//                estado.setModel(receta);      // guardamos en el estado actual
//                cambiarModoVista();           // cambiamos a modo vista (como haces en otros módulos)
//                actualizarComponentes();      // actualiza botones/campos
//
//                // Además: limpiar tabla de indicaciones y llenarla con esta receta
//                DefaultTableModel modelo = (DefaultTableModel) TablaIndicaciones.getModel();
//                modelo.setRowCount(0);
//                
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
    
    private void cargarRecetaParaCambiarEstado() {
//        int fila = tblRecetas.getSelectedRow();
//        if (fila >= 0) {
//            String codigo = tblRecetas.getValueAt(fila, 0).toString();
//            Receta receta = control.buscarReceta(codigo);
//            
//            if (receta != null) {
//                estado.setModel(receta);      // guardamos en el estado actual
//                cambiarModoVista();           // cambiamos a modo vista 
//                actualizarComponentes();      // actualiza botones/campos
//
//                DefaultTableModel modelo = (DefaultTableModel) tblRecetaSelec.getModel();
//                
//                modelo.addRow(new Object[]{
//                    receta.getCodReceta(),
//                    receta.getPaciente().getNombre(),
//                    receta.getMedico().getNombre(),
//                    receta.getFechaEmision(),
//                    receta.getFechaRetiro(),
//                    receta.getEstado()});
//            }
//        }
    }
    
    private void filtrarReceta() {
//        String texto = areaTxtBusCodigo.getText().trim().toLowerCase();
//        
//        try {
//            // Obtén todas las recetas desde el control
//            List<Receta> recetas = control.listarRecetas();
//            DefaultTableModel modelo = (DefaultTableModel) tblRecetas.getModel();
//            modelo.setRowCount(0); // limpiar tabla antes de insertar
//
//            if (recetas != null) {
//                // Filtrar por código si el texto no está vacío
//                List<Receta> recetasFiltradas = recetas.stream()
//                        .filter(r -> r.getCodReceta().toLowerCase().contains(texto))
//                        .collect(Collectors.toList());
//
//                // Rellenar tabla con los resultados
//                for (Receta r : recetasFiltradas) {
//                    modelo.addRow(new Object[]{
//                        r.getCodReceta(),
//                        r.getPaciente().getNombre(),
//                        r.getMedico().getNombre(),
//                        r.getFechaEmision(),
//                        r.getFechaRetiro(),
//                        r.getEstado()
//                    });
//                }
//            }
//        } catch (Exception ex) {
//            logger.log(java.util.logging.Level.SEVERE, "Error al filtrar recetas", ex);
//            JOptionPane.showMessageDialog(this, "Error al filtrar recetas: " + ex.getMessage(),
//                    "Error", JOptionPane.ERROR_MESSAGE);
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

        VentanaFarmaceuta = new javax.swing.JTabbedPane();
        PestañaDespacho = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        txtBusCodigo = new javax.swing.JLabel();
        areaTxtBusCodigo = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblRecetas = new javax.swing.JTable();
        BotonSeleccionar = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblRecetaSelec = new javax.swing.JTable();
        BotonGuardarCambio = new javax.swing.JButton();
        BotonCancelarCambio = new javax.swing.JButton();
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
        setTitle("Farmaceutas");

        VentanaFarmaceuta.setPreferredSize(new java.awt.Dimension(900, 600));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Despacho de Recetas", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 12))); // NOI18N

        txtBusCodigo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        txtBusCodigo.setText("Búsqueda por código");

        areaTxtBusCodigo.setText("R");
        areaTxtBusCodigo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                areaTxtBusCodigoActionPerformed(evt);
            }
        });

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tblRecetas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Código", "Paciente", "Médico", "Emisión", "Retiro", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
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
        tblRecetas.setPreferredSize(new java.awt.Dimension(800, 600));
        jScrollPane2.setViewportView(tblRecetas);

        BotonSeleccionar.setText("Seleccionar");
        BotonSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSeleccionarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtBusCodigo)
                        .addGap(18, 18, 18)
                        .addComponent(areaTxtBusCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BotonSeleccionar)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 841, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBusCodigo)
                    .addComponent(areaTxtBusCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BotonSeleccionar)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        tblRecetaSelec.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null}
            },
            new String [] {
                "Codigo", "Paciente", "Médico", "Emisión", "Retiro", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblRecetaSelec);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 828, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
        );

        BotonGuardarCambio.setText("Guardar");
        BotonGuardarCambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonGuardarCambioActionPerformed(evt);
            }
        });

        BotonCancelarCambio.setText("Cancelar");
        BotonCancelarCambio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarCambioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addComponent(BotonGuardarCambio)
                        .addGap(18, 18, 18)
                        .addComponent(BotonCancelarCambio))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonGuardarCambio)
                    .addComponent(BotonCancelarCambio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PestañaDespachoLayout = new javax.swing.GroupLayout(PestañaDespacho);
        PestañaDespacho.setLayout(PestañaDespachoLayout);
        PestañaDespachoLayout.setHorizontalGroup(
            PestañaDespachoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaDespachoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PestañaDespachoLayout.setVerticalGroup(
            PestañaDespachoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaDespachoLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(136, Short.MAX_VALUE))
        );

        VentanaFarmaceuta.addTab("Despacho", PestañaDespacho);

        PestañaDashboard.setEnabled(false);
        PestañaDashboard.setMaximumSize(new java.awt.Dimension(767, 767));

        PanelDatos.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Datos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 119, Short.MAX_VALUE)
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

        PanelMedicamentos.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(41, 43, 45)), "Medicamentos", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        javax.swing.GroupLayout PanelMedicamentosLayout = new javax.swing.GroupLayout(PanelMedicamentos);
        PanelMedicamentos.setLayout(PanelMedicamentosLayout);
        PanelMedicamentosLayout.setHorizontalGroup(
            PanelMedicamentosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 385, Short.MAX_VALUE)
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
            .addGap(0, 0, Short.MAX_VALUE)
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
                .addGroup(PestañaDashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PestañaDashboardLayout.createSequentialGroup()
                        .addComponent(PanelMedicamentos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(PanelRecetas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(PanelDatos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(94, Short.MAX_VALUE))
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
                .addContainerGap(70, Short.MAX_VALUE))
        );

        VentanaFarmaceuta.addTab("Dashboard", PestañaDashboard);

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
                .addGap(81, 81, 81))
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
                .addContainerGap(49, Short.MAX_VALUE))
        );
        PestañaHistoricoLayout.setVerticalGroup(
            PestañaHistoricoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PestañaHistoricoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(173, Short.MAX_VALUE))
        );

        VentanaFarmaceuta.addTab("Histórico", PestañaHistorico);

        PestañaAcercaDe.setLayout(new java.awt.GridBagLayout());

        jLabel6.setFont(new java.awt.Font("Georgia", 1, 18)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Hospital Benjamín Nuñez");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 309;
        gridBagConstraints.ipady = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 178, 0, 0);
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
        gridBagConstraints.insets = new java.awt.Insets(6, 167, 0, 0);
        PestañaAcercaDe.add(jLabel5, gridBagConstraints);

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/5.png"))); // NOI18N
        jLabel7.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = -63;
        gridBagConstraints.ipady = -9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(6, 126, 44, 141);
        PestañaAcercaDe.add(jLabel7, gridBagConstraints);

        VentanaFarmaceuta.addTab("Acerca de", PestañaAcercaDe);

        getContentPane().add(VentanaFarmaceuta, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents


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

    private void areaTxtBusCodigoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_areaTxtBusCodigoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_areaTxtBusCodigoActionPerformed

    private void BotonSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSeleccionarActionPerformed
        // TODO add your handling code here:
        cargarRecetaParaCambiarEstado();
        actualizarControles();
    }//GEN-LAST:event_BotonSeleccionarActionPerformed

    private void BotonGuardarCambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonGuardarCambioActionPerformed
        // TODO add your handling code here:
//        try {
//            control.guardarRecetas();
//            // control.actualizarReceta(receta);
//        } catch (Exception ex) {
//            System.getLogger(VentanaFarmaceuta.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
//        }
//        limpiarCampos();
//        actualizarTablaRecetas();
        
    }//GEN-LAST:event_BotonGuardarCambioActionPerformed

    private void BotonCancelarCambioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarCambioActionPerformed
        // TODO add your handling code here:
        cancelarOperacion();
    }//GEN-LAST:event_BotonCancelarCambioActionPerformed

    private void BotonVerIndicacionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonVerIndicacionesActionPerformed
//        int fila = TablaRecetas.getSelectedRow();
//        if (fila >= 0) {
//            String codigo = TablaRecetas.getValueAt(fila, 0).toString(); // código de la receta
//            Receta receta = control.buscarReceta(codigo); // debes tener este método en tu controlador
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

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws SQLException {
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
        Modelo modelo = new Modelo();
        Control control = new Control(modelo);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new VentanaFarmaceuta(control).setVisible(true));
    }

    //=============================================== Despacho ================================================
    public void configurarTablaRecetaSelec() {
        // 1. Opciones para el combo
        String[] estados = {"PROCESO", "LISTA", "ENTREGADA"};
        JComboBox<String> comboEstado = new JComboBox<>(estados);

        // 2. Asignar el combo como editor de la última columna ("Estado")
        tblRecetaSelec.getColumnModel()
                .getColumn(5) // columna índice 5 = Estado
                .setCellEditor(new DefaultCellEditor(comboEstado));
        
        TableColumn colEstado = tblRecetaSelec.getColumnModel().getColumn(5);

        // Renderer para mostrar siempre el combo
        colEstado.setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JLabel label = new JLabel(value != null ? value.toString() : "");
            label.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 11));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
                label.setOpaque(true);
            }
            
            return label;
        });

        // Editor para editar realmente con el combo
        colEstado.setCellEditor(new DefaultCellEditor(new JComboBox<>(estados)));

        // 3. Escuchar cambios en la tabla para actualizar la receta seleccionada
        ((DefaultTableModel) tblRecetaSelec.getModel()).addTableModelListener(e -> {
            int fila = e.getFirstRow();
            int columna = e.getColumn();
            
            if (columna == 5 && fila >= 0) { // Si modificaron la columna Estado
                String nuevoEstado = tblRecetaSelec.getValueAt(fila, columna).toString();
                
                if (estado.getModel() instanceof Receta receta) {
                    receta.setEstado(nuevoEstado);
                    //control.actualizarReceta(receta);
                }
            }
        });
    }

    //=============================================== Dasboard ===============================================
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
//        JFreeChart chart = control.crearGraficoPastelRecetasPorEstado(inicio, fin);

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
//        JFreeChart chart = control.crearGraficoPastelRecetasPorEstado(fI, fF);
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
//                    LocalDate fechaEmision = r.getFechaEmision();
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
//        for (Medicamento m : control.ListarMedicamentos()) {
//            jComboBoxMedicamentos.addItem(m.getNombre());
//        }
    }

    // Acción del botón "Agregar medicamento"
    private void agregarMedicamentoSeleccionado() {
        String seleccionado = (String) jComboBoxMedicamentos.getSelectedItem();
        if (seleccionado != null && !medicamentosSeleccionados.contains(seleccionado)) {
            medicamentosSeleccionados.add(seleccionado);

            // Refrescar tabla y gráfico
            actualizarTablaMedicamentosDashboard();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ya has agregado este medicamento o no hay selección válida.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void actualizarTablaMedicamentosDashboard() {
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
//                control.ListarRecetas()
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
        
//        JFreeChart chart = control.crearGraficoLineaMedicamentos(
//                inicio,
//                fin,
//                medicamentosSeleccionados,
//                control.ListarRecetas()
//        );
        
        PanelMedicamentos.removeAll();
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setPreferredSize(new Dimension(PanelMedicamentos.getWidth(), PanelMedicamentos.getHeight()));
        PanelMedicamentos.setLayout(new BorderLayout());
//        PanelMedicamentos.add(chartPanel, BorderLayout.CENTER);
        PanelMedicamentos.validate();
        PanelMedicamentos.repaint();
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
//        JFreeChart chart = control.crearGraficoLineaMedicamentos(
//                inicio,
//                fin,
//                medicamentosSeleccionados, // usar la lista global
//                control.ListarRecetas()
//        );

        // 4. Mostrarlo en el PanelMedicamentos
//        ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setMouseWheelEnabled(true);
//        chartPanel.setPreferredSize(new java.awt.Dimension(
//                PanelMedicamentos.getWidth(),
//                PanelMedicamentos.getHeight()
//        ));
//        
        PanelMedicamentos.removeAll();
        PanelMedicamentos.setLayout(new java.awt.BorderLayout());
//        PanelMedicamentos.add(chartPanel, java.awt.BorderLayout.CENTER);
        PanelMedicamentos.validate();
        PanelMedicamentos.repaint();
    }

    //==============================================================================================
    private void asignarIconosPestanas() {
        int tamañoIcono = 18;

        // Mapa de panel -> icono
        Map<javax.swing.JPanel, FontAwesomeSolid> iconos = new HashMap<>();
        iconos.put(PestañaAcercaDe, FontAwesomeSolid.INFO_CIRCLE);
        iconos.put(PestañaDashboard, FontAwesomeSolid.TACHOMETER_ALT);
        iconos.put(PestañaHistorico, FontAwesomeSolid.HISTORY);
        iconos.put(PestañaDespacho, FontAwesomeSolid.PRESCRIPTION_BOTTLE);

        // Asignamos iconos
        for (int i = 0; i < VentanaFarmaceuta.getTabCount(); i++) {
            javax.swing.JPanel panel = (javax.swing.JPanel) VentanaFarmaceuta.getComponentAt(i);
            if (iconos.containsKey(panel)) {
                FontIcon icon = FontIcon.of(iconos.get(panel), tamañoIcono);
                VentanaFarmaceuta.setIconAt(i, icon);
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner AñoFin;
    private javax.swing.JSpinner AñoInicio;
    private javax.swing.JButton BotonAgregarMedicamentoComboBox;
    private javax.swing.JButton BotonCancelarCambio;
    private javax.swing.JButton BotonGuardarCambio;
    private javax.swing.JButton BotonSeleccionFechas;
    private javax.swing.JButton BotonSeleccionar;
    private javax.swing.JButton BotonVerIndicaciones;
    private javax.swing.JSpinner DiaMesFin;
    private javax.swing.JSpinner DiaMesInicio;
    private javax.swing.JPanel PanelDatos;
    private javax.swing.JPanel PanelMedicamentos;
    private javax.swing.JPanel PanelRecetas;
    private javax.swing.JPanel PestañaAcercaDe;
    private javax.swing.JPanel PestañaDashboard;
    private javax.swing.JPanel PestañaDespacho;
    private javax.swing.JPanel PestañaHistorico;
    private javax.swing.JTable TablaIndicaciones;
    private javax.swing.JTable TablaRecetas;
    private javax.swing.JTabbedPane VentanaFarmaceuta;
    private javax.swing.JTextField areaTxtBusCodigo;
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
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTable tblMedicamentosGrafico;
    private javax.swing.JTable tblRecetaSelec;
    private javax.swing.JTable tblRecetas;
    private javax.swing.JLabel txtBusCodigo;
    // End of variables declaration//GEN-END:variables

    private final Control control;
    private final FormHandler estado;
}
