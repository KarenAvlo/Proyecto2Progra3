package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Receta;
import com.mycompany.ProyectoII.Receta;
import java.sql.SQLException;
import java.util.List;
import lombok.Data;
import lombok.Getter;
        
@Getter
@Data

public class RecetaDAO implements AbstractDAO<Integer, Receta> {

    private static final String URL = "jdbc:mysql://localhost:3306/mybd";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Receta, Integer> dao;

    // Constructor con conexión automática
    public RecetaDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Receta.class);
    }

    // Constructor con conexión externa
    public RecetaDAO(ConnectionSource connectionSource) throws SQLException {
        this.conexion = connectionSource;
        this.dao = DaoManager.createDao(connectionSource, Receta.class);
    }

    @Override
    public void add(Receta receta) throws SQLException {
        dao.create(receta);
    }

    @Override
    public Receta findById(Integer id) throws SQLException {
        return dao.queryForId(id);
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
    public void delete(Integer id) throws SQLException {
        dao.deleteById(id);
    }

    public void close() throws Exception {
        conexion.close();
    }

    // Buscar receta por código (codReceta)
    public Receta findByCodigo(String codReceta) throws SQLException {
        List<Receta> lista = dao.queryForEq("Codigo", codReceta);
        if (lista != null && !lista.isEmpty()) {
            return lista.get(0); // retorna la primera coincidencia
        }
        return null; // no encontrado
    }

    //  eliminar receta por código
    public void deleteByCodigo(String codReceta) throws SQLException {
        Receta r = findByCodigo(codReceta);
        if (r != null) {
            dao.delete(r);
        }
    }
}

