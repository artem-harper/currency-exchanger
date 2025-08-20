package servlets.exchangeRates;

import com.google.gson.Gson;
import dto.ExchangeRateDto;
import exceptions.Error;
import exceptions.ExchangeRateNotFoundException;
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

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            super.service(req, resp);
            return;
        }
        this.doPatch(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo()
                .replaceFirst("/", "");

        if (path.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error("Коды валют отсутвует в адресе"), resp.getWriter());
            return;
        }

        String[] codes = {path.substring(0, Validator.getCURRENT_CODE_LENGTH()), path.substring(Validator.getCURRENT_CODE_LENGTH())};

        try {
            ExchangeRateDto exchangeRateDto = exchangeRateService.findByCode(codes);

            gson.toJson((exchangeRateDto), resp.getWriter());
        }catch (ExchangeRateNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(new Error("Валютная пара отсутсвует"), resp.getWriter());
        }catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo()
                .replaceFirst("/", "");

        BigDecimal rate= null;


        try {
            String rateStr = getRate(req.getReader().readLine());
            rate = BigDecimal.valueOf(Double.parseDouble(rateStr));
        }catch (NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error("Некорректный ввод"), resp.getWriter());
        }catch (IllegalArgumentException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error("Курс отствует"), resp.getWriter());
        }

        if (!Validator.isValidRate(rate)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error(Validator.getMessage()), resp.getWriter());
            return;
        }


        if (path.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error("Коды валют отсутвует в адресе"), resp.getWriter());
            return;
        }

        String[] codes = {path.substring(0,Validator.getCURRENT_CODE_LENGTH()), path.substring(Validator.getCURRENT_CODE_LENGTH())};
        try {
            for (String code: codes){
                if (!Validator.isValidCode(code)){
                    throw new ValidationException();
                }
            }
        }catch (ValidationException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error(Validator.getMessage()), resp.getWriter());
            return;
        }

        try {
            ExchangeRateDto exchangeRateDto = exchangeRateService.findByCode(codes);
            ExchangeRateDto exchangeRateDtoUpdated = exchangeRateService.update(exchangeRateDto, rate);

            gson.toJson((exchangeRateDtoUpdated), resp.getWriter());
        }catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(new Error("Валютная пара отсутсвует"), resp.getWriter());
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    private static String getRate(String params) {

        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Request body is empty or null.");
        }

        for (String param : params.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        throw new IllegalArgumentException("Rate parameter not found.");
    }
}
