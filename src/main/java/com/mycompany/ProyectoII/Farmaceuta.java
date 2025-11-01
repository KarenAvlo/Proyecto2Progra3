package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "farmaceuta")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Farmaceuta extends Persona {


    @DatabaseField(columnName = "Estado")
    private boolean estado;

    public Farmaceuta(String cedula, String nombre, String clave) {
        super(cedula, nombre, clave);
        this.estado = true;
    }    
}
