package util;

import lombok.Getter;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

@UtilityClass
public class Validator {
    @Getter
    int CURRENT_CODE_LENGTH = 3;
    int MAX_NAME_LENGTH = 30;
    int MIN_NAME_LENGTH = 3;
    int CURRENT_SIGN_LENGTH = 4;
    int MIN_RATE = 0;
    @Getter
    String message;


    public static boolean isValidCode(String code){
        message="Невверно введен код";
        return code.length() == CURRENT_CODE_LENGTH && code.matches("[A-Z]+");
    }
    public static boolean isValidName(String name){
        message="Невверно введено имя";
        return name.length() < MAX_NAME_LENGTH && name.length() > MIN_NAME_LENGTH;
    }
    public static boolean isValidSign(String sign){
        message="Невверно введен знак";
        return sign.length() < CURRENT_SIGN_LENGTH ;
    }
    public static boolean isValidRate(BigDecimal rate){
        message="Невверно введен курс";
        return rate.compareTo(BigDecimal.ZERO) > 0;
    }

    public static boolean isValidRequest(String nameParam, String codeParam, String signParam) {
        return isValidCode(codeParam) && isValidName(nameParam) && isValidSign(signParam);
    }

}
