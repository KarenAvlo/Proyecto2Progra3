package com.mycompany.ProyectoII.control;

import com.mycompany.ProyectoII.Administrativo;
import com.mycompany.ProyectoII.Farmaceuta;
import com.mycompany.ProyectoII.GestorHospital;
import com.mycompany.ProyectoII.Hospital;
import com.mycompany.ProyectoII.Medicamento;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Paciente;
import com.mycompany.ProyectoII.Persona;
import com.mycompany.ProyectoII.Receta;
import static com.mycompany.ProyectoII.control.TipoUsuario.ADMINISTRATIVO;
import static com.mycompany.ProyectoII.control.TipoUsuario.MEDICO;
import com.mycompany.ProyectoII.modelo.modelo;
import com.mycompany.ProyectoII.vista.VentanaPrincipal;
import com.mycompany.ProyectoII.vista.VentanaAdministrador;
import com.mycompany.ProyectoII.vista.VentanaFarmaceuta;
import com.mycompany.ProyectoII.vista.VentanaMedico;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.JFreeChart;
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

   

@Setter
@Getter

public class Control {
private modelo model;

    public Control(modelo modelo) {
        this.model = modelo;
    }

    public Control() throws SQLException {
        this(new modelo());
    }

    public GestorHospital getHospital() {
        
return model.getModelo();
    }

    public void prueba1() {
        JOptionPane.showMessageDialog(null, "hola!");
    }

    public void cerrarAplicacion() {
        System.out.println("Aplicacion finalizada");

    }

//    public void GuardarCambioContraseña() {
//        modelo.guardarDatos();
//    }
//
//    public void abrirVentanaSegunUsuario(TipoUsuario tipo) {
//        System.out.println("abrirVentanaSegunUsuario llamado con tipo: " + tipo);
//        switch (tipo) {
//            case FARMACEUTA -> {
//                VentanaFarmaceuta ventanaFarmaceuta = new VentanaFarmaceuta(this);
//                ventanaFarmaceuta.setVisible(true);
//            }
//            case MEDICO -> {
//            }
//            case ADMINISTRATIVO -> {
//                VentanaAdministrador ventanaAdmin = new VentanaAdministrador(this);
//                ventanaAdmin.setVisible(true);
//            }
//            default -> JOptionPane.showMessageDialog(null, "Usuario no reconocido");
//        }
//    }
//
//    public void abrirVentanaMedico(Medico med) {
//        VentanaMedico ventanaMedico = new VentanaMedico(this, med);
//        ventanaMedico.setVisible(true);
//    }
//    
//    public void volverVentanaPrincipal(){
//        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(this);
//        ventanaPrincipal.setVisible(true);
//        
//    }
//
//    public Persona validarUsuario(String cedula, String clave) {
//        Persona p = modelo.getModelo().getGestorPersonas().login(cedula, clave); // usa tu login centralizado
//        return p;
//    }
//
//    public TipoUsuario tipoDeUsuario(Persona p) {
//        if (p instanceof Medico) {
//            return TipoUsuario.MEDICO;
//        }
//        if (p instanceof Farmaceuta) {
//            return TipoUsuario.FARMACEUTA;
//        }
//        if (p instanceof Administrativo) {
//            return TipoUsuario.ADMINISTRATIVO;
//        }
//        return null;
//    }
//
//    //===========Medicos==============
//    private MedicoDAO getGestorMedicos() {
//        return modelo.getModelo().getGestorMedicos();
//    }
//
//    public boolean agregarMedico(String cedula, String nombre, String especialidad) {
//        boolean exito = modelo.agregarMedico(cedula, nombre, especialidad);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public boolean eliminarMedico(String cedula) {
//        boolean exito = modelo.eliminarMedico(cedula);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public Medico buscarMedico(String cedula) {
//        return modelo.buscarMedico(cedula);
//    }
//
//    public List<Medico> listarMedicos() {
//        return modelo.listarMedicos();
//    }
//
//    //=================Farmaceutas=============
//    public boolean agregarFarmaceuta(String cedula, String nombre) {
//        boolean exito = modelo.agregarFarmaceuta(cedula, nombre);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public boolean EliminarFarmaceuta(String cedula) {
//        boolean exito = modelo.EliminarFarmaceuta(cedula);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public List<Farmaceuta> ListarFarmaceutas() {
//        return modelo.listarFarmaceutas();
//    }
//
//    public Farmaceuta buscarFarmaceuta(String cedula) {
//        return modelo.buscarFarmaceuta(cedula);
//    }
//
//    public boolean eliminarFarmaceuta(String cedula) {
//        boolean exito = modelo.eliminarFarmaceuta(cedula);
//        if (exito) {
//            // Guardar los cambios después de eliminar
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    //=============Pacientes=============
//    public boolean agregarPaciente(String cedula, String nombre, String fecha, String tel) {
//        boolean exito = modelo.agregarPaciente(cedula, nombre, fecha, tel);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public boolean EliminarPaciente(String cedula) {
//        boolean exito = modelo.EliminarPaciente(cedula);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public List<Paciente> ListarPacientes() {
//        return modelo.listarPacientes();
//    }
//
//    public Paciente buscarPaciente(String cedula) {
//        return modelo.buscarPaciente(cedula);
//    }
//
//    public boolean eliminarPaciente(String cedula) {
//        boolean exito = modelo.EliminarPaciente(cedula);
//        if (exito) {
//            // Guardar los cambios después de eliminar
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    //========medicamentos========
//    public boolean agregarMedicamento(String codigo, String nombre, String presentacion) {
//        boolean exito = modelo.agregarMedicamento(codigo, nombre, presentacion);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public boolean EliminarMedicamento(String codigo) {
//        boolean exito = modelo.EliminarMedicamento(codigo);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public List<Medicamento> ListarMedicamentos() {
//        return modelo.listarMedicamentos();
//    }
//
//    public Medicamento buscarMedicamento(String cod) {
//        return modelo.buscarMedicamento(cod);
//    }
//
//    public boolean eliminarMedicamento(String cod) {
//        boolean exito = modelo.EliminarMedicamento(cod);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    //================= Recetas =============
//    public boolean agregarReceta(Receta receta) {
//         boolean exito = modelo.agregarReceta(receta);
//        if (exito) {
//            return modelo.guardarDatos();
//        }
//        return false;
//    }
//
//    public List<Receta> listarRecetas() {
//        return modelo.listarRecetas();
//    }
//
//    public int cantidadRecetas() {
//        return modelo.cantidadRecetas();
//    }
//    
//    public void guardarRecetas() throws Exception{
//        modelo.guardarRecetas();
//    }
//
//    public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) {
//        return modelo.crearGraficoPastelRecetasPorEstado(fechaInicio, fechaFin);
//    }
//
//    public JFreeChart crearGraficoLineaMedicamentos(LocalDate i, LocalDate f, List<String> sel, List<Receta> listRe) {
//        return modelo.crearGraficoLineaMedicamentos(i, f, sel, listRe);
//    }
//
//    //============historico===========
//    public Receta buscarReceta(String cod) {
//        return modelo.buscarReceta(cod);
//    }
//
//    public List<Receta> ListarRecetas() {
//        return modelo.listarRecetas();
//    }

//    private final modelo modelo;


}
