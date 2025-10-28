package com.mycompany.ProyectoII;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "receta")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Receta {

    @DatabaseField(generatedId = true, columnName = "id")
    private int id;

    @DatabaseField(columnName = "Codigo", canBeNull = false)
    private String codReceta;

    @DatabaseField(columnName = "Estado", canBeNull = false)
    private String estado;  

    @DatabaseField(columnName = "FechaEmision", canBeNull = false)
    private Date fechaEmision;

    @DatabaseField(columnName = "FechaRetiro", canBeNull = false)
    private Date fechaRetiro;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "Paciente_Cedula", canBeNull = false)
    private Paciente paciente;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "Medico_Cedula", canBeNull = false)
    private Medico medico;

    @ForeignCollectionField(eager = true)
    private ForeignCollection<Indicaciones> indicaciones;

    private List<Indicaciones> inditemporal= new ArrayList<>();
public Receta(String codReceta, Paciente paciente, Medico medico, Date fechaEmision, Date fechaRetiro, String estado) {
    this.codReceta = codReceta;
    this.paciente = paciente;
    this.medico = medico;
    this.fechaEmision = fechaEmision;
    this.fechaRetiro = fechaRetiro;
    this.estado = estado;
   
}

    
//---------------------------------------------

       

     public void agregarIndicaciones(Indicaciones nuevaIndicacion) {
        if (this.indicaciones != null) {
            this.indicaciones.add(nuevaIndicacion); // receta viene de BD
        } else {
            inditemporal.add(nuevaIndicacion); // receta nueva en memoria
        }
        // asegura la relación recíproca
        nuevaIndicacion.setReceta(this);
    }

    public void ModificarIndicaciones(String codigoMedicamento, String nuevomed, int cantidad,
            String indicaciones, int duracionDias, List<Medicamento> medicamentosdisp) {
        Medicamento nuevoMedicamento = null;
        for (Medicamento m : medicamentosdisp) {
            if (m.getCodigo().equals(nuevomed)) {
                nuevoMedicamento = m;
            }
        }
        // busca y modifica
        if (this.indicaciones != null) {
            for (Indicaciones i : this.indicaciones) {
                if (i.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                    i.setMedicamento(nuevoMedicamento);
                    i.setCantidad(cantidad);
                    i.setIndicaciones(indicaciones);
                    i.setDuracion(duracionDias);
                }
            }
        }
        // también revisa la lista temporal si es necesario
        for (Indicaciones i : this.inditemporal) {
            if (i.getMedicamento().getCodigo().equals(codigoMedicamento)) {
                i.setMedicamento(nuevoMedicamento);
                i.setCantidad(cantidad);
                i.setIndicaciones(indicaciones);
                i.setDuracion(duracionDias);
            }
        }
    }

    public void finalizarReceta() {
        LocalDate hoy = LocalDate.now();
        this.fechaEmision = Date.valueOf(hoy);
        LocalDate retiro = hoy.plusDays(3);
        this.fechaRetiro = Date.valueOf(retiro);
        this.estado = "CONFECCIONADA"; 
    }


//    public Receta buscarPorCodigo(String codReceta) {
//        for (Receta r : listaRecetas) {
//            if (r.getCodReceta().equals(codReceta)) {
//                return r;
//            }
//        }
//        return null;
//    }
//
//    public boolean agregarReceta(Receta r) {
//        return listaRecetas.add(r);
//    }
//
//    public boolean borrarReceta(String codReceta) {
//        Receta r = buscarPorCodigo(codReceta);
//        return r != null && listaRecetas.remove(r);
//    }
//    
//    public int cantidadRecetas(){
//        return listaRecetas.size();
//    }
//
//    public void modificarEstadoReceta(String codReceta, String nuevoEstado) {
//        Receta r = buscarPorCodigo(codReceta);
//        if (r != null) {
//            r.setEstado(nuevoEstado);
//        }
//    }
//
//    public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
//        PieDataset dataset = crearDatasetRecetasPorEstado(fechaInicio, fechaFin);
//        JFreeChart chart = ChartFactory.createPieChart(
//                "Recetas (" + fechaInicio + " a " + fechaFin + ")",
//                dataset,
//                true,                 // leyenda
//                true,                 // tooltips
//                false                 // URLs
//        );
//        PiePlot plot = (PiePlot) chart.getPlot();
//        plot.setNoDataMessage("No hay recetas registradas");
//        plot.setCircular(true);
//        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
//                "{0} = {1} ({2})",
//                new DecimalFormat("0"),    
//                new DecimalFormat("0.0%")    
//        ));
//        plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
//        plot.setSimpleLabels(false); 
//        plot.setLabelGap(0.02);     
//        plot.setInteriorGap(0.04);  
//        plot.setLegendLabelToolTipGenerator(new StandardPieSectionLabelGenerator("Cantidad: {1}"));
//        return chart;
//    }
//    
//    private PieDataset crearDatasetRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
//        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
//        if (listaRecetas == null || listaRecetas.isEmpty()) {
//            return dataset;
//        }
//        Map<String, Long> conteo = listaRecetas.stream()
//                .filter(r -> {
//                    LocalDate fecha = r.getFechaEmision();
//                    return fecha != null
//                            && (fecha.isEqual(fechaInicio) || fecha.isAfter(fechaInicio))
//                            && (fecha.isEqual(fechaFin) || fecha.isBefore(fechaFin));
//                })
//                .collect(Collectors.groupingBy(
//                        r -> {
//                            Object e = r.getEstado();
//                            return (e == null) ? "Sin estado" : e.toString();
//                        },
//                        Collectors.counting()
//                ));
//        conteo.forEach(dataset::setValue);
//        return dataset;
//    }
//    
//    public TimeSeriesCollection crearDatasetMedicamentosPorMes(
//            LocalDate fechaInicio, LocalDate fechaFin, List<String> medicamentosSeleccionados, List<Receta> listaRecetas) {
//        TimeSeriesCollection dataset = new TimeSeriesCollection();
//        for (String nombreMed : medicamentosSeleccionados) {
//            TimeSeries serie = new TimeSeries(nombreMed);
//            LocalDate fecha = fechaInicio.withDayOfMonth(1);
//            while (!fecha.isAfter(fechaFin)) {
//                int cantidad = 0;
//                for (Receta r : listaRecetas) {
//                    LocalDate fechaEmision = r.getFechaEmision();
//                    if ((fechaEmision.getYear() == fecha.getYear()) && (fechaEmision.getMonthValue() == fecha.getMonthValue())) {
//                        for (Indicaciones i : r.getIndicaciones()) {
//                            if (i.getMedicamento().getNombre().equals(nombreMed)) {
//                                cantidad += i.getCantidad();
//                            }
//                        }
//                    }
//                }
//                serie.add(new Month(fecha.getMonthValue(), fecha.getYear()), cantidad);
//                fecha = fecha.plusMonths(1);
//            }
//            dataset.addSeries(serie);
//        }
//        return dataset;
//    }
//    
//    public JFreeChart crearGraficoLineaMedicamentos(
//            LocalDate inicio, LocalDate fin, List<String> seleccionados, List<Receta> listaRecetas) {
//        TimeSeriesCollection dataset = crearDatasetMedicamentosPorMes(inicio, fin, seleccionados, listaRecetas);
//        JFreeChart chart = ChartFactory.createTimeSeriesChart(
//                "Medicamentos despachados por mes",
//                "Mes",
//                "Cantidad",
//                dataset,
//                true,
//                true,
//                false
//        );
//        XYPlot plot = chart.getXYPlot();
//        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
//        renderer.setDefaultShapesVisible(true);
//        renderer.setDefaultShapesFilled(true);
//        plot.setRenderer(renderer);
//        return chart;
//    }
    

//    @Override
//    public String toString() {
//        StringBuilder salida = new StringBuilder();
//        for (Receta r : listaRecetas) {
//            salida.append(r.toString()).append("\n");
//        }
//        return salida.toString();
//    }
    
}

