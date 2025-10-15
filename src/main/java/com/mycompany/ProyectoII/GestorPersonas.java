package com.mycompany.p1pro3;

import jakarta.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/* -------------------------------------------------------------------+
*                                                                     |
* (c) 2025                                                            |
* EIF206 - Programación 3                                             |
* 2do ciclo 2025                                                      |
* NRC 51189 – Grupo 05                                                |
* Proyecto 1                                                          |
*                                                                     |
* 2-0816-0954; Avilés López, Karen Minards                            |
* 4-0232-0641; Zárate Hernández, Nicolas Alfredo                      |
*                                                                     |
* versión 1.0.0 13-09-2005                                            |
*                                                                     |
* --------------------------------------------------------------------+
 */
@Getter
@Setter

public class GestorPersonas {

    public void cargarTodo(AdministrativoDAO gestorAdministrativos,
            FarmaceutaDAO gestorFarmaceutas,
            MedicoDAO gestorMedicos) throws JAXBException, IOException {

        AdministrativoDAO ga = AdministrativoDAO.cargarDesdeXML();
        if (ga != null) {
            personas.addAll(ga.getListaAdministrativos()); 
            gestorAdministrativos.getListaAdministrativos().addAll(ga.getListaAdministrativos());
        }

        FarmaceutaDAO gf = FarmaceutaDAO.cargarDesdeXML();
        if (gf != null) {
            personas.addAll(gf.getListaFarmaceutas());
            gestorFarmaceutas.getListaFarmaceutas().addAll(gf.getListaFarmaceutas());
        }

        MedicoDAO gm = MedicoDAO.cargarDesdeXML();
        if (gm != null) {
            personas.addAll(gm.getListaMedicos());
            gestorMedicos.getListaMedicos().addAll(gm.getListaMedicos());
        }
    }

    public Persona login(String cedula, String clave) {
        for (Persona p : personas) {
            if (p.getCedula().equals(cedula) && p.getClave().equals(clave)) {
                return p;
            }
        }
        return null;
    }

    public void distribuirEnGestores(MedicoDAO gm, FarmaceutaDAO gf, AdministrativoDAO ga) {
        for (Persona p : personas) {
            switch (p) {
                case Medico m -> gm.getListaMedicos().add(m);
                case Farmaceuta f -> gf.getListaFarmaceutas().add(f);
                case Administrativo a -> ga.getListaAdministrativos().add(a);
                default -> {
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Persona f : personas) {
            sb.append(f.toString()).append("\n");
        }
        return sb.toString();

    }

    //==============Atributos===========
    private List<Persona> personas = new ArrayList<>();

}
