
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
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString(callSuper = true)
public class Administrativo extends Persona {

    // Si necesitás campos adicionales específicos de Administrativo, se agregan aquí.
    // Por ejemplo:
    // @DatabaseField
    // private String departamento;
    public Administrativo(String ced, String nombre, String clave) {
        super(ced, nombre, clave);
    }

    // 🧠 Métodos de negocio (no de persistencia)
    // El administrativo puede gestionar listas o catálogos de usuarios:
    // crear, modificar o eliminar médicos, pacientes o farmacéuticos.
    
    /*
       public Administrativo buscarPorCedula(String cedula) {
        Administrativo f1 = null;

        for (Administrativo f : ListaAdministrativos) {
            if (f.getCedula().equals(cedula)) {
                f1 = f;
            }
        }
        return f1;
    }

    public Administrativo buscarNombre(String nombre) {
        Administrativo f1 = null;

        for (Administrativo f : ListaAdministrativos) {
            if (f.getNombre().equals(nombre)) {
                f1 = f;
            }
        }
        return f1;
    }

    public boolean InclusionAdministrativo(String id, String nombre) {
        if(!existeAdmi(id)){
         Administrativo fa= new Administrativo(id, nombre, id);
         return ListaAdministrativos.add(fa);
        }
        return false;        
    }

    public boolean existeAdmi(String ced) {
        return buscarPorCedula(ced) != null;
    }

    public boolean BorrarAdministrativo(String id) {
        Administrativo fa = this.buscarPorCedula(id);
        return ListaAdministrativos.remove(fa);
    }

    public void ConsultaAdministrativo(String cedula) {
        Administrativo fa = this.buscarPorCedula(cedula);
        fa.toString();
    }

    public void ModificarIdAdministrativo(String id, String nuevoId) {
        Administrativo fa = this.buscarPorCedula(id);
        fa.setCedula(nuevoId);
    }

    public void ModificarClaveAdministrativo(String id, String clave) {
        Administrativo fa = this.buscarPorCedula(id);
        fa.setClave(clave);
    }
    
    
    */
    
}
