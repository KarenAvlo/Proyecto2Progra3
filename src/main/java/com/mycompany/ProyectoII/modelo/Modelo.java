
package com.mycompany.ProyectoII.modelo;

import com.mycompany.ProyectoII.Farmaceuta;
import com.mycompany.ProyectoII.GestorHospital;
import com.mycompany.ProyectoII.Indicaciones;
import com.mycompany.ProyectoII.Medicamento;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Paciente;
import com.mycompany.ProyectoII.Persona;
import com.mycompany.ProyectoII.Receta;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.jfree.chart.JFreeChart;

@Data

public class Modelo {

    public Modelo() throws SQLException {
        gestor = new GestorHospital();
    }
    
    public GestorHospital getModelo() {
        return gestor;
    }
    
     //========LOGIN==========
    public Persona validarUsuario(String cedula, String clave) throws SQLException {
        return gestor.validarUsuario(cedula, clave);
    }
    //admi
     public boolean actualizarClaveAdmi(String cedula, String nuevaClave) throws SQLException {
     return gestor.actualizarClaveAdmi(cedula, nuevaClave);
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
    
       public boolean actualizarClaveMedico(String cedula, String nuevaClave) throws SQLException {
     return gestor.actualizarClave(cedula, nuevaClave);
    }
    
//========Farmaceutas==========
    public void agregarFarmaceuta(Farmaceuta farmaceuta) throws SQLException {
        gestor.agregarFarmaceuta(farmaceuta);
    }

    public void EliminarFarmaceuta(String cedula) throws SQLException {
        gestor.eliminarFarmaceuta(cedula);
    }

    public List<Farmaceuta> listarFarmaceutas() throws SQLException {
        return gestor.obtenerTodosFarmaceutas();
    }

    public Farmaceuta buscarFarmaceuta(String cedula) throws SQLException {
        return gestor.buscarFarmaceutaPorCedula(cedula);
    }

    public void eliminarFarmaceuta(String cedula) throws SQLException {
        gestor.eliminarFarmaceuta(cedula);
    }
    
    public boolean actualizarClaveFarma(String cedula, String nuevaClave) throws SQLException {
     return gestor.actualizarClaveFarma(cedula, nuevaClave);
    }
    

    //=============Pacientes==============
    public void agregarPaciente(Paciente paciente) throws SQLException {
       gestor.agregarPaciente(paciente);
    }

    public void EliminarPaciente(String cedula) throws SQLException {
        gestor.eliminarPaciente(cedula);
    }

    public List<Paciente> listarPacientes() throws SQLException {
        return gestor.obtenerTodosPacientes();
    }

    public Paciente buscarPaciente(String cedula) throws SQLException {
        return gestor.buscarPaciente(cedula);
    }

    public void eliminarPaciente(String cedula) throws SQLException {
         gestor.eliminarPaciente(cedula);
    }

    //==============Medicamentos===============
    public void agregarMedicamento(Medicamento medicamento) throws SQLException {
         gestor.agregarMedicamento(medicamento);
    }

    public void EliminarMedicamento(String codigo) throws SQLException {
        gestor.eliminarMedicamento(codigo);
    }

    public List<Medicamento> obtenerTodosMedicamentos() throws SQLException {
        return gestor.obtenerTodosMedicamentos();
    }
   
    public Medicamento buscarMedicamento(String codigo) throws SQLException {
        return gestor.buscarMedicamentoPorCodigo(codigo);
    }

    public void eliminarMedicamento(String codigo) throws SQLException {
        gestor.eliminarMedicamento(codigo);
    }

    
    public List<Medicamento> listarMedicamentos() throws SQLException {
        return gestor.obtenerTodosMedicamentos();
    }
    
    
    //===========historicoRecetas=========
    public Receta buscarReceta(String codigo) throws SQLException {
        return gestor.buscarRecetaPorCodigo(codigo);
    }
    
    public List<Receta> listarRecetas() throws SQLException {
        return gestor.obtenerTodasRecetas();
    }
    
    //=============== Recetas =============
 public void agregarReceta(Receta receta) throws SQLException {
        gestor.agregarReceta(receta);
    }

    public Receta buscarRecetaPorCodigo(String codReceta) throws SQLException {
        return gestor.buscarRecetaPorCodigo(codReceta);
    }

    public List<Receta> obtenerTodasRecetas() throws SQLException {
        return gestor.obtenerTodasRecetas();
    }

    public void actualizarReceta(Receta receta) throws SQLException {
       gestor.actualizarReceta(receta);
    }

    public void eliminarReceta(String codReceta) throws SQLException {
       gestor.eliminarReceta(codReceta);
    }
    
      
    public int cantidadRecetas()throws SQLException{
        return gestor.cantidadRecetas();
    }

    // --------------------------
    // Funcionalidades específicas de Receta
    // --------------------------
    public void modificarEstadoReceta(String codReceta, String nuevoEstado) {
        gestor.modificarEstadoReceta(codReceta, nuevoEstado);
    }

    // Agregar indicaciones a receta existente (si no se hizo con crearIndicacion de médico)
    public void agregarIndicacionesReceta(String codReceta, Indicaciones indicacion) throws SQLException {
       gestor.agregarIndicacionesReceta(codReceta, indicacion);
    }

    // Buscar recetas por paciente
    public List<Receta> buscarRecetasPorPaciente(String cedulaPaciente) throws SQLException {
      return gestor.buscarRecetasPorPaciente(cedulaPaciente);
    }

    // Buscar recetas por médico
    public List<Receta> buscarRecetasPorMedico(String cedulaMedico) throws SQLException {
        return gestor.buscarRecetasPorMedico(cedulaMedico);
    }
    
    
    public void agregarIndicacion(Receta receta, Indicaciones i) {
        gestor.agregarIndicacion(receta, i);
    }
    
    
    
    
    
    
    
    

   public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException{
       return gestor.crearGraficoPastelRecetasPorEstado(fechaInicio, fechaFin);
    }
   
   public JFreeChart crearGraficoLineaMedicamentos(LocalDate i, LocalDate f, List<String> sel, List<Receta> listRe) {        
       return gestor.crearGraficoLineaMedicamentos(i, f, sel, listRe);
   }
    
//    //============Otro===============
  
     private final GestorHospital gestor;
}

