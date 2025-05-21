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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.condomanagement.model.Resident;
import org.example.condomanagement.model.User;
import org.example.condomanagement.service.HouseholdService;
import org.example.condomanagement.service.ResidentService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ResidentListController {

    @FXML private ComboBox<String> cbFilter;
    @FXML private TextField      txtSearch;
    @FXML private Button         btnAdd, btnEdit, btnDelete;
    @FXML private TableView<Resident> tableResidents;

    @FXML private TableColumn<Resident, String> colResId, colName, colNationalId, colPhone, colHouseholdCode, colIsHead;
    @FXML private TableColumn<Resident, Integer> colAge;


    private final ResidentService  residentService  = new ResidentService();
    private final HouseholdService householdService = new HouseholdService();

    private final ObservableList<Resident> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1) Thiết lập filter dropdown
        cbFilter.getItems().setAll("Mã nhân khẩu", "Họ và tên", "CMND/CCCD", "Số điện thoại");
        cbFilter.getSelectionModel().selectFirst();

        // 2) Binding các cột
        colResId.setCellValueFactory(r ->
                new ReadOnlyStringWrapper(r.getValue().getResidentId().toString())
        );
        colName.setCellValueFactory(r ->
                new ReadOnlyStringWrapper(r.getValue().getName())
        );
        colAge.setCellValueFactory(r -> {
            LocalDate bd = r.getValue().getBirthday();
            int age = bd != null ? Period.between(bd, LocalDate.now()).getYears() : 0;
            return new ReadOnlyObjectWrapper<>(age);
        });
        colNationalId.setCellValueFactory(r ->
                new ReadOnlyStringWrapper(r.getValue().getNationalId())
        );
        colPhone.setCellValueFactory(r -> {
            User u = r.getValue().getUser();
            String phone = (u != null ? u.getPhoneNumber() : "");
            return new ReadOnlyStringWrapper(phone);
        });
        colHouseholdCode.setCellValueFactory(r ->
                new ReadOnlyStringWrapper(r.getValue().getHousehold().getApartmentCode())
        );
        colIsHead.setCellValueFactory(r -> {
            boolean isHead = r.getValue().getResidentId()
                    .equals(r.getValue().getHousehold().getHeadResidentId());
            return new ReadOnlyStringWrapper(isHead ? "Có" : "");
        });

        // 3) Bật/tắt Edit & Delete
        BooleanBinding noSelection =
                tableResidents.getSelectionModel().selectedItemProperty().isNull();
        btnEdit.disableProperty().bind(noSelection);
        btnDelete.disableProperty().bind(noSelection);

        // 4) Không để dư ô trống bên phải
        tableResidents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 5) Load dữ liệu
        List<Resident> list = residentService.findAllWithAssociations();
        masterData.setAll(list);
        tableResidents.setItems(masterData);
    }

    @FXML
    void onSearch() {
        String key = txtSearch.getText().trim().toLowerCase();
        if (key.isEmpty()) {
            tableResidents.setItems(masterData);
            return;
        }
        String by = cbFilter.getSelectionModel().getSelectedItem();
        ObservableList<Resident> filtered = masterData.filtered(r -> {
            switch (by) {
                case "Họ và tên":
                    return r.getName().toLowerCase().contains(key);
                case "CMND/CCCD":
                    return r.getNationalId().toLowerCase().contains(key);
                case "Số điện thoại":
                    return r.getUser() != null &&
                            r.getUser().getPhoneNumber().contains(key);
                default:
                    return r.getResidentId().toString().contains(key);
            }
        });
        tableResidents.setItems(filtered);
    }

    @FXML
    void onAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/create_resident_dialog.fxml")
            );
            Parent root = loader.load();

            CreateResidentDialogController formCtrl = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Thêm Nhân Khẩu");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));

            formCtrl.init(dialog, residentService, householdService, null);
            dialog.showAndWait();

            if (formCtrl.isSaved()) {
                masterData.setAll(residentService.findAllWithAssociations());
                tableResidents.setItems(masterData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Không mở được form thêm!").show();
        }
    }



    @FXML
    private void onEdit() {
        Resident selected = tableResidents.getSelectionModel().getSelectedItem();
        if (selected == null) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng chọn nhân khẩu để sửa!").show();
            return;
        }

        // Tạo dialog
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/create_resident_dialog.fxml"));
            Parent root = loader.load();

            // Lấy controller của dialog
            CreateResidentDialogController controller = loader.getController();

            // Tạo stage cho dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Sửa nhân khẩu");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            // Khởi tạo dialog, truyền Resident cần edit
            controller.init(dialogStage, residentService, householdService, selected);

            dialogStage.showAndWait();

            // Nếu lưu thành công thì reload table
            if (controller.isSaved()) {
                masterData.setAll(residentService.findAllWithAssociations());
                tableResidents.setItems(masterData);
            }

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi mở form sửa: " + e.getMessage()).show();
        }
    }

    @FXML
    void onDelete() {
        Resident sel = tableResidents.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Alert cf = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa nhân khẩu " + sel.getResidentId() + "?", ButtonType.YES, ButtonType.NO);
        cf.showAndWait().filter(b -> b == ButtonType.YES).ifPresent(b -> {
            if (residentService.deleteResident(sel.getResidentId())) {
                masterData.remove(sel);
            } else {
                new Alert(Alert.AlertType.ERROR, "Xóa thất bại!").show();
            }
        });
    }
}
