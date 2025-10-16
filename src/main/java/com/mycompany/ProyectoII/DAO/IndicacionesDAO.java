package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Indicaciones;
import java.sql.SQLException;


//public class IndicacionesDAO implements AbstractDAO<Integer, Indicaciones>{
//
//    private static final String URL = "jdbc:mysql://localhost:3306/BDHospital";
//    private static final String USUARIO = "root";
//    private static final String CLAVE = "root";
//
//    private final ConnectionSource conexion;
//    private final Dao<Integer, Indicaciones> dao;
//
//    public IndicacionesDAO() throws SQLException {
//        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
//        this.dao = DaoManager.createDao(conexion, Indicaciones.class);
//    }
//
//    public IndicacionesDAO(ConnectionSource connectionSource) throws SQLException {
//        dao = DaoManager.createDao(connectionSource, Indicaciones.class);
//    }
//
//    public void add(Indicaciones i) throws SQLException {
//        dao.create(i);
//    }
//
//    public void update(Indicaciones i) throws SQLException {
//        dao.update(i);
//    }
//
//    public void delete(Indicaciones i) throws SQLException {
//        dao.delete(i);
//    }
//
//    public Indicaciones findById(int id) throws SQLException {
//        return dao.queryForId(id);
//    }
//
//    public Dao<Indicaciones, Integer> getDao() {
//        return dao;
//    }
//}