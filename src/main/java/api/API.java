package api;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {
    private static final String url = "https://api.mfapi.in/mf";
    private static JsonNode allMfData;
    private static final HashMap<Integer, Double> returnsCache = new HashMap<Integer, Double>();

    public static HttpURLConnection createConnection(String url) throws IOException {
        URL uri = new URL(url);
        return (HttpURLConnection) uri.openConnection();
    }

    public static String GET(String url) {
        try {
            HttpURLConnection connection = createConnection(url);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            } else {
                Logger.getLogger(API.class.getName()).log(Level.SEVERE, "Failed to retrieve data. Response code: " + responseCode);
                return null;
            }
        } catch (IOException ex) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static JsonNode getAllSchemes() {
        if (allMfData == null) {
            String response = GET(url);

            if (response != null)
                allMfData = JSON.parse(response);

            if (allMfData == null)
                allMfData = JSON.parse("");
        }

        return allMfData;
    }

    public static double getSchemeReturns(int schemeCode) {

        if (schemeCode == -1) return 0f;

        if (returnsCache.get(schemeCode) != null)
            return returnsCache.get(schemeCode);

        String response = GET(url + "/" + schemeCode);
        JsonNode json = JSON.parse(response);
        if (json == null) return 0f;

        JsonNode data = json.get("data");
        JsonNode recentNav = data.get(0);
        JsonNode firstNav = data.get(data.size() - 1);

        double monthlyReturns = Calculator.monthlyInterestRate(recentNav, firstNav);
        returnsCache.put(schemeCode, monthlyReturns);

        return monthlyReturns;
    }
}