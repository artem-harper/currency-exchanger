package dao;

import entity.Currency;
import entity.ExchangeRate;
import exceptions.CurrencyNotFoundException;
import lombok.Generated;
import util.ConnectionManager;

import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ExchangeRateDao implements Dao<String[], ExchangeRate> {

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    private final String FIND_EXCHANGE_RATE = """
            SELECT er.id AS er_id,
                   c1.id AS base_id,
                   c1.code AS base_code,
                   c1.fullName AS base_fullName,
                   c1.sign AS base_sign,
                   c2.id AS target_id,
                   c2.code AS target_code,
                   c2.fullName AS target_fullName,
                   c2.sign AS target_sign,
                   er.rate AS er_rate     
            FROM ExchangeRates AS er 
                    JOIN Currencies AS c1 ON er.baseCurrencyId=c1.id
                    JOIN Currencies AS c2 ON er.targetCurrencyId=c2.id
            WHERE (c1.code = ? AND c2.code = ?) OR (c1.code = ? AND c2.code = ?)
            """;

    private final String FIND_ALL_EXCHANGE_RATES= """
            SELECT er.id AS er_id,
                   c1.id AS base_id,
                   c1.code AS base_code,
                   c1.fullName AS base_fullName,
                   c1.sign AS base_sign,
                   c2.id AS target_id,
                   c2.code AS target_code,
                   c2.fullName AS target_fullName,
                   c2.sign AS target_sign,
                   er.rate AS er_rate     
            FROM ExchangeRates AS er 
                    JOIN Currencies AS c1 ON er.baseCurrencyId=c1.id
                    JOIN Currencies AS c2 ON er.targetCurrencyId=c2.id""";

    private final String SAVE_CURRENCY_EXCHANGE = """
            INSERT INTO ExchangeRates(baseCurrencyId, targetCurrencyId, rate)
            VALUES (?, ?, ?)            
            """;

    private final String UPDATE_CURRENCY_EXCHANGE = """
            UPDATE ExchangeRates SET rate=? WHERE baseCurrencyId=? AND targetCurrencyId=?;
            """;

    @Override
    public Optional<ExchangeRate> findByCode(String[] codes) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_EXCHANGE_RATE)){

            String baseCurrencyCode = codes[0];
            String targetCurrencyCode = codes[1];

            prepareStatement.setObject(1, baseCurrencyCode);
            prepareStatement.setObject(2, targetCurrencyCode);

            prepareStatement.setObject(3, targetCurrencyCode);
            prepareStatement.setObject(4, baseCurrencyCode);

            var resultSet = prepareStatement.executeQuery();


            ExchangeRate exchangeRate = null;

            if (resultSet.next()) {
                exchangeRate = buildExchangeRate(resultSet);
            }

            return Optional.ofNullable(exchangeRate);

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<ExchangeRate> findAll() throws SQLException{
        try(var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(FIND_ALL_EXCHANGE_RATES)){

            var resultSet = prepareStatement.executeQuery();

            List<ExchangeRate> exchangeRateList = new ArrayList<>();

            while (resultSet.next()){
                exchangeRateList.add(buildExchangeRate(resultSet));
            }

            return exchangeRateList;

        }
    }

    @Override
    public ExchangeRate save(ExchangeRate value) throws SQLException {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_CURRENCY_EXCHANGE, Statement.RETURN_GENERATED_KEYS)){

            Optional<Currency> maybeBaseCurrency = currencyDao.findByCode(value.getBaseCurrency().getCode());
            Optional<Currency> maybeTargetCurrency = currencyDao.findByCode(value.getTargetCurrency().getCode());

            if (maybeBaseCurrency.isEmpty() || maybeTargetCurrency.isEmpty()){
                throw new CurrencyNotFoundException();
            }

            prepareStatement.setInt(1, maybeBaseCurrency.get().getId());
            prepareStatement.setInt(2, maybeTargetCurrency.get().getId());
            prepareStatement.setBigDecimal(3, value.getRate());

            prepareStatement.executeUpdate();

            ResultSet key = prepareStatement.getGeneratedKeys();
            key.next();

            value.setId(key.getInt(1));
            value.setBaseCurrency(maybeBaseCurrency.get());
            value.setTargetCurrency(maybeTargetCurrency.get());

            return value;
        }
    }

    public ExchangeRate update(ExchangeRate exchangeRate) throws  SQLException{
        try(var connection = ConnectionManager.get();
            var prepareStatement = connection.prepareStatement(UPDATE_CURRENCY_EXCHANGE)){

            prepareStatement.setBigDecimal(1, exchangeRate.getRate());
            prepareStatement.setInt(2, exchangeRate.getBaseCurrency().getId());
            prepareStatement.setInt(3, exchangeRate.getTargetCurrency().getId());

            prepareStatement.executeUpdate();

            return exchangeRate;
        }

    }

    private static ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return ExchangeRate.builder()
                .id(resultSet.getInt("er_id"))
                .baseCurrency(buildCurrency(resultSet, "base_"))
                .targetCurrency(buildCurrency(resultSet, "target_"))
                .rate(resultSet.getBigDecimal("er_rate"))
                .build();
    }

    private static Currency buildCurrency(ResultSet resultSet, String table) throws SQLException {
        return Currency.builder()
                .id(resultSet.getInt(table + "id"))
                .code(resultSet.getString(table + "code"))
                .fullName(resultSet.getString(table + "fullName"))
                .sign(resultSet.getString(table + "sign"))
                .build();
    }

    public static ExchangeRateDao getInstance(){
        return INSTANCE;
    }
}
