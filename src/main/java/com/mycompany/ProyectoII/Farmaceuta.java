package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
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
//@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)   // incluye los campos heredados de Persona
public class Farmaceuta extends Persona {
    
    @DatabaseField(id = true, columnName = "Cedula")
    private String cedula;

    @DatabaseField(columnName = "Nombre")
    private String nombre;

    @DatabaseField(columnName = "clave")
    private String clave;

    @DatabaseField(columnName = "Estado")
    private boolean estado;

    public Farmaceuta(String cedula, String nombre, String clave, boolean estado) {
        super(cedula, nombre, clave);
        this.estado = estado;
    }


//----------------------------------------

    
}
