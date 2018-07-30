package pattern.servicelocator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import org.json.JSONException;

public class ServiceLocator {
	private Cache cache = new Cache();
	
	public String getForecast(String city) throws SQLException, MalformedURLException, IOException, JSONException {
		String cachedForecast = cache.getCachedForecast(city);
		
		if(cachedForecast != null) {
			return cachedForecast;
		}
		
		String forecast = cache.addForecast(city);
		cache.removeCachedData(city);
		
		return forecast;
	}
}
