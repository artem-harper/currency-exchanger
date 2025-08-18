package dao;

import entity.Currency;
import lombok.SneakyThrows;
import util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao implements Dao<String, Currency>{

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private final String FIND_CURRENCY = """
            SELECT id, code, fullName, sign FROM Currencies WHERE code=?
            """;

    private final String FIND_ALL= """
            SELECT id, code, fullName, sign FROM Currencies    
            """;

    private final String SAVE_CURRENCY = """
            INSERT INTO Currencies(code, fullName, sign) VALUES (?, ?, ?)
             """;


    private CurrencyDao(){

    }

    @Override

    public Optional<Currency> findByCode(String value) {
        try(var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(FIND_CURRENCY)){

            prepareStatement.setObject(1, value);
            var resultSet = prepareStatement.executeQuery();

            Currency currency = null;

            if(resultSet.next()){
                currency = buildCurrency(resultSet);
            }

            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Currency> findAll() {
        try(var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(FIND_ALL)){

            List<Currency> currencyList = new ArrayList<>();

            var resultSet = prepareStatement.executeQuery();

            while (resultSet.next()){
                currencyList.add(buildCurrency(resultSet));
            }

            return currencyList;

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    @SneakyThrows
    public Currency save(Currency value)  {

        try(var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(SAVE_CURRENCY, Statement.RETURN_GENERATED_KEYS)){

            prepareStatement.setObject(1, value.getCode());
            prepareStatement.setObject(2, value.getFullName());
            prepareStatement.setObject(3, value.getSign());

            prepareStatement.executeUpdate();

            var keySet = prepareStatement.getGeneratedKeys();
            keySet.next();

            value.setId(keySet.getInt(1));

            return value;

        }
    }

    private static Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return Currency.builder()
                .id(resultSet.getObject("id", Integer.class))
                .code(resultSet.getObject("code", String.class))
                .fullName(resultSet.getObject("fullName", String.class))
                .sign(resultSet.getObject("sign", String.class))
                .build();
    }

    public static CurrencyDao getInstance(){
        return INSTANCE;
    }
}
