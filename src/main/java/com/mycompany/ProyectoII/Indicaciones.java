package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@DatabaseTable(tableName = "indicaciones")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Indicaciones {

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "CodigoMedicamento")
    private Medicamento medicamento;

    @DatabaseField(columnName = "Cantidad")
    private int cantidad;

    @DatabaseField(columnName = "Indicaciones")
    private String indicaciones;

    @DatabaseField(columnName = "Duracion")
    private int duracion;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "Receta_id")
    private Receta receta;  // para relacionar con la receta a la que pertenece
}
