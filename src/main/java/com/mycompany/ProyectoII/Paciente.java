
package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "paciente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Paciente {

    @DatabaseField(id = true, columnName = "Cedula")
    private String cedula;

    @DatabaseField(columnName = "Nombre")
    private String nombre;

    @DatabaseField(columnName = "Telefono")
    private String telefono;

    @DatabaseField(columnName = "Fecha Nacimiento")
    private Date fechaNacimiento;  // java.sql.Date para compatibilidad con MySQL
    
    @DatabaseField(columnName = "Estado")
    private boolean estado;
    
        public Paciente(String cedula, String nombre, String telefono, Date fechaNacimiento) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = true; // siempre activo al crear
    }
}





