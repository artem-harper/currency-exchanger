package services;

import dao.ExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import dto.exchangeDto.ExchangeRequestDto;
import dto.exchangeDto.ExchangeResponseDto;
import entity.ExchangeRate;
import exceptions.ExchangeRateNotFoundException;
import mapper.CurrencyMapper;
import mapper.ExchangeRateMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {

    private static final ExchangeService INSTANCE = new ExchangeService();

    private final String CROSS_CURRENCY = "USD";
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();

    public ExchangeResponseDto exchange(ExchangeRequestDto exchangeRequestDto) throws SQLException {

        String baseCurrencyCode = exchangeRequestDto.getBase().getCode();
        String targetCurrencyCode = exchangeRequestDto.getTarget().getCode();
        BigDecimal amount = exchangeRequestDto.getAmount();
        
        String[] codes = {baseCurrencyCode, targetCurrencyCode};
        
        Optional<ExchangeRate> maybeExchangeRateDto = exchangeRateDao.findByCode(codes);

        BigDecimal convertedAmount;
        ExchangeRateDto exchangeRateDto;

        if (maybeExchangeRateDto.isEmpty()){

            return crossExchange(baseCurrencyCode, targetCurrencyCode, amount);
        }

        exchangeRateDto = exchangeRateMapper.mapToDto(maybeExchangeRateDto.get());

        if (targetCurrencyCode.equals(exchangeRateDto.getBaseCurrencyDto().getCode())){

            BigDecimal reversedRate = new BigDecimal(1).divide(exchangeRateDto.getRate(), 2, RoundingMode.HALF_UP);
            convertedAmount = reversedRate.multiply(amount);

            return buildExchangeResponse(exchangeRateDto.getTargetCurrencyDto(), exchangeRateDto.getBaseCurrencyDto(), reversedRate, amount, convertedAmount);
        }

        BigDecimal defaultRate = exchangeRateDto.getRate();

        convertedAmount = defaultRate.multiply(amount);

        return buildExchangeResponse(exchangeRateDto.getBaseCurrencyDto(), exchangeRateDto.getTargetCurrencyDto(), defaultRate, amount, convertedAmount);

    }

    private ExchangeResponseDto crossExchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        BigDecimal convertedAmount;
        Optional<ExchangeRate> maybeBaseExchange = exchangeRateDao.findByCode(new String[]{CROSS_CURRENCY, baseCurrencyCode});
        Optional<ExchangeRate> maybeTargetExchange = exchangeRateDao.findByCode(new String[]{CROSS_CURRENCY, targetCurrencyCode});

        if (maybeBaseExchange.isPresent() && maybeTargetExchange.isPresent()){

            ExchangeRateDto baseExchange = exchangeRateMapper.mapToDto(maybeBaseExchange.get());
            ExchangeRateDto targetExchange = exchangeRateMapper.mapToDto(maybeTargetExchange.get());

            BigDecimal newRate = targetExchange.getRate()
                    .divide(baseExchange.getRate(), 2, RoundingMode.HALF_UP);
            convertedAmount = newRate.multiply(amount);

            return buildExchangeResponse(baseExchange.getTargetCurrencyDto(), targetExchange.getTargetCurrencyDto(), newRate, amount, convertedAmount);
        }else{
            throw new ExchangeRateNotFoundException();
        }
    }

    private static ExchangeResponseDto buildExchangeResponse(CurrencyDto currency1, CurrencyDto currency2, BigDecimal rate, BigDecimal amount, BigDecimal convertedAmount) {
        return ExchangeResponseDto.builder()
                .baseCurrency(currency1)
                .targetCurrency(currency2)
                .rate(rate)
                .amount(amount)
                .convertedAmount(convertedAmount)
                .build();
    }

    public static ExchangeService getInstance(){
        return INSTANCE;
    }

}
