package com.mycompany.ProyectoII.DAO;

import java.sql.SQLException;
import java.util.List;


public interface AbstractDAO <K,T> {
    
    public void add(T e) throws SQLException;

    public T findById(K id) throws SQLException;

    public List<T> findAll() throws SQLException;

    public void update(T e) throws SQLException;

    public void delete(K id) throws SQLException;
}
