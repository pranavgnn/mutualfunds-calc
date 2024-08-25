package api;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

class DateDifference {
    public static int get(String d1, String d2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        
        LocalDate d1Date = LocalDate.parse(d1, formatter);
        LocalDate d2Date = LocalDate.parse(d2, formatter);

        return (int) ChronoUnit.DAYS.between(d2Date, d1Date);
    }
}

public class Calculator {
    
    public static double monthlyInterestRate(JsonNode recentNav, JsonNode firstNav) {
        int d = DateDifference.get(recentNav.get("date").asText(), firstNav.get("date").asText());
        double nf = recentNav.get("nav").asDouble();
        double ni = firstNav.get("nav").asDouble();

        return (Math.pow(nf / Math.max(ni, 1), 365.25 / Math.max(d, 1)) - 1) / 12;
    }
    
    public static int getTotalAmount(int p, int n, double r, int totalInvested) {
        int total = (int) (p * ((Math.pow(1 + r, n) - 1) / r * (1 + r)));
        
        if (r == 0)
            total = Math.max(total, totalInvested);
        
        return total;
    }
}