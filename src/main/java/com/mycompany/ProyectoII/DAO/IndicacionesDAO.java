package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Indicaciones;
import java.sql.SQLException;
import java.util.List;

public class IndicacionesDAO implements AbstractDAO<Integer, Indicaciones> {

    private static final String URL = "jdbc:mysql://localhost:3306/bdhospital";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private ConnectionSource conexion;
    private final Dao<Indicaciones, Integer> dao;

    public IndicacionesDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Indicaciones.class);

    }

    public IndicacionesDAO(ConnectionSource connectionSource) throws SQLException {
        dao = DaoManager.createDao(connectionSource, Indicaciones.class);
    }

    public void add(Indicaciones i) throws SQLException {
        dao.create(i);
    }

    public void update(Indicaciones i) throws SQLException {
        dao.update(i);
    }

    public Dao<Indicaciones, Integer> getDao() {
        return dao;
    }

    @Override
    public Indicaciones findById(Integer id) throws SQLException {
        return dao.queryForId(id);
    }

    @Override
    public void delete(Integer id) throws SQLException {
        Indicaciones i = dao.queryForId(id);
        if (i != null) {
            dao.delete(i);
        }
    }

    public void delete(Indicaciones i) throws SQLException {
        if (i != null) {
            dao.delete(i);
        }
    }

    @Override
    public List<Indicaciones> findAll() throws SQLException {
        return dao.queryForAll();
    }
    
    public void close() throws Exception {
        conexion.close();
    }
}
