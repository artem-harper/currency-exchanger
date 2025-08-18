package servlets.exchangeRates;

import com.google.gson.Gson;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import exceptions.CurrencyNotFoundException;
import exceptions.Error;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ExchangeRateService;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRateDto> exchangeRateDtos;
        try {
            exchangeRateDtos = exchangeRateService.findALl();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        gson.toJson((exchangeRateDtos), resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));
        try {
            if (!Validator.isValidCode(baseCurrencyCode) || !Validator.isValidCode(targetCurrencyCode)) {
                throw new ValidationException();
            }
        } catch(ValidationException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error(Validator.getMessage()), resp.getWriter());
            return;
        }

        ExchangeRateDto exchangeRateDto = ExchangeRateDto.builder()
                .baseCurrencyDto(CurrencyDto.builder().code(baseCurrencyCode).build())
                .targetCurrencyDto(CurrencyDto.builder().code(targetCurrencyCode).build())
                .rate(rate)
                .build();

        try {
            ExchangeRateDto exchangeRateDtoSaved = exchangeRateService.save(exchangeRateDto);
            gson.toJson((exchangeRateDtoSaved), resp.getWriter());

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")){
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                gson.toJson(new Error("Данная валютная пара уже существует"), resp.getWriter());
            }else{
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                gson.toJson(new Error("Ошибка подключения к базе данных"), resp.getWriter());
            }
        }catch (CurrencyNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(new Error("Валюта отсутвует в базе данных"), resp.getWriter());
        }
    }
}
