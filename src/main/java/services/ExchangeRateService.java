package services;

import dao.ExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import dto.exchangeDto.ExchangeRequestDto;
import dto.exchangeDto.ExchangeResponseDto;
import entity.ExchangeRate;
import exceptions.ExchangeRateNotFoundException;
import mapper.ExchangeRateMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ExchangeRateService {

    private final static ExchangeRateService INSTANCE = new ExchangeRateService();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();



    public ExchangeRateDto findByCode(String[] codes) throws SQLException{
        return exchangeRateDao.findByCode(codes)
                .map(exchangeRateMapper::mapToDto)
                .orElseThrow(ExchangeRateNotFoundException::new);
    }

    public List<ExchangeRateDto> findALl() throws SQLException {
        return exchangeRateDao.findAll()
                .stream().map(exchangeRateMapper::mapToDto)
                .toList();
    }

    public ExchangeRateDto save(ExchangeRateDto exchangeRateDto) throws SQLException{
        ExchangeRate exchangeRate = exchangeRateMapper.mapToEntity(exchangeRateDto);

        return exchangeRateMapper.mapToDto(exchangeRateDao.save(exchangeRate));
    }


    public ExchangeRateDto update(ExchangeRateDto exchangeRateDto, BigDecimal rate) throws SQLException{

        ExchangeRate exchangeRateEntity = exchangeRateMapper.mapToEntity(exchangeRateDto);
        exchangeRateEntity.setId(exchangeRateEntity.getId());
        exchangeRateEntity.setRate(rate);
        return exchangeRateMapper.mapToDto(exchangeRateDao.update(exchangeRateEntity));
    }



    public static ExchangeRateService getInstance(){
        return INSTANCE;
    }


}
