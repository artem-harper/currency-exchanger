package servlets.currency;

import com.google.gson.Gson;

import dto.CurrencyDto;
import exceptions.Error;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;
import util.Validator;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CurrencyDto> list = currencyService.getAllCurrencies();
        gson.toJson((list), resp.getWriter());

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nameParam = req.getParameter("name");
        String codeParam = req.getParameter("code");
        String signParam = req.getParameter("sign");

        CurrencyDto currencyDto = CurrencyDto.builder()
                .name(nameParam)
                .code(codeParam)
                .sign(signParam)
                .build();

        if (nameParam.isEmpty() || codeParam.isEmpty() || signParam.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error("Отсутвует поле формы"), resp.getWriter());
            return;
        }

        try {
            if (!Validator.isValidRequest(nameParam, codeParam, signParam)){
                throw new ValidationException();
            }
        }catch (ValidationException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error(Validator.getMessage()), resp.getWriter());
            return;

        }

        try {
            CurrencyDto savedCurrency = currencyService.save(currencyDto);
            gson.toJson((savedCurrency), resp.getWriter());
        }catch(SQLException e){
            if (e.getMessage().contains("UNIQUE constraint failed")){
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                gson.toJson(new Error("Данная валюта уже существует"), resp.getWriter());
            }else{
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                gson.toJson(new Error("Ошибка подключения к базе данных"), resp.getWriter());
            }
        }

        }
}
