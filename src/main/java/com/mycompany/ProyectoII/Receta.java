package com.mycompany.ProyectoII;

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
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Receta {

    @DatabaseField(id 
    = true, columnName = "codReceta")
    private String codReceta;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "paciente_cedula")
    private Paciente paciente;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "medico_cedula")
    private Medico medico;

    @ForeignCollectionField(eager = true)
    private List<Indicaciones> indicaciones = new ArrayList<>();

    @DatabaseField(columnName = "fechaEmision")
    private Date fechaEmision;

    @DatabaseField(columnName = "fechaRetiro")
    private Date fechaRetiro;

    @DatabaseField(columnName = "estado")
    private String estado;
}


/*

       

    public void ModificarIndicaciones(String codigoMedicamento, String nuevomed, int cantidad,
            String indicaciones, int duracionDias, List<Medicamento> medicamentosdisp) {
        Medicamento nuevoMedicamento = null;
        for (Medicamento m : medicamentosdisp) {
            if (m.getCodigo().equals(nuevomed)) {
                nuevoMedicamento = m;
            }
        }
        for (Indicaciones i : this.indicaciones) {
            if (i.getMedicamento().getCodigo().equals(codigoMedicamento)) { // buscamos el medicamento por codigo el que deseamos cambiar
                i.setMedicamento(nuevoMedicamento);
                i.setCantidad(cantidad);
                i.setIndicaciones(indicaciones);
                i.setDuracion(duracionDias);
            }
        }
    }

    public void finalizarReceta() {
        this.fechaEmision= Date.now();
        this.fechaRetiro = fechaEmision.plusDays(3); //tres dias despues de emitida
        this.estado = "CONFECCIONADA"; 
    }
    
    public void agregarIndicaciones(Indicaciones nuevaIndicacion) {
        this.indicaciones.add(nuevaIndicacion);
    }


    public Receta buscarPorCodigo(String codReceta) {
        for (Receta r : listaRecetas) {
            if (r.getCodReceta().equals(codReceta)) {
                return r;
            }
        }
        return null;
    }

    public boolean agregarReceta(Receta r) {
        return listaRecetas.add(r);
    }

    public boolean borrarReceta(String codReceta) {
        Receta r = buscarPorCodigo(codReceta);
        return r != null && listaRecetas.remove(r);
    }
    
    public int cantidadRecetas(){
        return listaRecetas.size();
    }

    public void modificarEstadoReceta(String codReceta, String nuevoEstado) {
        Receta r = buscarPorCodigo(codReceta);
        if (r != null) {
            r.setEstado(nuevoEstado);
        }
    }

    public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
        PieDataset dataset = crearDatasetRecetasPorEstado(fechaInicio, fechaFin);
        JFreeChart chart = ChartFactory.createPieChart(
                "Recetas (" + fechaInicio + " a " + fechaFin + ")",
                dataset,
                true,                 // leyenda
                true,                 // tooltips
                false                 // URLs
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
    
    private PieDataset crearDatasetRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        if (listaRecetas == null || listaRecetas.isEmpty()) {
            return dataset;
        }
        Map<String, Long> conteo = listaRecetas.stream()
                .filter(r -> {
                    LocalDate fecha = r.getFechaEmision();
                    return fecha != null
                            && (fecha.isEqual(fechaInicio) || fecha.isAfter(fechaInicio))
                            && (fecha.isEqual(fechaFin) || fecha.isBefore(fechaFin));
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
    
    public TimeSeriesCollection crearDatasetMedicamentosPorMes(
            LocalDate fechaInicio, LocalDate fechaFin, List<String> medicamentosSeleccionados, List<Receta> listaRecetas) {
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        for (String nombreMed : medicamentosSeleccionados) {
            TimeSeries serie = new TimeSeries(nombreMed);
            LocalDate fecha = fechaInicio.withDayOfMonth(1);
            while (!fecha.isAfter(fechaFin)) {
                int cantidad = 0;
                for (Receta r : listaRecetas) {
                    LocalDate fechaEmision = r.getFechaEmision();
                    if ((fechaEmision.getYear() == fecha.getYear()) && (fechaEmision.getMonthValue() == fecha.getMonthValue())) {
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
        TimeSeriesCollection dataset = crearDatasetMedicamentosPorMes(inicio, fin, seleccionados, listaRecetas);
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Medicamentos despachados por mes",
                "Mes",
                "Cantidad",
                dataset,
                true,
                true,
                false
        );
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultShapesFilled(true);
        plot.setRenderer(renderer);
        return chart;
    }
    

    @Override
    public String toString() {
        StringBuilder salida = new StringBuilder();
        for (Receta r : listaRecetas) {
            salida.append(r.toString()).append("\n");
        }
        return salida.toString();
    }
    
    @XmlElement(name = "receta")
    private List<Receta> listaRecetas = new ArrayList<>();

*/

