package com.example.ecodeli2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class DashboardData {

    private static final JsonNode revenueRoot;
    static {
        JsonNode tmp = null;
        try (InputStream is = DashboardData.class.getResourceAsStream("revenue.json")) {
            ObjectMapper mapper = new ObjectMapper();
            tmp = mapper.readTree(is).path("revenue");
        } catch (Exception e) {
            e.printStackTrace();
        }
        revenueRoot = tmp;
    }

    public static List<Integer> getAvailableYears() {
        if (revenueRoot == null) {
            return Collections.emptyList();
        }
        List<Integer> years = new ArrayList<>();
        revenueRoot.fieldNames()
                .forEachRemaining(name -> years.add(Integer.parseInt(name)));
        Collections.sort(years);
        return years;
    }

    public static Map<Integer, Double> getMonthlySales(int year) {
        Map<Integer, Double> sales = new LinkedHashMap<>();
        if (revenueRoot == null || !revenueRoot.has(String.valueOf(year))) {
            return sales;
        }

        double[] totals = new double[12];
        JsonNode yearNode = revenueRoot.get(String.valueOf(year));
        yearNode.fields().forEachRemaining(e -> {
            JsonNode arr = e.getValue();
            for (int i = 0; i < arr.size() && i < 12; i++) {
                totals[i] += arr.get(i).asDouble();
            }
        });
        for (int m = 1; m <= 12; m++) {
            sales.put(m, totals[m - 1]);
        }
        return sales;
    }

    public static Map<String, Double> getYearlyRevenueByCategory(int year) {
        Map<String, Double> result = new LinkedHashMap<>();
        if (revenueRoot == null || !revenueRoot.has(String.valueOf(year))) {
            return result;
        }

        JsonNode yearNode = revenueRoot.get(String.valueOf(year));
        Iterator<Map.Entry<String, JsonNode>> fields = yearNode.fields();

        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            double sum = 0;
            for (JsonNode value : entry.getValue()) {
                sum += value.asDouble();
            }
            result.put(entry.getKey(), sum);
        }
        return result;
    }
}