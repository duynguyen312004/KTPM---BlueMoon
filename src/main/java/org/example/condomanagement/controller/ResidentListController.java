package org.example.condomanagement.controller;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.condomanagement.model.Resident;
import org.example.condomanagement.model.User;
import org.example.condomanagement.service.ResidentService;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

public class ResidentListController {

    @FXML private ComboBox<String> cbFilter;
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch, btnAdd, btnEdit, btnDelete;
    @FXML private TableView<Resident> tableResidents;

    @FXML private TableColumn<Resident, String>  colResId, colName, colNationalId, colPhone, colHouseholdCode, colIsHead;
    @FXML private TableColumn<Resident, Integer> colAge;

    private final ResidentService service = new ResidentService();
    private final ObservableList<Resident> masterData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Filter dropdown
        cbFilter.getItems().setAll("Mã nhân khẩu", "Họ và tên", "CMND/CCCD", "Số điện thoại");
        cbFilter.getSelectionModel().selectFirst();

        // 2. Column binding
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
            System.out.println("DEBUG: resident=" + r.getValue().getResidentId()
                    + " user=" + u);
            String phone = (u != null ? u.getPhoneNumber() : "NULL");
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

        // 3. Enable/disable Edit & Delete
        BooleanBinding noSelection =
                tableResidents.getSelectionModel().selectedItemProperty().isNull();
        btnEdit.disableProperty().bind(noSelection);
        btnDelete.disableProperty().bind(noSelection);

        // 4. Resize policy cho table không dư khoảng trống
        tableResidents.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 5. Load data
        List<Resident> list = service.findAllWithAssociations();
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
        tableResidents.setItems(
                masterData.filtered(r -> {
                    switch (by) {
                        case "Họ và tên":      return r.getName().toLowerCase().contains(key);
                        case "CMND/CCCD":      return r.getNationalId().toLowerCase().contains(key);
                        case "Số điện thoại": return r.getUser()!=null &&
                                r.getUser().getPhoneNumber().contains(key);
                        default:               return r.getResidentId().toString().contains(key);
                    }
                })
        );
    }

    @FXML void onAdd()    { /* TODO: mở form thêm */ }
    @FXML void onEdit()   { /* TODO: mở form sửa*/ }
    @FXML void onDelete() { /* TODO: xóa*/ }
}
