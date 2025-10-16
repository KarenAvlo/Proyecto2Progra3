package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Farmaceuta;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;

@Getter

public class FarmaceutaDAO implements AbstractDAO<String, Farmaceuta> {

    private static final String URL = "jdbc:mysql://localhost:3306/BDHospital";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Farmaceuta, String> dao;

    public FarmaceutaDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Farmaceuta.class);
    }

    @Override
    public void add(Farmaceuta farmaceuta) throws SQLException {
        dao.create(farmaceuta);
    }

    @Override
    public Farmaceuta findById(String cedula) throws SQLException {
        return dao.queryForId(cedula);
    }

    @Override
    public List<Farmaceuta> findAll() throws SQLException {
        return dao.queryForAll();
    }

    @Override
    public void update(Farmaceuta farmaceuta) throws SQLException {
        dao.update(farmaceuta);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        dao.deleteById(cedula);
    }

    public void close() throws Exception {
        conexion.close();
    }
}



