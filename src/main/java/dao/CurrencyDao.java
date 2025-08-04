package dao;

import entity.Currency;

import java.util.List;

public class CurrencyDao implements Dao<String, Currency>{
    @Override
    public Currency get(String value) {
        return null;
    }

    @Override
    public List<Currency> getAll() {
        return List.of();
    }

    @Override
    public void add(Currency value) {

    }
}
