package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.condomanagement.service.HouseholdService;
import org.example.condomanagement.service.ResidentService;
import org.example.condomanagement.service.VehicleService;

import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private ImageView iconHouseholds;
    @FXML
    private ImageView iconResidents;
    @FXML
    private ImageView iconUnits;

    @FXML
    private Label lblTotalHouseholds;
    @FXML
    private Label lblTotalResidents;
    @FXML
    private Label lblTotalUnits;

    private final HouseholdService householdService = new HouseholdService();
    private final ResidentService residentService = new ResidentService();
    private final VehicleService vehicleService = new VehicleService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load icons
        iconHouseholds.setImage(loadImage("household.png"));
        iconResidents.setImage(loadImage("resident.png"));
        iconUnits.setImage(loadImage("vehicles.png"));

        // Gọi các hàm đếm và cập nhật UI
        updateDashboardCounts();
    }

    private void updateDashboardCounts() {
        int totalHouseholds = householdService.countAll();
        int totalResidents = residentService.countAll();
        int totalVehicles = vehicleService.countAll();

        lblTotalHouseholds.setText(String.valueOf(totalHouseholds));
        lblTotalResidents.setText(String.valueOf(totalResidents));
        lblTotalUnits.setText(String.valueOf(totalVehicles));
    }

    private Image loadImage(String fileName) {
        String path = "/assets/" + fileName;
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("❌ Asset not found: " + path);
            return null;
        }
        return new Image(is);
    }
}
