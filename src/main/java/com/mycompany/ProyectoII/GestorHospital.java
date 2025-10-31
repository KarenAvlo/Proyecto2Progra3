package com.mycompany.ProyectoII;

import com.mycompany.ProyectoII.DAO.AdministrativoDAO;
import com.mycompany.ProyectoII.DAO.FarmaceutaDAO;
import com.mycompany.ProyectoII.DAO.MedicamentoDAO;
import com.mycompany.ProyectoII.DAO.MedicoDAO;
import com.mycompany.ProyectoII.DAO.PacienteDAO;
import com.mycompany.ProyectoII.DAO.RecetaDAO;
import com.mycompany.ProyectoII.DAO.IndicacionesDAO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class GestorHospital {

    // DAOs
    private final PacienteDAO pacienteDAO;
    private final MedicoDAO medicoDAO;
    private final FarmaceutaDAO farmaceutaDAO;
    private final AdministrativoDAO administrativoDAO;
    private final RecetaDAO recetaDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final IndicacionesDAO indicacionesDAO;

    // Constructor
    public GestorHospital() throws SQLException {
        this.pacienteDAO = new PacienteDAO();
        this.medicoDAO = new MedicoDAO();
        this.farmaceutaDAO = new FarmaceutaDAO();
        this.administrativoDAO = new AdministrativoDAO();
        this.recetaDAO = new RecetaDAO();
        this.medicamentoDAO = new MedicamentoDAO();
        this.indicacionesDAO = new IndicacionesDAO();
    }

    // --------------------------
    // CRUD Paciente
    // --------------------------
    public void agregarPaciente(Paciente paciente) throws SQLException {
        pacienteDAO.add(paciente);
    }

    public Paciente buscarPaciente(String cedula) throws SQLException {
        return pacienteDAO.findById(cedula);
    }

    public List<Paciente> obtenerTodosPacientes() throws SQLException {
        return pacienteDAO.findAll();
    }

    public void actualizarPaciente(Paciente paciente) throws SQLException {
        pacienteDAO.update(paciente);
    }

    public void eliminarPaciente(String cedula) throws SQLException {
        pacienteDAO.delete(cedula);
    }

    public Paciente buscarPorCedula(String cedula) throws SQLException {
        return pacienteDAO.findById(cedula);
    }

    // --------------------------
    // Funcionalidades específicas de Paciente
    // --------------------------
    public boolean inclusionPaciente(String id, String nombre, String nacimiento, String numero) throws SQLException {
        if (buscarPorCedula(id) == null) {
            Paciente p = new Paciente(id, nombre, numero, Date.valueOf(nacimiento));
            pacienteDAO.add(p);
            return true;
        }
        return false;
    }

    // Consultar paciente (solo imprime información)
    public void consultaPaciente(String cedula) throws SQLException {
        Paciente p = buscarPorCedula(cedula);
        if (p != null) {
            System.out.println(p.toString());
        }
    }

    // Modificar cédula de paciente (si realmente querés cambiar la PK)
    public void modificarIdPaciente(String id, String nuevoId) throws SQLException {
        Paciente p = buscarPorCedula(id);
        if (p != null) {
            p.setCedula(nuevoId);
            pacienteDAO.update(p);
        }
    }

    // Obtener todos los pacientes como String
    @Override
    public String toString() {
        try {
            StringBuilder salida = new StringBuilder();
            for (Paciente p : pacienteDAO.findAll()) {
                salida.append(p.toString()).append("\n");
            }
            return salida.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
//administrativo
     public boolean actualizarClaveAdmi(String cedula, String nuevaClave) throws SQLException {
     return administrativoDAO.actualizarClave(cedula, nuevaClave);
    }
    
    
    // --------------------------
    // CRUD Medico
    // --------------------------
    public void agregarMedico(Medico medico) throws SQLException {
        medicoDAO.add(medico);
    }

    public Medico buscarMedico(String cedula) throws SQLException {
        return medicoDAO.findById(cedula);
    }

    public List<Medico> obtenerTodosMedicos() throws SQLException {
        return medicoDAO.findAll();
    }

    public void actualizarMedico(Medico medico) throws SQLException {
        medicoDAO.update(medico);
    }

    public void eliminarMedico(String cedula) throws SQLException {
        medicoDAO.delete(cedula);
    }
     public boolean actualizarClave(String cedula, String nuevaClave) throws SQLException {
     return medicoDAO.actualizarClave(cedula, nuevaClave);
    }

    // --------------------------
    // Funcionalidades específicas de Medico
    // --------------------------
    public Receta prescribirReceta(String codReceta, String cedulaPaciente, String cedulaMedico) throws SQLException {
        // Buscar paciente y medico en la base de datos
        Paciente paciente = pacienteDAO.findById(cedulaPaciente);
        Medico medico = medicoDAO.findById(cedulaMedico);

        if (paciente == null || medico == null) {
            return null; // no se puede crear receta si no existen paciente o médico
        }

        Receta receta = new Receta(codReceta, paciente, medico, null, null, "Inprocess");
        receta.finalizarReceta(); // setea fechaEmision, fechaRetiro y estado

        recetaDAO.add(receta); // guardar receta en la base de datos
        return receta;
    }

    public void crearIndicacion(String codReceta, String codMedicamento, int cantidad,
            String indicaciones, int duracion) throws SQLException {
        Receta receta = recetaDAO.findByCodigo(codReceta);
        if (receta == null) {
            return;
        }

        Medicamento medicamento = medicamentoDAO.findById(codMedicamento);
        if (medicamento == null) {
            return;
        }

        Indicaciones i = new Indicaciones(medicamento, cantidad, indicaciones,
                duracion, receta);
        receta.getIndicaciones().add(i);

        recetaDAO.update(receta); // actualizar la receta con la nueva indicación
    }

    public void modificarReceta(String codReceta, String codigoMedicamento, String nuevoMedicamento,
            int cantidad, String indicaciones, int duracion) throws SQLException {
        Receta receta = null;
        try {
            receta = recetaDAO.findByCodigo(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }

        if (receta == null) {
            return;
        }

        receta.ModificarIndicaciones(codigoMedicamento, nuevoMedicamento, cantidad, indicaciones, duracion,
                medicamentoDAO.findAll()); // pasamos todos los medicamentos disponibles
        recetaDAO.update(receta); // actualizar en base de datos
    }

    // --------------------------
    // CRUD Farmaceuta
    // --------------------------
    public void agregarFarmaceuta(Farmaceuta farmaceuta) throws SQLException {
        farmaceutaDAO.add(farmaceuta);
    }

    public Farmaceuta buscarFarmaceutaPorCedula(String cedula) throws SQLException {
        return farmaceutaDAO.findById(cedula);
    }

    public List<Farmaceuta> obtenerTodosFarmaceutas() throws SQLException {
        return farmaceutaDAO.findAll();
    }

    public void actualizarFarmaceuta(Farmaceuta farmaceuta) throws SQLException {
        farmaceutaDAO.update(farmaceuta);
    }

    public void eliminarFarmaceuta(String cedula) throws SQLException {
        farmaceutaDAO.delete(cedula);
    }
    
     public boolean actualizarClaveFarma(String cedula, String nuevaClave) throws SQLException {
     return farmaceutaDAO.actualizarClave(cedula, nuevaClave);
    }

    // --------------------------
    // Funcionalidades específicas de Farmaceuta
    // --------------------------
    // Procesar receta (pone en "En Proceso" si cumple condiciones)
    public boolean ProcesarReceta(String idPaciente, String codRec) {
        Receta receta = null;
        try {
            receta = recetaDAO.findByCodigo(codRec);
        } catch (SQLException ex) {
            ex.getMessage();
        }

        if (receta != null && receta.getEstado().equals("CONFECCIONADA")
                && (receta.getFechaRetiro().equals(LocalDate.now())
                || receta.getFechaRetiro().equals(LocalDate.now().plusDays(1))
                || receta.getFechaRetiro().equals(LocalDate.now().plusDays(2))
                || receta.getFechaRetiro().equals(LocalDate.now().plusDays(3)))) {
            receta.setEstado("En Proceso");
            return true;
        }
        return false;
    }

// Enlistar receta (pone en "Lista" si ya está en "En Proceso")
    public void enlistarReceta(String idPaciente, String codRec) {
        if (ProcesarReceta(idPaciente, codRec)) {
            Receta receta = null;
            try {
                receta = recetaDAO.findByCodigo(codRec);
            } catch (SQLException ex) {
                ex.getMessage();
            }
            if (receta != null) {
                receta.setEstado("Lista");
            }
        }
    }

// Despachar receta (pone en "Entregada" si está en "Lista")
    public void despacharReceta(String idPaciente, String codRec) {

        Receta receta = null;
        try {
            receta = recetaDAO.findByCodigo(codRec);
        } catch (SQLException ex) {
            ex.getMessage();
        }

        if (receta != null && "Lista".equals(receta.getEstado())) {
            receta.setEstado("Entregada");
            try {
                recetaDAO.update(receta);
            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
    }

// Buscar por nombre (opcional, devuelve lista)
    public List<Farmaceuta> buscarFarmaceutaPorNombre(String nombre) throws SQLException {
        return farmaceutaDAO.getDao().queryForEq("nombre", nombre);
    }

// Modificar clave de farmaceuta
    public void modificarClaveFarmaceuta(String cedula, String nuevaClave) throws SQLException {
        Farmaceuta f = farmaceutaDAO.findById(cedula);
        if (f != null) {
            f.setClave(nuevaClave);
            farmaceutaDAO.update(f);
        }
    }

    // --------------------------
    // CRUD Medicamento
    // --------------------------
    public void agregarMedicamento(Medicamento medicamento) throws SQLException {
        medicamentoDAO.add(medicamento);
    }

    public Medicamento buscarMedicamentoPorCodigo(String codigo) throws SQLException {
        return medicamentoDAO.findById(codigo);
    }

    public List<Medicamento> obtenerTodosMedicamentos() throws SQLException {
        return medicamentoDAO.findAll();
    }

    public void actualizarMedicamento(Medicamento medicamento) throws SQLException {
        medicamentoDAO.update(medicamento);
    }

    public void eliminarMedicamento(String codigo) throws SQLException {
        medicamentoDAO.delete(codigo);
    }

    
    // --------------------------
    // CRUD Indicaciones
    // --------------------------
    public void agregarIndicacion(Indicaciones i) throws SQLException {
        indicacionesDAO.add(i);
    }

    public void actualizarIndicacion(Indicaciones i) throws SQLException {
        indicacionesDAO.update(i);
    }

    public void eliminarIndicacion(Indicaciones i) throws SQLException {
        indicacionesDAO.delete(i);
    }

    // Agregar indicación a una receta
    public void agregarIndicacion(Receta receta, Indicaciones i) {
        try {
            i.setReceta(receta);
            indicacionesDAO.add(i);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --------------------------
    // CRUD Receta
    // --------------------------
    public void agregarReceta(Receta receta) throws SQLException {
        recetaDAO.add(receta);
    }

    public Receta buscarRecetaPorCodigo(String codReceta) throws SQLException {
        return recetaDAO.findByCodigo(codReceta);
    }

    public List<Receta> obtenerTodasRecetas() throws SQLException {
        return recetaDAO.findAll();
    }

    public void actualizarReceta(Receta receta) throws SQLException {
        recetaDAO.update(receta);
    }

    public void eliminarReceta(String codReceta) throws SQLException {
        recetaDAO.deleteByCodigo(codReceta);
    }
    
    public int cantidadRecetas()throws SQLException{
        List<Receta> recetas = recetaDAO.findAll();
        return recetas.size();
    }
    
    // --------------------------
    // Funcionalidades específicas de Receta
    // --------------------------
    public void modificarEstadoReceta(String codReceta, String nuevoEstado) {
        Receta receta = null;
        try {
            recetaDAO.findByCodigo(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        if (receta != null) {
            receta.setEstado(nuevoEstado);

            try {
                recetaDAO.update(receta);
            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
    }

    // Agregar indicaciones a receta existente (si no se hizo con crearIndicacion de médico)
    public void agregarIndicacionesReceta(String codReceta, Indicaciones indicacion) throws SQLException {
        Receta receta = recetaDAO.findByCodigo(codReceta);
        if (receta != null) {
            receta.agregarIndicaciones(indicacion);
            recetaDAO.update(receta);
        }
    }

    // Buscar recetas por paciente
    public List<Receta> buscarRecetasPorPaciente(String cedulaPaciente) throws SQLException {
        Paciente paciente = pacienteDAO.findById(cedulaPaciente);
        if (paciente == null) {
            return new ArrayList<>();
        }
        return recetaDAO.getDao().queryForEq("Paciente_Cedula", cedulaPaciente);

    }

    // Buscar recetas por médico
    public List<Receta> buscarRecetasPorMedico(String cedulaMedico) throws SQLException {
        return recetaDAO.getDao().queryForEq("medico_cedula", cedulaMedico);
    }

    //Funcionalidades Extra del Sistema Hospitalario
    public Persona validarUsuario(String cedula, String clave) throws SQLException {
        // Buscar en Médicos
        Medico medico = medicoDAO.findById(cedula);
        if (medico != null && medico.getClave().equals(clave)) {
            return medico;
        }

        // Buscar en Farmaceutas
        Farmaceuta farmaceuta = farmaceutaDAO.findById(cedula);
        if (farmaceuta != null && farmaceuta.getClave().equals(clave)) {
            return farmaceuta;
        }

        // Buscar en Administrativos
        Administrativo administrativo = administrativoDAO.findById(cedula);
        if (administrativo != null && administrativo.getClave().equals(clave)) {
            return administrativo;
        }

        // Si no se encuentra en ninguna tabla
        return null;
    }
    
    private PieDataset crearDatasetRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        List<Receta> recetas = recetaDAO.findAll();

        if (recetas == null || recetas.isEmpty()) {
            return dataset;
        }

        Map<String, Long> conteo = recetas.stream()
                .filter(r -> {
                    Date fecha = r.getFechaEmision();
                    if (fecha == null) {
                        return false;
                    }

                    // Convertir a LocalDate
                    LocalDate fechaEmision;
                    if (fecha instanceof java.sql.Date) {
                        fechaEmision = ((java.sql.Date) fecha).toLocalDate();
                    } else {
                        fechaEmision = fecha.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                    }

                    // Comparar fechas
                    return (fechaEmision.isEqual(fechaInicio) || fechaEmision.isAfter(fechaInicio))
                            && (fechaEmision.isEqual(fechaFin) || fechaEmision.isBefore(fechaFin));
                })
                .collect(Collectors.groupingBy(
                        r -> {
                            Object e = r.getEstado();
                            return (e == null) ? "Sin estado" : e.toString();
                        },
                        Collectors.counting()
                ));

        conteo.forEach(dataset::setValue);
        return dataset;
    }

    public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        PieDataset dataset = crearDatasetRecetasPorEstado(fechaInicio, fechaFin);
        JFreeChart chart = ChartFactory.createPieChart(
                "Recetas (" + fechaInicio + " a " + fechaFin + ")",
                dataset,
                true,
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setNoDataMessage("No hay recetas registradas");
        plot.setCircular(true);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {1} ({2})",
                new DecimalFormat("0"),
                new DecimalFormat("0.0%")
        ));
        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        plot.setSimpleLabels(false);
        plot.setLabelGap(0.02);
        plot.setInteriorGap(0.04);
        plot.setLegendLabelToolTipGenerator(new StandardPieSectionLabelGenerator("Cantidad: {1}"));
        return chart;
    }

    public TimeSeriesCollection crearDatasetMedicamentosPorMes(
            LocalDate fechaInicio, LocalDate fechaFin,
            List<String> medicamentosSeleccionados, List<Receta> listaRecetas) {

        TimeSeriesCollection dataset = new TimeSeriesCollection();

        for (String nombreMed : medicamentosSeleccionados) {
            TimeSeries serie = new TimeSeries(nombreMed);
            LocalDate fecha = fechaInicio.withDayOfMonth(1);

            while (!fecha.isAfter(fechaFin)) {
                int cantidad = 0;

                for (Receta r : listaRecetas) {
                    Date fechaEmisionDate = r.getFechaEmision();
                    if (fechaEmisionDate == null) {
                        continue;
                    }

                    //  Convertir la fecha a LocalDate
                    LocalDate fechaEmision;
                    if (fechaEmisionDate instanceof java.sql.Date) {
                        fechaEmision = ((java.sql.Date) fechaEmisionDate).toLocalDate();
                    } else {
                        fechaEmision = fechaEmisionDate.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                    }

                    //  Comparar año y mes correctamente
                    if (fechaEmision.getYear() == fecha.getYear()
                            && fechaEmision.getMonthValue() == fecha.getMonthValue()) {

                        for (Indicaciones i : r.getIndicaciones()) {
                            if (i.getMedicamento().getNombre().equals(nombreMed)) {
                                cantidad += i.getCantidad();
                            }
                        }
                    }
                }

                serie.add(new Month(fecha.getMonthValue(), fecha.getYear()), cantidad);
                fecha = fecha.plusMonths(1);
            }

            dataset.addSeries(serie);
        }

        return dataset;
    }

    public JFreeChart crearGraficoLineaMedicamentos(
            LocalDate inicio, LocalDate fin, List<String> seleccionados, List<Receta> listaRecetas) {

        // Crear dataset con los datos de medicamentos
        TimeSeriesCollection dataset = crearDatasetMedicamentosPorMes(inicio, fin, seleccionados, listaRecetas);

        // Crear el gráfico de líneas
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Medicamentos despachados por mes",
                "Mes",
                "Cantidad",
                dataset,
                true,
                true,
                false
        );

        // Configurar el gráfico
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultShapesFilled(true);
        renderer.setDefaultStroke(new BasicStroke(2.0f));
        plot.setRenderer(renderer);

        // Formato de eje X (fecha)
        DateAxis ejeX = (DateAxis) plot.getDomainAxis();
        ejeX.setDateFormatOverride(new SimpleDateFormat("MMM yyyy"));

        // Si no hay datos, muestra aviso
        if (dataset.getSeriesCount() == 0) {
            chart.addSubtitle(new TextTitle("No hay datos disponibles para el rango seleccionado"));
        }

        return chart;
    }


    // --------------------------
    // Cierre de conexión
    // --------------------------
    public void close() throws Exception {
        pacienteDAO.close();
        medicoDAO.close();
        farmaceutaDAO.close();
        administrativoDAO.close();
        recetaDAO.close();
        medicamentoDAO.close();
        indicacionesDAO.close();
    }

}
