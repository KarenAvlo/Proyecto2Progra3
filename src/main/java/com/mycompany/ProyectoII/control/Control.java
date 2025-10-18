package com.mycompany.ProyectoII.control;

//package com.mycompany.ProyectoII.control;
import com.mycompany.ProyectoII.Administrativo;
import com.mycompany.ProyectoII.Farmaceuta;
import com.mycompany.ProyectoII.Indicaciones;
import com.mycompany.ProyectoII.Medicamento;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Paciente;
import com.mycompany.ProyectoII.Persona;
import com.mycompany.ProyectoII.Receta;
import com.mycompany.ProyectoII.control.TipoUsuario;
import static com.mycompany.ProyectoII.control.TipoUsuario.ADMINISTRATIVO;
import static com.mycompany.ProyectoII.control.TipoUsuario.MEDICO;
import com.mycompany.ProyectoII.modelo.Modelo;
import com.mycompany.ProyectoII.vista.VentanaAdministrador;
import com.mycompany.ProyectoII.vista.VentanaFarmaceuta;
import com.mycompany.ProyectoII.vista.VentanaMedico;
import com.mycompany.ProyectoII.vista.VentanaPrincipal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import javax.swing.JOptionPane;
import lombok.Getter;
import lombok.Setter;
import org.jfree.chart.JFreeChart;

@Setter
@Getter

public class Control {

    public Control(Modelo m) {
        this.modelo = m;
    }

    public Control() throws SQLException {
        modelo = new Modelo();
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void prueba1() {
        JOptionPane.showMessageDialog(null, "hola!");
    }

    public void cerrarAplicacion() {
        System.out.println("Aplicacion finalizada");

    }

    /*
    public void GuardarCambioContraseña() {
        modelo.guardarDatos();
    }
     */
    public void abrirVentanaSegunUsuario(TipoUsuario tipo) throws SQLException {
        System.out.println("abrirVentanaSegunUsuario llamado con tipo: " + tipo);
        switch (tipo) {
            case FARMACEUTA -> {
                VentanaFarmaceuta ventanaFarmaceuta = new VentanaFarmaceuta(this);
                ventanaFarmaceuta.setVisible(true);
            }
            case MEDICO -> {
            }
            case ADMINISTRATIVO -> {
                VentanaAdministrador ventanaAdmin = new VentanaAdministrador(this);
                ventanaAdmin.setVisible(true);
            }
            default ->
                JOptionPane.showMessageDialog(null, "Usuario no reconocido");
        }
    }

    public void abrirVentanaMedico(Medico med) throws SQLException {
        VentanaMedico ventanaMedico = new VentanaMedico(this, med);
        ventanaMedico.setVisible(true);
    }

    public void volverVentanaPrincipal() {
        VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(this);
        ventanaPrincipal.setVisible(true);

    }

    public Persona validarUsuario(String cedula, String clave) throws SQLException {
        return modelo.validarUsuario(cedula, clave);
    }

    public TipoUsuario tipoDeUsuario(Persona p) {
        if (p instanceof Medico) {
            return TipoUsuario.MEDICO;
        }
        if (p instanceof Farmaceuta) {
            return TipoUsuario.FARMACEUTA;
        }
        if (p instanceof Administrativo) {
            return TipoUsuario.ADMINISTRATIVO;
        }
        return null;
    }

    //========MEDICOS==========
    public void agregarMedico(Medico m) throws SQLException {
        modelo.agregarMedico(m);
    }

    public void eliminarMedico(String cedula) throws SQLException {
        modelo.eliminarMedico(cedula);
    }

    public void actualizarMedico(Medico medico) throws SQLException {
        modelo.actualizarMedico(medico);
    }

    public Medico buscarMedico(String cedula) {
        Medico med = null;
        try {
            med = modelo.buscarMedico(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return med;
    }

    public List<Medico> obtenerTodosMedicos() throws SQLException {
        return modelo.obtenerTodosMedicos();
    }

    //=================Farmaceutas=============
    public void agregarFarmaceuta(Farmaceuta farmaceuta) {
        try {
            modelo.agregarFarmaceuta(farmaceuta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public void EliminarFarmaceuta(String cedula) {
        try {
            modelo.EliminarFarmaceuta(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public List<Farmaceuta> ListarFarmaceutas() {
        try {
            return modelo.listarFarmaceutas();
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return null;
    }

    public Farmaceuta buscarFarmaceuta(String cedula) {
        try {
            return modelo.buscarFarmaceuta(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return null;
    }

    public void eliminarFarmaceuta(String cedula) {
        try {
            modelo.EliminarFarmaceuta(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    //=============Pacientes=============
    public void agregarPaciente(Paciente paciente) {
        try {
            modelo.agregarPaciente(paciente);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public void EliminarPaciente(String cedula) {
        try {
            modelo.EliminarPaciente(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public List<Paciente> ListarPacientes() {
        List<Paciente> pac = null;
        try {
            pac = modelo.listarPacientes();
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return pac;
    }

    public Paciente buscarPaciente(String cedula) {
        Paciente pa = null;
        try {
            pa = modelo.buscarPaciente(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return pa;
    }

    public void eliminarPaciente(String cedula) {
        try {
            modelo.eliminarPaciente(cedula);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    //========medicamentos========
    public void agregarMedicamento(Medicamento med) {
        try {
            modelo.agregarMedicamento(med);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public void EliminarMedicamento(String codigo) {
        try {
            modelo.eliminarMedicamento(codigo);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public List<Medicamento> obtenerTodosMedicamentos() throws SQLException {
        return modelo.obtenerTodosMedicamentos();
    }
   

    public Medicamento buscarMedicamento(String cod) {
        Medicamento med = null;
        try {
            med = modelo.buscarMedicamento(cod);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return med;
    }

    public void eliminarMedicamento(String cod) {
        try {
            modelo.EliminarMedicamento(cod);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    
    
    
    
    
    //================= Recetas =============
    public void agregarReceta(Receta receta) {
        try {
            modelo.agregarReceta(receta);

        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public List<Receta> obtenerTodasRecetas() throws SQLException {
        return modelo.obtenerTodasRecetas();

    }

    public Receta buscarRecetaPorCodigo(String codReceta) {
        try {
            return modelo.buscarRecetaPorCodigo(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return null;
    }

    public void actualizarReceta(Receta receta) {
        try {
            modelo.actualizarReceta(receta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    public void eliminarReceta(String codReceta) {
        try {
            modelo.eliminarReceta(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
    }

    // --------------------------
    // Funcionalidades específicas de Receta
    // --------------------------
    public void modificarEstadoReceta(String codReceta, String nuevoEstado) {
        modelo.modificarEstadoReceta(codReceta, nuevoEstado);
    }

    // Agregar indicaciones a receta existente (si no se hizo con crearIndicacion de médico)
    public void agregarIndicacionesReceta(String codReceta, Indicaciones indicacion) throws SQLException {
        modelo.agregarIndicacionesReceta(codReceta, indicacion);
    }

    // Buscar recetas por paciente
    public List<Receta> buscarRecetasPorPaciente(String cedulaPaciente) throws SQLException {
        return modelo. buscarRecetasPorPaciente(cedulaPaciente);
    }

    // Buscar recetas por médico
    public List<Receta> buscarRecetasPorMedico(String cedulaMedico) throws SQLException {
        return modelo.buscarRecetasPorMedico(cedulaMedico);
    }


    public JFreeChart crearGraficoPastelRecetasPorEstado(LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        return modelo.crearGraficoPastelRecetasPorEstado(fechaInicio, fechaFin);
    }

    public JFreeChart crearGraficoLineaMedicamentos(LocalDate i, LocalDate f, List<String> sel, List<Receta> listRe) {
        return modelo.crearGraficoLineaMedicamentos(i, f, sel, listRe);
    }
    //============historico===========
    public Receta buscarReceta(String cod) {
        Receta re = null;
        try {
            re = modelo.buscarReceta(cod);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        return re;

    }

    private Modelo modelo;

}
