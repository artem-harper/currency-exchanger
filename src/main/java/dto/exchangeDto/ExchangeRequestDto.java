package dto.exchangeDto;

import dto.CurrencyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
@AllArgsConstructor

public class ExchangeRequestDto {
    CurrencyDto base;
    CurrencyDto target;
    BigDecimal amount;
}
