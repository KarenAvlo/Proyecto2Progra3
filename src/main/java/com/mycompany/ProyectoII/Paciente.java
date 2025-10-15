
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

    @DatabaseField(id = true, columnName = "cedula")
    private String cedula;

    @DatabaseField(columnName = "nombre")
    private String nombre;

    @DatabaseField(columnName = "telefono")
    private String telefono;

    @DatabaseField(columnName = "fechaNacimiento")
    private Date fechaNacimiento;  // java.sql.Date para compatibilidad con MySQL
}





