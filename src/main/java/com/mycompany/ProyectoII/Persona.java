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
@DatabaseField(id = true, columnName = "Cedula")
    private String cedula;

    @DatabaseField(columnName = "Nombre")
    private String nombre;

    @DatabaseField(columnName = "Clave")
    private String clave;
}
//👉 Ojo: aunque no tengas tabla persona, sí puedes usar @DatabaseField en la clase base para que las subclases hereden correctamente esos campos.