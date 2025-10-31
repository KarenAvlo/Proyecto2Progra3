
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

