package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Administrativo;
import com.mycompany.ProyectoII.Administrativo;
import java.sql.SQLException;
import java.util.List;

public class AdministrativoDAO implements AbstractDAO<String, Administrativo> {

    private static final String URL = "jdbc:mysql://localhost:3306/mybd";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Administrativo, String> dao;

  public AdministrativoDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Administrativo.class);
    }

    @Override
    public void add(Administrativo administrativo) throws SQLException {
        dao.create(administrativo);
    }

    @Override
    public Administrativo findById(String cedula) throws SQLException {
        return dao.queryForId(cedula);
    }

    @Override
    public List<Administrativo> findAll() throws SQLException {
        return dao.queryForAll();
    }

    @Override
    public void update(Administrativo administrativo) throws SQLException {
        dao.update(administrativo);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        dao.deleteById(cedula);
    }

    public void close() throws Exception {
        conexion.close();
    }

}
