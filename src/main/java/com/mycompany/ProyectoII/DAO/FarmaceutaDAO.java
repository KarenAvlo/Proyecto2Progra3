package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Farmaceuta;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter

public class FarmaceutaDAO implements AbstractDAO<String, Farmaceuta> {

    private static final String URL = "jdbc:mysql://localhost:3306/bdhospital";
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
               //Buscar solo los que tienen estado 1
        Farmaceuta farma = dao.queryForId(cedula);
        if (farma != null && farma.isEstado()) {
            return farma;
        }
        return null;
    }

    @Override
    public List<Farmaceuta> findAll() throws SQLException {
            //solo busque los que tienen estado 1
        List<Farmaceuta> far;
        far = new ArrayList<>();

        for (Farmaceuta fa : dao.queryForAll()) {
            if (fa.isEstado()) {
                far.add(fa);
            }
        }
        return far;
    }

    @Override
    public void update(Farmaceuta farmaceuta) throws SQLException {
        dao.update(farmaceuta);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        Farmaceuta farma = this.findById(cedula);

        if (farma != null) {
            farma.setEstado(false);
            dao.update(farma);
        } else {
            System.out.println("No se encontro farmaceuta con la cedula: " + cedula);
        }
    }
public boolean actualizarClave(String cedula, String nuevaClave) throws SQLException {
    Farmaceuta medico = findById(cedula);
    if (medico != null) {
        medico.setClave(nuevaClave);
        dao.update(medico);
        return true;
    }
    return false;
}
    public void close() throws Exception {
        conexion.close();
    }
}
