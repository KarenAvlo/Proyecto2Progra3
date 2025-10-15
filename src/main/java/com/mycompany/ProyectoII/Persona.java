package com.mycompany.ProyectoII;

import com.j256.ormlite.field.DatabaseField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class Persona {

    @DatabaseField(id = true, columnName = "cedula")
    private String cedula;

    @DatabaseField(columnName = "nombre")
    private String nombre;

    @DatabaseField(columnName = "clave")
    private String clave;
}