package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "medicamento")
@NoArgsConstructor         // Constructor vacío (requerido por ORMLite)
@Getter
@Setter
@ToString
public class Medicamento {

    @DatabaseField(id = true, columnName = "Codigo")
    private String codigo;

    @DatabaseField(columnName = "Nombre")
    private String nombre;

    @DatabaseField(columnName = "Presentacion")
    private String presentacion;

    @DatabaseField(columnName = "Estado")
    private boolean estado;

    public Medicamento(String codigo, String nombre, String presentacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
        this.estado = true;

    }

}
