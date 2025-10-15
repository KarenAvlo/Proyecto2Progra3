
package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.mycompany.ProyectoII.Paciente;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "medicos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Medico extends Persona {

    public Medico(String cedula, String nombre, String especialidad, String clave) {
        super(cedula, nombre, clave);
        this.especialidad = especialidad;
    }

    @DatabaseField(columnName = "especialidad")
    private String especialidad;

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