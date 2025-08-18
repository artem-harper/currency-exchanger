package mapper;

import dto.ExchangeRateDto;
import entity.ExchangeRate;

public class ExchangeRateMapper implements Mapper<ExchangeRate, ExchangeRateDto>{
    private static final ExchangeRateMapper INSTANCE = new ExchangeRateMapper();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    private ExchangeRateMapper(){

    }

    @Override
    public ExchangeRateDto mapToDto(ExchangeRate object) {
        return ExchangeRateDto.builder()
                .id(object.getId())
                .baseCurrencyDto(currencyMapper.mapToDto(object.getBaseCurrency()))
                .targetCurrencyDto(currencyMapper.mapToDto(object.getTargetCurrency()))
                .rate(object.getRate())
                .build();
    }

    @Override
    public ExchangeRate mapToEntity(ExchangeRateDto object) {

        return ExchangeRate.builder()
                .id(object.getId())
                .baseCurrency(currencyMapper.mapToEntity(object.getBaseCurrencyDto()))
                .targetCurrency(currencyMapper.mapToEntity(object.getTargetCurrencyDto()))
                .rate(object.getRate())
                .build();
    }

    public static ExchangeRateMapper getInstance(){
        return INSTANCE;
    }
}
