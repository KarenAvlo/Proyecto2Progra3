package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "farmaceuta")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)   // incluye los campos heredados de Persona
public class Farmaceuta extends Persona {

    public Farmaceuta(String cedula, String nombre, String clave) {
        super(cedula, nombre, clave);
    }

    public boolean ProcesarReceta(String idPaciente, String codRec, List<Paciente> p, List<Receta> re) {

        Receta receta = null;
        for (Receta r : re) {
            if (r.getCodReceta().equals(codRec)) {
                receta = r;
            }
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

    public void enlistarReceta(String idPaciente, String codRec, List<Paciente> p, List<Receta> re) {
        if (ProcesarReceta(idPaciente, codRec, p, re)) {
            Receta receta = null;
            for (Receta r : re) {
                if (r.getCodReceta().equals(codRec)) {
                    receta = r;
                }
            }
            if (receta != null) {
                receta.setEstado("Lista");
            }
        }
    }

    public void DespacharReceta(String idPaciente, String codRec, List<Paciente> p, List<Receta> re) {
        Receta receta = null;
        for (Receta r : re) {
            if (r.getCodReceta().equals(codRec)) {
                receta = r;
            }
        }
        if (receta != null && receta.getEstado().equals("Lista")) {
            receta.setEstado("Entregada");
        }
    }
    
    
}
