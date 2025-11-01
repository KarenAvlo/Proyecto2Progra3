package com.mycompany.ProyectoII;


import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    private List<Indicaciones> inditemporal = new ArrayList<>();

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
    
}

