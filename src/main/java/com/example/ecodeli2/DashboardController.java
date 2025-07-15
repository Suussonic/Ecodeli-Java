package com.example.ecodeli2;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.geometry.Insets;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Map;

public class DashboardController {

    @FXML
    private StackPane contentPane;

    @FXML
    public void initialize() {
        showHome();
    }

    @FXML
    private void showHome() {
        contentPane.getChildren().clear();

        Map<String, Integer> roleDistribution = UserData.getUserRoleDistribution();
        int total = roleDistribution.values().stream().mapToInt(Integer::intValue).sum();

        if (total == 0) {
            Label noDataLabel = new Label("Aucune donnée utilisateur disponible");
            noDataLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            contentPane.getChildren().add(noDataLabel);
            return;
        }

        PieChart pie = new PieChart();
        pie.setTitle("Distribution des rôles utilisateurs");

        roleDistribution.forEach((roleName, count) -> {
            PieChart.Data slice = new PieChart.Data(roleName, count);
            pie.getData().add(slice);
            
            double percentage = (count.doubleValue() / total) * 100;
            String tooltipText = String.format("%s: %d utilisateurs (%.1f%%)", roleName, count, percentage);
            
            // Ajout du tooltip après que le node soit créé
            slice.nodeProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    Tooltip.install(newValue, new Tooltip(tooltipText));
                }
            });
        });

        contentPane.getChildren().add(pie);
    }

    @FXML
    private void showSales() {
        contentPane.getChildren().clear();
        LineChart<String, Number> chart = ChartsBuilder.buildSalesChartAllYears();
        contentPane.getChildren().add(chart);
    }

    @FXML
    private void exportData() {
        contentPane.getChildren().clear();

        Button btnPng = new Button("Exporter en PNG");
        btnPng.setStyle("-fx-background-color: #00EC64; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15px 30px;");
        btnPng.setPrefWidth(200);
        btnPng.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Enregistrer le graphique en PNG");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image PNG", "*.png"));
            File file = chooser.showSaveDialog(contentPane.getScene().getWindow());
            if (file != null) {
                try {
                    // Créer le graphique des rôles utilisateurs
                    PieChart userRoleChart = createUserRolePieChartForExport();
                    WritableImage image = snapshotNode(userRoleChart, 1600, 900);
                    BufferedImage buffered = SwingFXUtils.fromFXImage(image, null);
                    ImageIO.write(buffered, "png", file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button btnPdf = new Button("Exporter en PDF");
        btnPdf.setStyle("-fx-background-color: #146EFF; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 15px 30px;");
        btnPdf.setPrefWidth(200);
        btnPdf.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Enregistrer le graphique en PDF");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Document PDF", "*.pdf"));
            File file = chooser.showSaveDialog(contentPane.getScene().getWindow());
            if (file != null) {
                try {
                    Document document = new Document(PageSize.A4, 36, 36, 36, 36);
                    PdfWriter.getInstance(document, new FileOutputStream(file));
                    document.open();

                    LineChart<String, Number> chart1 = ChartsBuilder.buildSalesChartAllYears();
                    WritableImage img1 = snapshotNode(chart1, 1200, 800);
                    File tempFile1 = new File("temp_chart1.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(img1, null), "png", tempFile1);

                    // Graphique des rôles utilisateurs
                    PieChart chart2 = createUserRolePieChartForExport();
                    WritableImage img2 = snapshotNode(chart2, 1000, 800);
                    File tempFile2 = new File("temp_chart2.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(img2, null), "png", tempFile2);

                    Image pdfImg1 = Image.getInstance(tempFile1.getAbsolutePath());
                    pdfImg1.scaleToFit(PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() / 2 - 50);
                    document.add(pdfImg1);

                    document.add(new com.lowagie.text.Paragraph("\n"));

                    Image pdfImg2 = Image.getInstance(tempFile2.getAbsolutePath());
                    pdfImg2.scaleToFit(PageSize.A4.getWidth() - 72, PageSize.A4.getHeight() / 2 - 50);
                    document.add(pdfImg2);

                    document.close();

                    tempFile1.delete();
                    tempFile2.delete();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        VBox box = new VBox(20, new Label("Exporter le graphique :"), new VBox(10, btnPng, btnPdf));
        box.setPadding(new Insets(20));
        contentPane.getChildren().add(box);
    }

    private WritableImage snapshotNode(Node node, double width, double height) {
        if (node instanceof Region) {
            ((Region) node).setPrefSize(width, height);
        }
        StackPane root = new StackPane(node);
        Scene scene = new Scene(root);
        root.applyCss();
        root.layout();
        WritableImage image = new WritableImage((int) width, (int) height);
        return node.snapshot(null, image);
    }

    private PieChart createUserRolePieChartForExport() {
        PieChart pie = new PieChart();
        pie.setTitle("Distribution des rôles utilisateurs");
        
        Map<String, Integer> roleDistribution = UserData.getUserRoleDistribution();
        roleDistribution.forEach((roleName, count) -> {
            PieChart.Data slice = new PieChart.Data(roleName, count);
            pie.getData().add(slice);
        });
        
        return pie;
    }
}
