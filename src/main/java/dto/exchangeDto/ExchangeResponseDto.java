package dto.exchangeDto;

import dto.CurrencyDto;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder

public class ExchangeResponseDto {
    CurrencyDto baseCurrency;
    CurrencyDto targetCurrency;
    BigDecimal rate;
    BigDecimal amount;
    BigDecimal convertedAmount;
}
