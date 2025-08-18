package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRate {

    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
}
