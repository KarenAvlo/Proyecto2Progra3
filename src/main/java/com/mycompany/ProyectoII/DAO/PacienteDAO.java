package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Paciente;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;

@Getter
public class PacienteDAO implements AbstractDAO<String, Paciente> {

    private static final String URL = "jdbc:mysql://localhost:3306/mybd";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Paciente, String> dao;

    public PacienteDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Paciente.class);
    }

    @Override
    public void add(Paciente paciente) throws SQLException {
        dao.create(paciente);
    }

    @Override
    public Paciente findById(String cedula) throws SQLException {
        return dao.queryForId(cedula);
    }

    @Override
    public List<Paciente> findAll() throws SQLException {
        return dao.queryForAll();
    }

    @Override
    public void update(Paciente paciente) throws SQLException {
        dao.update(paciente);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        dao.deleteById(cedula);
    }

    public void close() throws Exception {
        conexion.close();
    }
}

