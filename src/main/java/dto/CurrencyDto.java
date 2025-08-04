package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor

public class CurrencyDto {
    int id;
    String code;
    String fullName;
    String sign;
}
