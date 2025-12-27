package com.pend.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Placeholder simple currency converter.
 * RIGHT NOW it returns the amount unchanged. Use this method to wire a real API.
 *
 * If you want a quick demo, you can call a free API like exchangerate.host or exchangerate-api.com,
 * parse JSON (you may add a JSON library or parse manually).
 */
public class CurrencyAPI {

    public static double convert(double amount, String from, String to) {
        // possibly implement real API call & caching
        // but return just amount for now
        return amount;
    }

    // Example of a simple GET (not used currently)
    private static String simpleGet(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            StringBuilder sb = new StringBuilder();
            String l;
            while ((l = br.readLine()) != null) sb.append(l);
            return sb.toString();
        }
    }
}
