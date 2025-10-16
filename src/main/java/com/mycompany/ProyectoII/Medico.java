
package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "medico")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper=true)
@ToString

public class Medico extends Persona{

    @DatabaseField(columnName = "Especialidad")
    private String especialidad;

    @DatabaseField(columnName = "Estado")
    private boolean estado;

    // Constructor completo
    public Medico(String cedula, String nombre, String clave, String especialidad) {
        super(cedula, nombre, clave);
        this.especialidad = especialidad;
        this.estado = true; // ´pr defecto siempre available
    }

}

/*

    public Receta prescribirReceta(String codReceta, String idPaciente,
            List<Paciente> lp, List<Receta> lre) {
        Paciente p = null;
        for (Paciente pp : lp) {
            if (pp.getCedula() != null && pp.getCedula().equals(idPaciente)) {
                p = pp;
            }
        }
        Receta re = new Receta(codReceta, p, this, new ArrayList<>(), null, null, "Inprocess");
        re.finalizarReceta();
        lre.add(re);
        return re;
    }

    public void CrearIndicacion(Receta receta, String codMed, int cant,
            String indicaciones, int duracion,
            List<Medicamento> medicamentosdisp) {
        Medicamento medicamento = null;
        for (Medicamento m : medicamentosdisp) {
            if (m.getCodigo().equals(codMed)) {
                medicamento = m;
            }
        }
        if (medicamento != null) {
            Indicaciones i = new Indicaciones(medicamento, cant, indicaciones, duracion);
            receta.getIndicaciones().add(i);
        }
    }

    public void modificarReceta(String codReceta, String codigoMedicamento, String nuevomed, int cantidad,
            String indicaciones, int duracionDias, List<Medicamento> medicamentosdisp,
            List<Receta> recetas) {
        Receta re = null;
        for (Receta r : recetas) {
            if (r.getCodReceta().equals(codReceta)) {
                re = r;
            }
        }
        if (re != null) {
            re.ModificarIndicaciones(codigoMedicamento, nuevomed, cantidad, indicaciones, duracionDias,
                    medicamentosdisp);
        }
    }


*/