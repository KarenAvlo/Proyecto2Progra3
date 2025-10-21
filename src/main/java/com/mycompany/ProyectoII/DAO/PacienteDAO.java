package com.mycompany.ProyectoII.DAO;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.mycompany.ProyectoII.Paciente;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class PacienteDAO implements AbstractDAO<String, Paciente> {

    private static final String URL = "jdbc:mysql://localhost:3306/bdhospital";
    private static final String USUARIO = "root";
    private static final String CLAVE = "root";

    private final ConnectionSource conexion;
    private final Dao<Paciente, String> dao;

    public PacienteDAO() throws SQLException {
        this.conexion = new JdbcConnectionSource(URL, USUARIO, CLAVE);
        this.dao = DaoManager.createDao(conexion, Paciente.class);
    }

    @Override
    public void add(Paciente paciente) throws SQLException {
        dao.create(paciente);
    }

    @Override
    public Paciente findById(String cedula) throws SQLException {
        //busque solo los de estado 1 (osea los disponibles)
        Paciente pa = dao.queryForId(cedula);
        if (pa != null && pa.isEstado()) {
            return pa;
        }
        return null;
    }

    @Override
    public List<Paciente> findAll() throws SQLException {
                //solo busque los que tienen estado 1
        List<Paciente> pac;
        pac = new ArrayList<>();

        for (Paciente pa : dao.queryForAll()) {
            if (pa.isEstado()) {
                pac.add(pa);
            }
        }
        return pac;
    }

    @Override
    public void update(Paciente paciente) throws SQLException {
        dao.update(paciente);
    }

    @Override
    public void delete(String cedula) throws SQLException {
        Paciente pa = this.findById(cedula);
        if (pa != null) {
            pa.setEstado(false);
            dao.update(pa);
        } else {
            System.out.println("No se encontro el paciente con cédula" + cedula);
        }
    }

    public void close() throws Exception {
        conexion.close();
    }
}
