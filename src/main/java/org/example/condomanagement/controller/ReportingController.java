package org.example.condomanagement.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.condomanagement.dao.BillingItemDao;
import org.example.condomanagement.dao.HouseholdDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Double;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ReportingController {

    @FXML
    private ComboBox<String> statTypeComboBox;
    @FXML
    private ComboBox<String> objectComboBox;
    @FXML
    private Button filterButton;
    @FXML
    private Button exportReportButton;
    @FXML
    private BarChart<String, Number> householdBarChart;
    @FXML
    private Label totalCountLabel;
    @FXML
    private Label collectedAmountLabel;
    @FXML
    private Label pendingAmountLabel;
    @FXML
    private Label collectionRateLabel;
    @FXML
    private Label topDebtor1CodeLabel;
    @FXML
    private Label topDebtor2CodeLabel;
    @FXML
    private Label topDebtor3CodeLabel;
    @FXML
    private Label topDebtor1AmountLabel;
    @FXML
    private Label topDebtor2AmountLabel;
    @FXML
    private Label topDebtor3AmountLabel;
    @FXML
    private TableView<BillingItemDao.BillingSummary> detailsTableView;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> householdIdColumn;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> householdNameColumn;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> amountCollectedColumn;
    @FXML
    private TableColumn<BillingItemDao.BillingSummary, String> amountDueColumn;

    private final HouseholdDao householdDao = new HouseholdDao();
    private final BillingItemDao billingItemDao = new BillingItemDao();
    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));


    @FXML
    void initialize() {
        List<BillingItemDao.BillingSummary> billingItemList=billingItemDao.getBillingSummaryByHousehold();

        //Điền thông tin các Label tương ứng
        fillLabel(billingItemList);

        //Hiển thị biểu đồ thống kê
        loadChar(billingItemList);
        householdBarChart.setCategoryGap(10);
        householdBarChart.setBarGap(5);
        householdBarChart.setTitle("Mức đóng của các hộ");

        //top 3 hộ nợ nhiều nhất
        loadTopList(billingItemList);

        //tabel-view
        loadTable(billingItemList);

        exportReportButton.setOnAction(this::handleExportReport);


    }

    void fillLabel(List<BillingItemDao.BillingSummary> billingItemList){
        int totalHouseholds = billingItemList.size();

        double totalCollected = billingItemList.stream()
                .mapToDouble(item -> item.getTotalActualAmount().doubleValue())
                .sum();

        double totalExpected = billingItemList.stream()
                .mapToDouble(item -> item.getTotalExpectedAmount().doubleValue())
                .sum();

        double totalPending = totalExpected - totalCollected;

        double collectionRate = 0;
        if (totalExpected > 0) {
            collectionRate = (totalCollected / totalExpected) * 100;
        }
        totalCountLabel.setText(totalHouseholds + " hộ");
        collectedAmountLabel.setText(currencyFormat.format(totalCollected));
        pendingAmountLabel.setText(currencyFormat.format(totalPending));
        collectionRateLabel.setText(String.format("%.0f%%", collectionRate));
    }

    void loadChar(List<BillingItemDao.BillingSummary> billingItemList){
        statTypeComboBox.setItems(FXCollections.observableArrayList("Tất cả", "Đã thu", "Chưa thu"));
        statTypeComboBox.getSelectionModel().selectFirst(); // chọn sẵn "Tất cả"
        List<String> apartmentCodes = billingItemList.stream()
                .map(item -> item.getHouseholdId())
                .collect(Collectors.toList());
        objectComboBox.setItems(FXCollections.observableArrayList(apartmentCodes));
        objectComboBox.getSelectionModel().selectFirst();

        filterButton.setOnAction(event -> applyFilterAndUpdateChart(billingItemList));
    }

    private void applyFilterAndUpdateChart(List<BillingItemDao.BillingSummary> billingItemList) {
        String selectedType = statTypeComboBox.getValue();
        String selectedHousehold = objectComboBox.getValue();

        List<BillingItemDao.BillingSummary> filtered = billingItemList.stream()
                .filter(b -> {
                    boolean matchesType = switch (selectedType) {
                        case "Đã thu" -> b.getTotalActualAmount() > 0;
                        case "Chưa thu" -> b.getTotalExpectedAmount()-b.getTotalActualAmount() > 0;
                        default -> true;
                    };

                    boolean matchesHousehold = selectedHousehold == null || selectedHousehold.isEmpty()
                            || b.getHouseholdId().equals(selectedHousehold);


                    return matchesType && matchesHousehold;
                })
                .collect(Collectors.toList());

        updateBarChart(filtered);
    
    }

    private void updateBarChart(List<BillingItemDao.BillingSummary> data) {
        householdBarChart.getData().clear();

        String selectedType = statTypeComboBox.getValue();

        XYChart.Series<String, Number> collectedSeries = new XYChart.Series<>();
        collectedSeries.setName("Đã thu");

        XYChart.Series<String, Number> uncollectedSeries = new XYChart.Series<>();
        uncollectedSeries.setName("Chưa thu");

        for (BillingItemDao.BillingSummary summary : data) {
            String household = summary.getHouseholdId();
            Double actual = summary.getTotalActualAmount();
            Double expected = summary.getTotalExpectedAmount();
            Double unpaid = expected-actual;

            switch (selectedType) {
                case "Tất cả" -> {
                    collectedSeries.getData().add(new XYChart.Data<>(household, actual.doubleValue()));
                    uncollectedSeries.getData().add(new XYChart.Data<>(household, unpaid.doubleValue()));
                }
                case "Đã thu" -> {
                    collectedSeries.getData().add(new XYChart.Data<>(household, actual.doubleValue()));
                }
                case "Chưa thu" -> {
                    uncollectedSeries.getData().add(new XYChart.Data<>(household, unpaid.doubleValue()));
                }
            }
        }

        if (selectedType.equals("Tất cả") || selectedType.equals("Đã thu")) {
            householdBarChart.getData().add(collectedSeries);
        }

        if (selectedType.equals("Tất cả") || selectedType.equals("Chưa thu")) {
            householdBarChart.getData().add(uncollectedSeries);
        }

        Platform.runLater(() -> {
            for (XYChart.Series<String, Number> series : householdBarChart.getData()) {
                for (XYChart.Data<String, Number> dataPoint : series.getData()) {
                    Node node = dataPoint.getNode();
                    if (node != null) {
                        String seriesName = series.getName();
                        if (seriesName.equals("Đã thu")) {
                            node.setStyle("-fx-bar-fill: green; -fx-padding: 0 2px;"); // padding nhỏ
                        } else if (seriesName.equals("Chưa thu")) {
                            node.setStyle("-fx-bar-fill: red; -fx-padding: 0 2px;");
                        }
                        node.setScaleX(0.5); // làm cột hẹp lại
                    }
                }
            }
        });
    }

    void loadTopList(List<BillingItemDao.BillingSummary> billingItemList){
        List<BillingItemDao.BillingSummary> sorted = billingItemList.stream()
                .sorted(Comparator.comparing(BillingItemDao.BillingSummary::getDebtAmount).reversed())
                .limit(3)
                .collect(Collectors.toList()
                );

        if (sorted.size() > 0) {
            BillingItemDao.BillingSummary debtor1 = sorted.get(0);
            topDebtor1CodeLabel.setText(debtor1.getHouseholdId()); // hoặc debtor1.getHouseholdName()
            topDebtor1AmountLabel.setText(currencyFormat.format(debtor1.getTotalExpectedAmount()-debtor1.getTotalActualAmount()));
        }

        if (sorted.size() > 1) {
            BillingItemDao.BillingSummary debtor2 = sorted.get(1);
            topDebtor2CodeLabel.setText(debtor2.getHouseholdId());
            topDebtor2AmountLabel.setText(currencyFormat.format(debtor2.getTotalExpectedAmount()-debtor2.getTotalActualAmount()));
        }

        if (sorted.size() > 2) {
            BillingItemDao.BillingSummary debtor3 = sorted.get(2);
            topDebtor3CodeLabel.setText(debtor3.getHouseholdId());
            topDebtor3AmountLabel.setText(currencyFormat.format(debtor3.getTotalExpectedAmount()-debtor3.getTotalActualAmount()));
        }
    }

    void loadTable(List<BillingItemDao.BillingSummary> billingItemList){
        detailsTableView.setItems(FXCollections.observableArrayList(billingItemList));
        householdIdColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getHouseholdId()));
        householdNameColumn.setCellValueFactory(cellData->new SimpleStringProperty(cellData.getValue().getHouseholdName()));
        amountCollectedColumn.setCellValueFactory(cellData->new SimpleStringProperty(formatCurrency(cellData.getValue().getTotalActualAmount())));
        amountDueColumn.setCellValueFactory(cellData->new SimpleStringProperty(formatCurrency(cellData.getValue().getTotalExpectedAmount()-cellData.getValue().getTotalActualAmount())));
    }

    private String formatCurrency(Double amount) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormatter.format(amount);
    }

    private void handleExportReport(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu báo cáo");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        File file = fileChooser.showSaveDialog(statTypeComboBox.getScene().getWindow());

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Báo cáo");

                // Header
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("Mã hộ khẩu");
                header.createCell(1).setCellValue("Tên hộ");
                header.createCell(2).setCellValue("Đã thu");
                header.createCell(3).setCellValue("Còn thiếu");

                // Ghi dữ liệu
                int rowIndex = 1;
                for (BillingItemDao.BillingSummary item : detailsTableView.getItems()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(item.getHouseholdId());
                    row.createCell(1).setCellValue(item.getHouseholdName());
                    row.createCell(2).setCellValue(item.getTotalActualAmount());
                    row.createCell(3).setCellValue(item.getTotalExpectedAmount() - item.getTotalActualAmount());
                }

                // Auto-size
                for (int i = 0; i < 4; i++) {
                    sheet.autoSizeColumn(i);
                }

                // Ghi ra file
                try (FileOutputStream out = new FileOutputStream(file)) {
                    workbook.write(out);
                }

                showAlert("Thành công", "Xuất báo cáo thành công!", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Lỗi", "Không thể xuất báo cáo: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
