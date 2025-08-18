package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor

public class ExchangeRateDto {
    int id;
    CurrencyDto baseCurrencyDto;
    CurrencyDto targetCurrencyDto;
    BigDecimal rate;
}
