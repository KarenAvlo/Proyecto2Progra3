package com.mycompany.ProyectoII;

import cr.ac.una.util.xml.XMLUtils;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.FileNotFoundException;
import java.io.IOException;
import lombok.Data;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
@Data
@XmlRootElement(name = "medicamentos")
@XmlAccessorType(XmlAccessType.FIELD)

public class GestorMedicamentos {
//
//    public static GestorMedicamentos cargarDesdeXML() throws IOException, JAXBException {
//        try (InputStream is = GestorMedicamentos.class.getClassLoader().getResourceAsStream("medicamentos.xml")) {
//            if (is == null) {
//                throw new FileNotFoundException("No se encontró medicamentos.xml en resources");
//            }
//            return XMLUtils.loadFromXML(is, GestorMedicamentos.class);
//        }
//    }
//
//    public void guardar() throws Exception {
//        String ruta = "src/main/resources/medicamentos.xml";
//        try (PrintWriter salida = new PrintWriter(ruta)) {
//            salida.println(XMLUtils.toXMLString(this));
//        }
//    }
//
//    public Medicamento buscarPorCodigo(String codigo) {
//        Medicamento f1 = null;
//
//        for (Medicamento f : ListaMedicamentos) {
//            if (f.getCodigo().equals(codigo)) {
//                f1 = f;
//            }
//        }
//        return f1;
//    }
//
//    public Medicamento buscarNombre(String nombre) {
//        Medicamento f1 = null;
//
//        for (Medicamento f : ListaMedicamentos) {
//            if (f.getNombre().equals(nombre)) {
//                f1 = f;
//            }
//        }
//        return f1;
//    }
//
//    public boolean InclusionMedicamento(String id, String nombre, String presentacion) {
//        if (!existeMed(id)) {
//            Medicamento fa = new Medicamento(id, nombre, presentacion);
//
//            return ListaMedicamentos.add(fa);
//        }
//        return false;
//    }
//
//    public boolean existeMed(String id) {
//        return buscarPorCodigo(id) != null;
//    }
//
//    public boolean BorrarMedicamento(String id) {
//        Medicamento fa = this.buscarPorCodigo(id);
//        return ListaMedicamentos.remove(fa);
//    }
//
//    public void ConsultaMedicamento(String codigo) {
//        Medicamento fa = this.buscarPorCodigo(codigo);
//        fa.toString();
//    }
//
//    public void ModificarIdMedicamento(String id, String nuevoId) {
//        Medicamento fa = this.buscarPorCodigo(id);
//        fa.setCodigo(nuevoId);
//    }
//
//    @Override
//    public String toString() {
//        String salida = "";
//        for (Medicamento f : ListaMedicamentos) {
//            salida += f.toString() + "\n ";
//        }
//        return salida;
//
//    }
//    
//    @XmlElement(name = "medicamento")
//    private List<Medicamento> ListaMedicamentos = new ArrayList<>();
}
