
package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
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
* versión 2.0.0 06-11-2025                                            |
*                                                                     |
* --------------------------------------------------------------------+
 */
@DatabaseTable(tableName = "administrativo")
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Administrativo extends Persona {


    public Administrativo(String cedula, String nombre, String clave) {
        super(cedula, nombre, clave);
    }
}
