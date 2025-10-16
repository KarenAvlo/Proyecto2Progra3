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
@AllArgsConstructor        // Constructor con todos los campos
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
    private String estado;

}
