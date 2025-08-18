package mapper;

import dto.CurrencyDto;
import entity.Currency;

public class CurrencyMapper implements Mapper<Currency, CurrencyDto> {

    private static final CurrencyMapper INSTANCE = new CurrencyMapper();

    private CurrencyMapper(){

    }

    @Override
    public CurrencyDto mapToDto(Currency object) {
        return CurrencyDto.builder()
                .id(object.getId())
                .code(object.getCode())
                .name(object.getFullName())
                .sign(object.getSign())
                .build();
    }

    @Override
    public Currency mapToEntity(CurrencyDto object) {
        return Currency.builder()
                .id(object.getId())
                .code(object.getCode())
                .fullName(object.getName())
                .sign(object.getSign())
                .build();
    }

    public static CurrencyMapper getInstance(){
        return INSTANCE;
    }
}
