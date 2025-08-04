package dao;


import java.util.List;

public interface Dao<K, T> {

    T get(K value);
    List<T> getAll();
    void add(T value);
}
