
package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/* -------------------------------------------------------------------+
*                                                                     |
* (c) 2025                                                            |
* EIF206 - Programación 3                                             |
* 2do ciclo 2025                                                      |
* NRC 51189 – Grupo 05                                                |
* Proyecto 2                                                          |
*                                                                     |
* 2-0816-0954; Avilés López, Karen Minards                            |
* 4-0232-0641; Zárate Hernández, Nicolas Alfredo                      |
*                                                                     |
* versión 2.0.0 15-10-2025                                            |
*                                                                     |
* --------------------------------------------------------------------+
 */
@DatabaseTable(tableName = "administrativo")
//@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(callSuper = true)
public class Administrativo extends Persona {

    @DatabaseField(id = true, columnName = "Cedula")
    private String cedula;

    @DatabaseField(columnName = "Nombre")
    private String nombre;

    @DatabaseField(columnName = "Clave")
    private String clave;

    public Administrativo(String cedula, String nombre, String clave) {
        super(cedula, nombre, clave);
    }
}
