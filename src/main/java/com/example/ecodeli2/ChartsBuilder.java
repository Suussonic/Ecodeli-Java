package com.example.ecodeli2;

import javafx.collections.FXCollections;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import java.util.List;

public class ChartsBuilder {

    public static LineChart<String, Number> buildSalesChartAllYears() {
        CategoryAxis xAxis = new CategoryAxis();
        List<String> months = Stream.of(MonthEnum.values())
                .map(MonthEnum::getDisplayName)
                .collect(Collectors.toList());
        xAxis.setAutoRanging(false);
        xAxis.setCategories(FXCollections.observableArrayList(months));
        xAxis.setTickLabelRotation(45);
        xAxis.setLabel("Month");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Revenue");

        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Monthly revenue (all years)");

        for (int year : DashboardData.getAvailableYears()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(String.valueOf(year));
            DashboardData.getMonthlySales(year)
                    .forEach((monthIdx, value) -> {
                        String monthName = MonthEnum.of(monthIdx).getDisplayName();
                        series.getData().add(new XYChart.Data<>(monthName, value));
                    });
            chart.getData().add(series);
        }

        return chart;
    }
}