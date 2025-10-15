package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Medico;
import java.sql.SQLException;
import java.util.List;

public class MedicoDAO implements AbstractDAO<String, Medico> {

    private static final String URL = "jdbc:mysql://localhost:3306/BDHospital";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Medico, String> dao;

    public MedicoDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Medico.class);
    }

    @Override
    public void add(Medico medico) throws SQLException {
        dao.create(medico);
    }

    @Override
    public Medico findById(String cedula) throws SQLException {
        return dao.queryForId(cedula);
    }

    @Override
    public List<Medico> findAll() throws SQLException {
        return dao.queryForAll();
    }

    @Override
    public void update(Medico medico) throws SQLException {
        dao.update(medico);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        dao.deleteById(cedula);
    }

    public void close() throws Exception {
        conexion.close();
    }
}