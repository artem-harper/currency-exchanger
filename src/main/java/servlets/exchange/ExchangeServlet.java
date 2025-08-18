package servlets.exchange;

import com.google.gson.Gson;
import dto.CurrencyDto;
import dto.exchangeDto.ExchangeRequestDto;
import exceptions.Error;
import exceptions.ExchangeRateNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ExchangeService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeService exchangeService = ExchangeService.getInstance();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrency = req.getParameter("from");
        String targetCurrency = req.getParameter("to");
        BigDecimal amount = new BigDecimal(req.getParameter("amount"));

        ExchangeRequestDto exchangeRequestDto = ExchangeRequestDto.builder()
                .base(CurrencyDto.builder().code(baseCurrency).build())
                .target(CurrencyDto.builder().code(targetCurrency).build())
                .amount(amount).build();
       try {
            gson.toJson(exchangeService.exchange(exchangeRequestDto), resp.getWriter());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }catch (ExchangeRateNotFoundException  e){
           resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
           gson.toJson(new Error("Валютная пара отсутсвует"), resp.getWriter());
       }

    }
}
