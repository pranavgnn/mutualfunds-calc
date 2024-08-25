package api;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class API {
    private static final String url = "https://api.mfapi.in/mf";
    private static HttpClient client = HttpClient.newHttpClient();
    private static JsonNode allMfData;
    private static final HashMap <Integer, Double> returnsCache = new HashMap<Integer, Double>();
    
    public static HttpRequest createRequest(String url) {
        return HttpRequest.newBuilder().uri(URI.create(url)).build();
    }
    
    public static HttpResponse<String> GET(String url) {
        try {
            return client.send(createRequest(url), BodyHandlers.ofString());
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(API.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    public static JsonNode getAllSchemes() {
        if (allMfData == null) {
            HttpResponse<String> response = GET(url);
            
            if (response != null)
                allMfData = JSON.parse(response.body());
            
            if (allMfData == null)
                allMfData = JSON.parse("");
        }

        return allMfData;
    }
    
    public static double getSchemeReturns(int schemeCode) {
        
        if (schemeCode == -1) return 0f;
        
        if (returnsCache.get(schemeCode) != null)
            return returnsCache.get(schemeCode);
        
        HttpResponse<String> response = GET(url + "/" + schemeCode);
        JsonNode json = JSON.parse(response.body());
        if (json == null) return 0f;
        
        JsonNode data = json.get("data");
        JsonNode recentNav = data.get(0);
        JsonNode firstNav = data.get(data.size() - 1);
        
        double monthlyReturns = Calculator.monthlyInterestRate(recentNav, firstNav);
        returnsCache.put(schemeCode, monthlyReturns);
        
        return monthlyReturns;
    }
}
