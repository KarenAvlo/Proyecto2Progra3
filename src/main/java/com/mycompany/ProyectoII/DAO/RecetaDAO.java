package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Receta;
import com.mycompany.ProyectoII.Receta;
import java.sql.SQLException;
import java.util.List;
import lombok.Getter;
        
@Getter
public class RecetaDAO implements AbstractDAO<String, Receta> {

    private static final String URL = "jdbc:mysql://localhost:3306/BDHospital";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Receta, String> dao;

    public RecetaDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Receta.class);
    }

    @Override
    public void add(Receta receta) throws SQLException {
        dao.create(receta);
    }

    @Override
    public Receta findById(String codReceta) throws SQLException {
        return dao.queryForId(codReceta);
    }

    @Override
    public List<Receta> findAll() throws SQLException {
        return dao.queryForAll();
    }

    @Override
    public void update(Receta receta) throws SQLException {
        dao.update(receta);
    }

    @Override
    public void delete(String codReceta) throws SQLException {
        dao.deleteById(codReceta);
    }

    public void close() throws Exception {
        conexion.close();
    }
}
