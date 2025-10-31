package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Medico;
import com.mycompany.ProyectoII.Medico;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO implements AbstractDAO<String, Medico> {

    private static final String URL = "jdbc:mysql://localhost:3306/bdhospital";
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
        Medico med = dao.queryForId(cedula);
        if (med != null && med.isEstado()) {
            return med;
        }
        return null;
    }

    @Override
    public List<Medico> findAll() throws SQLException {
        //solo busque los que tienen estado 1
        List<Medico> meds;
        meds = new ArrayList<>();

        for (Medico med : dao.queryForAll()) {
            if (med.isEstado()) {
                meds.add(med);
            }
        }
        return meds;
    }

    @Override
    public void update(Medico medico) throws SQLException {
        dao.update(medico);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        //esto es para borrados lógicos

        Medico med = this.findById(cedula);
        if (med != null) {
            med.setEstado(false);
            dao.update(med);
        } else {
            System.out.println("No se encontro el medico con la cedula: " + cedula);
        }

    }
    
    public boolean actualizarClave(String cedula, String nuevaClave) throws SQLException {
    Medico medico = findById(cedula);
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
