package com.mycompany.ProyectoII.modelo;

import com.mycompany.ProyectoII.Farmaceuta;
import com.mycompany.ProyectoII.GestorHospital;
import com.mycompany.ProyectoII.Hospital;
import com.mycompany.ProyectoII.Medicamento;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Paciente;
import com.mycompany.ProyectoII.Persona;
import com.mycompany.ProyectoII.Receta;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
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

public class Modelo {

    public Modelo() throws SQLException {
        gestor = new GestorHospital();
    }
    
    public GestorHospital getModelo() {
        return gestor;
    }
    
    public Persona validarUsuario(String cedula, String clave) throws SQLException {
        return gestor.validarUsuario(cedula, clave);
    }

    //========MEDICOS==========
    public void agregarMedico(Medico m) throws SQLException {
        gestor.agregarMedico(m);
    }

    public void eliminarMedico(String cedula) throws SQLException {
        gestor.eliminarMedico(cedula);
    }
    public void actualizarMedico(Medico medico) throws SQLException {
        gestor.actualizarMedico(medico);
    }

    public Medico buscarMedico(String cedula) throws SQLException {
        return gestor.buscarMedico(cedula);
    }

    public List<Medico> obtenerTodosMedicos() throws SQLException {
        return gestor.obtenerTodosMedicos();
    }

//    //----Farmaceutas---
//    public boolean agregarFarmaceuta(String cedula, String nombre) {
//        return gestor.getGestorFarmaceutas().InclusionFarmaceuta(cedula, nombre);
//    }
//
//    public boolean EliminarFarmaceuta(String cedula) {
//        return gestor.getGestorFarmaceutas().BorrarFarmaceuta(cedula);
//    }
//
//    public List<Farmaceuta> listarFarmaceutas() {
//        return gestor.getGestorFarmaceutas().getListaFarmaceutas();
//    }
//
//    public Farmaceuta buscarFarmaceuta(String cedula) {
//        return gestor.getGestorFarmaceutas().buscarPorCedula(cedula);
//    }
//
//    public boolean eliminarFarmaceuta(String cedula) {
//        return gestor.getGestorFarmaceutas().BorrarFarmaceuta(cedula);
//    }
//
//    //=============Pacientes==============
//    public boolean agregarPaciente(String cedula, String nombre, String fecha, String telefono) {
//        return gestor.getGestorP().InclusionPaciente(cedula, nombre, fecha, telefono);
//    }
//
//    public boolean EliminarPaciente(String cedula) {
//        return gestor.getGestorP().BorrarPaciente(cedula);
//    }
//
//    public List<Paciente> listarPacientes() {
//        return gestor.getGestorP().getListaPacientes();
//    }
//
//    public Paciente buscarPaciente(String cedula) {
//        return gestor.getGestorP().buscarPorCedula(cedula);
//    }
//
//    public boolean eliminarPaciente(String cedula) {
//        return gestor.getGestorP().BorrarPaciente(cedula);
//    }
//
//    //==============Medicamentos===============
//    public boolean agregarMedicamento(String codigo, String nombre, String presentacion) {
//        return gestor.getFarma().getGestorMedicamentos().InclusionMedicamento(codigo, nombre, presentacion);
//    }
//
//    public boolean EliminarMedicamento(String codigo) {
//        return gestor.getFarma().getGestorMedicamentos().BorrarMedicamento(codigo);
//    }
//
//    public List<Medicamento> listarMedicamentos() {
//        return gestor.getFarma().getGestorMedicamentos().getListaMedicamentos();
//    }
//
//    public Medicamento buscarMedicamento(String codigo) {
//        return gestor.getFarma().getGestorMedicamentos().buscarPorCodigo(codigo);
//    }
//
//    public boolean eliminarMedicamento(String codigo) {
//        return gestor.getFarma().getGestorMedicamentos().BorrarMedicamento(codigo);
//    }
//
//    //===========historicoRecetas=========
//    public Receta buscarReceta(String codigo) {
//        return gestor.getFarma().getGestorRecetas().buscarPorCodigo(codigo);
//    }
//    
//    public List<Receta> listarRecetas() {
//        return gestor.getFarma().getGestorRecetas().getListaRecetas();
//    }
//    
//    //=============== Recetas =============
//    public boolean agregarReceta(Receta receta){
//        return gestor.getFarma().getGestorRecetas().agregarReceta(receta);
//        
//    }
//    
//    public int cantidadRecetas(){
//        return gestor.getFarma().getGestorRecetas().cantidadRecetas();
//    }
//    
//    public void guardarRecetas() throws Exception{
//        gestor.getFarma().getGestorRecetas().guardar();
//    }
//    
//    public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin){
//        return gestor.getFarma().getGestorRecetas().crearGraficoPastelRecetasPorEstado(fechaInicio, fechaFin);
//    }
//    
//    public JFreeChart crearGraficoLineaMedicamentos(LocalDate i, LocalDate f, List<String> sel, List<Receta> listRe) {
//        return gestor.getFarma().getGestorRecetas().crearGraficoLineaMedicamentos(i, f, sel, listRe);
//    }
//    
//    //============Otro===============
//    public boolean cargarDatos() {
//        try {
//            gestor.cargarDatos();
//            return true;
//        } catch (Exception e) {
//            System.err.println("Error al cargar datos iniciales: " + e.getMessage());
//            return false;
//        }
//    }
//
//    public boolean guardarDatos() {
//        return gestor.guardarDatos();
//    }
//    
     private final GestorHospital gestor;
}
