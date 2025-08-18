package services;

import dao.CurrencyDao;

import dto.CurrencyDto;
import entity.Currency;
import exceptions.CurrencyNotFoundException;
import mapper.CurrencyMapper;

import java.sql.SQLException;
import java.util.List;

public class CurrencyService {

    private static final CurrencyService INSTANCE = new CurrencyService();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    private CurrencyService(){

    }

    public CurrencyDto getCurrency(String code){
         return currencyDao.findByCode(code)
                 .map(currencyMapper::mapToDto)
                 .orElseThrow(CurrencyNotFoundException::new);
    }

    public List<CurrencyDto> getAllCurrencies(){
        return currencyDao.findAll().stream().map(currencyMapper::mapToDto).toList();
    }

    public CurrencyDto save(CurrencyDto currencyDto) throws SQLException {
        Currency currency = currencyMapper.mapToEntity(currencyDto);

        return currencyMapper.mapToDto(currencyDao.save(currency));
    }

    public static CurrencyService getInstance(){
        return INSTANCE;
    }
}
