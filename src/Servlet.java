import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;

import pattern.servicelocator.ServiceLocator;


@WebServlet(name = "forecast", urlPatterns = { "/forecast" })
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String city = request.getParameter("queriedCity");
	    
	    ServiceLocator service = new ServiceLocator();
	    try {
			String forecast = service.getForecast(city);
			response.getWriter().print(forecast);
		} catch (SQLException | JSONException e) {
			e.printStackTrace();
		}
	}
}
