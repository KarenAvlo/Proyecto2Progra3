package com.mycompany.ProyectoII;

import com.mycompany.ProyectoII.DAO.AdministrativoDAO;
import com.mycompany.ProyectoII.DAO.FarmaceutaDAO;
import com.mycompany.ProyectoII.DAO.MedicamentoDAO;
import com.mycompany.ProyectoII.DAO.MedicoDAO;
import com.mycompany.ProyectoII.DAO.PacienteDAO;
import com.mycompany.ProyectoII.DAO.RecetaDAO;
import com.mycompany.ProyectoII.DAO.IndicacionesDAO;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GestorHospital {

    // DAOs
    private final PacienteDAO pacienteDAO;
    private final MedicoDAO medicoDAO;
    private final FarmaceutaDAO farmaceutaDAO;
    private final AdministrativoDAO administrativoDAO;
    private final RecetaDAO recetaDAO;
    private final MedicamentoDAO medicamentoDAO;
    private final IndicacionesDAO indicacionesDAO;

    // Constructor
    public GestorHospital() throws SQLException {
        this.pacienteDAO = new PacienteDAO();
        this.medicoDAO = new MedicoDAO();
        this.farmaceutaDAO = new FarmaceutaDAO();
        this.administrativoDAO = new AdministrativoDAO();
        this.recetaDAO = new RecetaDAO();
        this.medicamentoDAO = new MedicamentoDAO();
        this.indicacionesDAO = new IndicacionesDAO();
    }

    // --------------------------
    // CRUD Paciente
    // --------------------------
    public void agregarPaciente(Paciente paciente) throws SQLException {
        pacienteDAO.add(paciente);
    }

    public Paciente buscarPaciente(String cedula) throws SQLException {
        return pacienteDAO.findById(cedula);
    }

    public List<Paciente> obtenerTodosPacientes() throws SQLException {
        return pacienteDAO.findAll();
    }

    public void actualizarPaciente(Paciente paciente) throws SQLException {
        pacienteDAO.update(paciente);
    }

    public void eliminarPaciente(String cedula) throws SQLException {
        pacienteDAO.delete(cedula);
    }

    public Paciente buscarPorCedula(String cedula) throws SQLException {
        return pacienteDAO.findById(cedula);
    }

    // --------------------------
    // Funcionalidades específicas de Medico
    // --------------------------
    public boolean inclusionPaciente(String id, String nombre, String nacimiento, String numero) throws SQLException {
        if (buscarPorCedula(id) == null) {
            Paciente p = new Paciente(id, nombre, numero, Date.valueOf(nacimiento));
            pacienteDAO.add(p);
            return true;
        }
        return false;
    }

    public boolean existePaciente(String ced) throws SQLException {
        return buscarPorCedula(ced) != null;
    }

    // Buscar por nombre (pueden haber varios)
    public List<Paciente> buscarPorNombre(String nombre) throws SQLException {
        return pacienteDAO.getDao().queryForEq("nombre", nombre);
    }

    // Consultar paciente (solo imprime información)
    public void consultaPaciente(String cedula) throws SQLException {
        Paciente p = buscarPorCedula(cedula);
        if (p != null) {
            System.out.println(p.toString());
        }
    }

    // Modificar cédula de paciente (si realmente querés cambiar la PK)
    public void modificarIdPaciente(String id, String nuevoId) throws SQLException {
        Paciente p = buscarPorCedula(id);
        if (p != null) {
            p.setCedula(nuevoId);
            pacienteDAO.update(p);
        }
    }

    // Obtener todos los pacientes como String
    @Override
    public String toString() {
        try {
            StringBuilder salida = new StringBuilder();
            for (Paciente p : pacienteDAO.findAll()) {
                salida.append(p.toString()).append("\n");
            }
            return salida.toString();
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }

    // --------------------------
    // CRUD Medico
    // --------------------------
    public void agregarMedico(Medico medico) throws SQLException {
        medicoDAO.add(medico);
    }

    public Medico buscarMedico(String cedula) throws SQLException {
        return medicoDAO.findById(cedula);
    }

    public List<Medico> obtenerTodosMedicos() throws SQLException {
        return medicoDAO.findAll();
    }

    public void actualizarMedico(Medico medico) throws SQLException {
        medicoDAO.update(medico);
    }

    public void eliminarMedico(String cedula) throws SQLException {
        medicoDAO.delete(cedula);
    }

    // --------------------------
    // Funcionalidades específicas de Medico
    // --------------------------
    public Receta prescribirReceta(String codReceta, String cedulaPaciente, String cedulaMedico) throws SQLException {
        // Buscar paciente y medico en la base de datos
        Paciente paciente = pacienteDAO.findById(cedulaPaciente);
        Medico medico = medicoDAO.findById(cedulaMedico);

        if (paciente == null || medico == null) {
            return null; // no se puede crear receta si no existen paciente o médico
        }

        Receta receta = new Receta(codReceta, paciente, medico, null, null, "Inprocess");
        receta.finalizarReceta(); // setea fechaEmision, fechaRetiro y estado

        recetaDAO.add(receta); // guardar receta en la base de datos
        return receta;
    }

    public void crearIndicacion(String codReceta, String codMedicamento, int cantidad,
            String indicaciones, int duracion) throws SQLException {
        Receta receta = recetaDAO.findByCodigo(codReceta);
        if (receta == null) {
            return;
        }

        Medicamento medicamento = medicamentoDAO.findById(codMedicamento);
        if (medicamento == null) {
            return;
        }

        Indicaciones i = new Indicaciones(medicamento, cantidad, indicaciones,
                duracion, receta);
        receta.getIndicaciones().add(i);

        recetaDAO.update(receta); // actualizar la receta con la nueva indicación
    }

    public void modificarReceta(String codReceta, String codigoMedicamento, String nuevoMedicamento,
            int cantidad, String indicaciones, int duracion) throws SQLException {
        Receta receta = null;
        try {
            receta = recetaDAO.findByCodigo(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }

        if (receta == null) {
            return;
        }

        receta.ModificarIndicaciones(codigoMedicamento, nuevoMedicamento, cantidad, indicaciones, duracion,
                medicamentoDAO.findAll()); // pasamos todos los medicamentos disponibles
        recetaDAO.update(receta); // actualizar en base de datos
    }

    // --------------------------
    // CRUD Farmaceuta
    // --------------------------
    public void agregarFarmaceuta(Farmaceuta farmaceuta) throws SQLException {
        farmaceutaDAO.add(farmaceuta);
    }

    public Farmaceuta buscarFarmaceutaPorCedula(String cedula) throws SQLException {
        return farmaceutaDAO.findById(cedula);
    }

    public List<Farmaceuta> obtenerTodosFarmaceutas() throws SQLException {
        return farmaceutaDAO.findAll();
    }

    public void actualizarFarmaceuta(Farmaceuta farmaceuta) throws SQLException {
        farmaceutaDAO.update(farmaceuta);
    }

    public void eliminarFarmaceuta(String cedula) throws SQLException {
        farmaceutaDAO.delete(cedula);
    }

    // --------------------------
    // Funcionalidades específicas de Farmaceuta
    // --------------------------
    // Procesar receta (pone en "En Proceso" si cumple condiciones)
    public boolean ProcesarReceta(String idPaciente, String codRec) {
        Receta receta = null;
        try {
            receta = recetaDAO.findByCodigo(codRec);
        } catch (SQLException ex) {
            ex.getMessage();
        }

        if (receta != null && receta.getEstado().equals("CONFECCIONADA")
                && (receta.getFechaRetiro().equals(LocalDate.now())
                || receta.getFechaRetiro().equals(LocalDate.now().plusDays(1))
                || receta.getFechaRetiro().equals(LocalDate.now().plusDays(2))
                || receta.getFechaRetiro().equals(LocalDate.now().plusDays(3)))) {
            receta.setEstado("En Proceso");
            return true;
        }
        return false;
    }

// Enlistar receta (pone en "Lista" si ya está en "En Proceso")
    public void enlistarReceta(String idPaciente, String codRec) {
        if (ProcesarReceta(idPaciente, codRec)) {
            Receta receta = null;
            try {
                receta = recetaDAO.findByCodigo(codRec);
            } catch (SQLException ex) {
                ex.getMessage();
            }
            if (receta != null) {
                receta.setEstado("Lista");
            }
        }
    }

// Despachar receta (pone en "Entregada" si está en "Lista")
    public void despacharReceta(String idPaciente, String codRec) {

        Receta receta = null;
        try {
            receta = recetaDAO.findByCodigo(codRec);
        } catch (SQLException ex) {
            ex.getMessage();
        }

        if (receta != null && "Lista".equals(receta.getEstado())) {
            receta.setEstado("Entregada");
            try {
                recetaDAO.update(receta);
            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
    }

// Buscar por nombre (opcional, devuelve lista)
    public List<Farmaceuta> buscarFarmaceutaPorNombre(String nombre) throws SQLException {
        return farmaceutaDAO.getDao().queryForEq("nombre", nombre);
    }

// Modificar clave de farmaceuta
    public void modificarClaveFarmaceuta(String cedula, String nuevaClave) throws SQLException {
        Farmaceuta f = farmaceutaDAO.findById(cedula);
        if (f != null) {
            f.setClave(nuevaClave);
            farmaceutaDAO.update(f);
        }
    }

    // --------------------------
    // CRUD Medicamento
    // --------------------------
    public void agregarMedicamento(Medicamento medicamento) throws SQLException {
        medicamentoDAO.add(medicamento);
    }

    public Medicamento buscarMedicamentoPorCodigo(String codigo) throws SQLException {
        return medicamentoDAO.findById(codigo);
    }

    public List<Medicamento> obtenerTodosMedicamentos() throws SQLException {
        return medicamentoDAO.findAll();
    }

    public void actualizarMedicamento(Medicamento medicamento) throws SQLException {
        medicamentoDAO.update(medicamento);
    }

    public void eliminarMedicamento(String codigo) throws SQLException {
        medicamentoDAO.delete(codigo);
    }

    // --------------------------
    // Funcionalidades específicas de Medicamento
    // --------------------------
    // Por ahora no hay funcionalidades especiales, pero aquí se podrían agregar métodos como:
    // buscar por nombre, filtrar por presentación, etc.
    // --------------------------
    // CRUD Indicaciones
    // --------------------------
    public void agregarIndicacion(Indicaciones i) throws SQLException {
        indicacionesDAO.add(i);
    }

    public void actualizarIndicacion(Indicaciones i) throws SQLException {
        indicacionesDAO.update(i);
    }

    public void eliminarIndicacion(Indicaciones i) throws SQLException {
        indicacionesDAO.delete(i);
    }

    // Agregar indicación a una receta
    public void agregarIndicacionReceta(String codReceta, Indicaciones i) {

        Receta r = null;
        try {
            recetaDAO.findByCodigo(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        if (r != null) {
            i.setReceta(r);
            r.getIndicaciones().add(i);
            try {
                agregarIndicacion(i);
            } catch (SQLException ex) {
                ex.getMessage();
            }

            try {
                recetaDAO.update(r);
            } catch (SQLException ex) {
                ex.getMessage();
            }
            // opcional, para reflejar la lista de indicaciones
        }
    }

    // --------------------------
    // CRUD Receta
    // --------------------------
    public void agregarReceta(Receta receta) throws SQLException {
        recetaDAO.add(receta);
    }

    public Receta buscarRecetaPorCodigo(String codReceta) throws SQLException {
        return recetaDAO.findByCodigo(codReceta);
    }

    public List<Receta> obtenerTodasRecetas() throws SQLException {
        return recetaDAO.findAll();
    }

    public void actualizarReceta(Receta receta) throws SQLException {
        recetaDAO.update(receta);
    }

    public void eliminarReceta(String codReceta) throws SQLException {
        recetaDAO.deleteByCodigo(codReceta);
    }

    // --------------------------
    // Funcionalidades específicas de Receta
    // --------------------------
    public void modificarEstadoReceta(String codReceta, String nuevoEstado) {
        Receta receta = null;
        try {
            recetaDAO.findByCodigo(codReceta);
        } catch (SQLException ex) {
            ex.getMessage();
        }
        if (receta != null) {
            receta.setEstado(nuevoEstado);

            try {
                recetaDAO.update(receta);
            } catch (SQLException ex) {
                ex.getMessage();
            }
        }
    }

    // Agregar indicaciones a receta existente (si no se hizo con crearIndicacion de médico)
    public void agregarIndicacionesReceta(String codReceta, Indicaciones indicacion) throws SQLException {
        Receta receta = recetaDAO.findByCodigo(codReceta);
        if (receta != null) {
            receta.agregarIndicaciones(indicacion);
            recetaDAO.update(receta);
        }
    }

    // Buscar recetas por paciente
    public List<Receta> buscarRecetasPorPaciente(String cedulaPaciente) throws SQLException {
        Paciente paciente = pacienteDAO.findById(cedulaPaciente);
        if (paciente == null) {
            return new ArrayList<>();
        }

        return recetaDAO.getDao().queryForEq("Paciente_Cedula", cedulaPaciente);

    }

    // Buscar recetas por médico
    public List<Receta> buscarRecetasPorMedico(String cedulaMedico) throws SQLException {
        return recetaDAO.getDao().queryForEq("medico_cedula", cedulaMedico);
    }

    // --------------------------
    // Cierre de conexión
    // --------------------------
    public void close() throws Exception {
        pacienteDAO.close();
        medicoDAO.close();
        farmaceutaDAO.close();
        administrativoDAO.close();
        recetaDAO.close();
        medicamentoDAO.close();
        indicacionesDAO.close();
    }

}
