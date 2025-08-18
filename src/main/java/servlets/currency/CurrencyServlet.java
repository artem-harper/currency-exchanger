package servlets.currency;

import com.google.gson.Gson;
import dto.CurrencyDto;
import exceptions.CurrencyNotFoundException;
import exceptions.Error;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private final CurrencyService currencyService = CurrencyService.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().replaceFirst("/", "").toUpperCase();

        if (code.isEmpty()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new Error("Код валюты отсутвует в адресе") , resp.getWriter());
            return;
        }

        try {
            CurrencyDto Currency = currencyService.getCurrency(code);
            gson.toJson((Currency), resp.getWriter());

        }catch (CurrencyNotFoundException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(new Error("Валюта отсутвует"), resp.getWriter());

        }

    }

}
