package org.example.condomanagement.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.model.Resident;
import org.example.condomanagement.service.HouseholdService;
import org.example.condomanagement.service.ResidentService;
import org.example.condomanagement.controller.CreateHouseholdDialogController;
import org.example.condomanagement.model.VehicleType;
import java.util.stream.Collectors;
import org.hibernate.Session;
import org.example.condomanagement.util.HibernateUtil;
import java.util.List;

@SuppressWarnings("unused")
public class HouseholdListController {

    @FXML
    private ComboBox<String> cbFilter;
    @FXML
    private TextField txtSearch;
    @FXML
    private Button btnSearch, btnAdd, btnEdit, btnDelete;

    @FXML
    private TableView<Household> tableHouseholds;
    @FXML
    private TableColumn<Household, String> colCode, colHead;
    @FXML
    private TableColumn<Household, Integer> colMemberCount;
    @FXML
    private TableColumn<Household, Double> colArea;
    @FXML
    private TableColumn<Household, String> colAddress;
    @FXML
    private TableColumn<Household, Integer> colMotorbikeCount;
    @FXML
    private TableColumn<Household, Integer> colCarCount;

    private final HouseholdService service = new HouseholdService();
    private final ResidentService residentSvc = new ResidentService();
    private final ObservableList<Household> masterData = FXCollections.observableArrayList();

    @SuppressWarnings("deprecation")
    @FXML
    public void initialize() {
        // 1) Filter dropdown
        cbFilter.getItems().setAll("Mã hộ khẩu", "Địa chỉ");
        cbFilter.getSelectionModel().selectFirst();

        // 2) Column binding
        colCode.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getApartmentCode()));

        colHead.setCellValueFactory(c -> {
            Integer headId = c.getValue().getHeadResidentId();
            if (headId != null) {
                Resident r = residentSvc.findById(headId);
                if (r != null)
                    return new ReadOnlyStringWrapper(r.getName());
            }
            return new ReadOnlyStringWrapper("");
        });

        // Số thành viên = size của collection residents (JOIN-FETCH trong DAO)
        colMemberCount.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getResidents().size()));

        colArea.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getArea()));

        colAddress.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getAddress()));

        // Thêm binding cho số lượng phương tiện
        colMotorbikeCount.setCellValueFactory(c -> {
            Household h = c.getValue();
            int count = h.getVehicles().stream()
                    .filter(v -> VehicleType.MOTORBIKE.equals(v.getType()))
                    .collect(Collectors.toList())
                    .size();
            return new ReadOnlyObjectWrapper<>(count);
        });

        colCarCount.setCellValueFactory(c -> {
            Household h = c.getValue();
            int count = h.getVehicles().stream()
                    .filter(v -> VehicleType.CAR.equals(v.getType()))
                    .collect(Collectors.toList())
                    .size();
            return new ReadOnlyObjectWrapper<>(count);
        });

        tableHouseholds.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        // 3) Enable/Disable nút Sửa & Xóa
        BooleanBinding noSelection = tableHouseholds.getSelectionModel().selectedItemProperty().isNull();
        btnEdit.disableProperty().bind(noSelection);
        btnDelete.disableProperty().bind(noSelection);

        // 4) Load dữ liệu
        loadHouseholds();
    }

    @FXML
    void onSearch() {
        String key = txtSearch.getText().trim().toLowerCase();
        if (key.isEmpty()) {
            tableHouseholds.setItems(masterData);
            return;
        }
        String by = cbFilter.getSelectionModel().getSelectedItem();
        ObservableList<Household> filtered = masterData.filtered(h -> {
            if ("Mã hộ khẩu".equals(by)) {
                return h.getApartmentCode().toLowerCase().contains(key);
            } else {
                return h.getAddress().toLowerCase().contains(key);
            }
        });
        tableHouseholds.setItems(filtered);
    }

    @FXML
    void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_household_dialog.fxml"));
            Parent root = loader.load();
            CreateHouseholdDialogController controller = loader.getController();
            // Không truyền household là ADD
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Thêm hộ khẩu");
            stage.showAndWait();
            // Sau khi đóng form, reload
            loadHouseholds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEdit() {
        Household sel = tableHouseholds.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_household_dialog.fxml"));
            Parent root = loader.load();
            CreateHouseholdDialogController controller = loader.getController();
            controller.setHousehold(sel); // Truyền household vào để edit
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sửa hộ khẩu");
            stage.showAndWait();
            loadHouseholds();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onDelete() {
        Household sel = tableHouseholds.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;
        Alert cf = new Alert(Alert.AlertType.CONFIRMATION,
                "Bạn có chắc muốn xóa hộ khẩu " + sel.getApartmentCode() + "?",
                ButtonType.YES, ButtonType.NO);
        cf.showAndWait()
                .filter(b -> b == ButtonType.YES)
                .ifPresent(b -> {
                    if (service.deleteHousehold(sel.getHouseholdId())) {
                        masterData.remove(sel);
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Xóa thất bại!").show();
                    }
                });
    }

    private void loadHouseholds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            
            // Chỉ fetch join residents
            String hql = "SELECT DISTINCT h FROM Household h LEFT JOIN FETCH h.residents";
            List<Household> households = session.createQuery(hql, Household.class).getResultList();

            // Nạp vehicles cho từng household khi session còn mở
            for (Household h : households) {
                h.getVehicles().size();
            }

            masterData.setAll(FXCollections.observableArrayList(households));
            tableHouseholds.setItems(masterData);
            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể tải danh sách hộ khẩu: " + e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
