package pattern.servicelocator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;
import database.Database;

public class Cache {
	private static final String GEOCODING_API_KEY = "&key=[GEOCODING_API_KEY]";
	private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
	private static final String DARKSKY_API_KEY = "[DARKSKY_API_KEY]/";
	private static final String DARKSKY_API_URL = "https://api.darksky.net/forecast/";
	private static final String DARKSKY_API_PARAMETERS = "?si=summary";
	private static final int FIVE_MINUTES = 300000;
	private static int INSERT_IS_SUCCESSFUL = 0;
	private Database db = new Database();
	private ResultSet cachedForecast;
	
	public Cache() {
		try {
			cachedForecast = db.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getCachedForecast(String city) throws SQLException {
		while(cachedForecast.next()) {
			String cachedCity = cachedForecast.getString("city");
			String cachedData = cachedForecast.getString("cached_forecast");
			
			if(cachedCity.equals(city.toLowerCase())) {
				return cachedData;
			}
		}
		return null;
	}
	
	public String addForecast(String city) throws SQLException, MalformedURLException, IOException, JSONException {
		String forecast = this.getForecast(city);
		INSERT_IS_SUCCESSFUL = db.addRow(city, forecast);
		
		return forecast;
	}
	
	
	
    private String[] getCoordinates(URL urlToRead) throws IOException, JSONException {
    	StringBuilder data = this.getURLData(urlToRead);

        JSONObject results = new JSONObject(data.toString()).getJSONArray("results")
        												    .getJSONObject(0);
        
        JSONObject location = results.getJSONObject("geometry")
    							  	 .getJSONObject("location");
        
        String address = results.getString("formatted_address");
        
        String[] coordinates = {location.getString("lat"), location.getString("lng"), address};
        return coordinates;
    }
    
    private String getForecast(String city) throws MalformedURLException, IOException, JSONException {
    	String[] coordinates = this.getCoordinates(new URL(GEOCODING_API_URL + city.toLowerCase() + GEOCODING_API_KEY));
    	StringBuilder data = this.getURLData(new URL(DARKSKY_API_URL
    											   + DARKSKY_API_KEY
    											   + coordinates[0] + "," + coordinates[1]
    											   + DARKSKY_API_PARAMETERS));
    	
    	JSONObject json = new JSONObject(data.toString());
    	JSONObject dailyData = json.getJSONObject("daily")
    						  	   .getJSONArray("data")
    						  	   .getJSONObject(0);
    	
    	String summary = dailyData.getString("summary");
    	String temperature = dailyData.getString("temperatureHigh") + "�C";

    	return summary + " " + temperature;
    }
    
    private StringBuilder getURLData(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        
        InputStream result = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(result));
        
        StringBuilder data = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            data.append(line + '\n');
        }
        rd.close();
        
        return data;
    }
    
    void removeCachedData(String city) {
    	if(!(INSERT_IS_SUCCESSFUL > 0)) return;
    	Timer timer = new Timer();
    	
    	timer.schedule(new TimerTask() {
    		@Override
    		public void run() {
    			try {
					db.removeData(city);
				} catch (SQLException e) {
					e.printStackTrace();
				}
    		}
    	}, FIVE_MINUTES);
    }
}
