
package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Medicamento;
import java.sql.SQLException;
import java.util.List;


public class MedicamentoDAO implements AbstractDAO<String, Medicamento> {

    private static final String URL = "jdbc:mysql://localhost:3306/mybd";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Medicamento, String> dao;

    // Constructor que crea la conexión automáticamente
    public MedicamentoDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Medicamento.class);
    }

    // Constructor que recibe una conexión externa
    public MedicamentoDAO(ConnectionSource connectionSource) throws SQLException {
        this.conexion = connectionSource;
        this.dao = DaoManager.createDao(connectionSource, Medicamento.class);
    }

    @Override
    public void add(Medicamento m) throws SQLException {
        dao.create(m);
    }

    @Override
    public void update(Medicamento m) throws SQLException {
        dao.update(m);
    }

    @Override
    public void delete(String codigo) throws SQLException {
        Medicamento m = dao.queryForId(codigo);
        if (m != null) {
            dao.delete(m);
        }
    }

    @Override
    public Medicamento findById(String codigo) throws SQLException {
        return dao.queryForId(codigo);
    }

    @Override
    public List<Medicamento> findAll() throws SQLException {
        return dao.queryForAll();
    }

    public Dao<Medicamento, String> getDao() {
        return dao;
    }
    
    public void close() throws Exception {
        conexion.close();
    }
}