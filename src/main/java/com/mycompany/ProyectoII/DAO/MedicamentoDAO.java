package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Medicamento;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicamentoDAO implements AbstractDAO<String, Medicamento> {

    private static final String URL = "jdbc:mysql://localhost:3306/bdhospital";
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
        //borrado lógico
        Medicamento m = dao.queryForId(codigo);
        if (m != null) {
            m.setEstado(false);
            dao.update(m);
        } else {
            System.out.println("No se encontro el medicamento con codigo" + codigo);
        }
    }

    @Override
    public Medicamento findById(String codigo) throws SQLException {
                  //Buscar solo los que tienen estado 1
        Medicamento medi = dao.queryForId(codigo);
        if (medi != null && medi.isEstado()) {
            return medi;
        }
        return null;
    }

    @Override
    public List<Medicamento> findAll() throws SQLException {
                 //solo busque los que tienen estado 1
        List<Medicamento> far;
        far = new ArrayList<>();

        for (Medicamento medi : dao.queryForAll()) {
            if (medi.isEstado()) {
                far.add(medi);
            }
        }
        return far;
    }

    public Dao<Medicamento, String> getDao() {
        return dao;
    }

    public void close() throws Exception {
        conexion.close();
    }
}
