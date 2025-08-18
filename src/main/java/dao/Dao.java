package dao;


import entity.Currency;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface Dao<K, T> {

    Optional<T> findByCode(K value) throws SQLException;
    List<T> findAll() throws SQLException;
    T save(T value) throws SQLException;

}
