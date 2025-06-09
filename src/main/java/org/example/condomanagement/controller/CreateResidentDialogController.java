package org.example.condomanagement.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.model.Resident;
import org.example.condomanagement.model.User;
import org.example.condomanagement.service.HouseholdService;
import org.example.condomanagement.service.ResidentService;
import org.example.condomanagement.service.UserService;

import java.time.LocalDate;
import java.util.List;

@SuppressWarnings("unused")
public class CreateResidentDialogController {

    @FXML
    private TextField txtName, txtNationalId, txtPhoneNumber;
    @FXML
    private DatePicker dpBirthday;
    @FXML
    private ComboBox<String> cbRelationship;
    @FXML
    private ComboBox<Household> cbHousehold;

    @FXML
    private Button btnSave, btnCancel;

    private Stage dialogStage;
    private ResidentService residentService;
    private HouseholdService householdService;

    private boolean saved = false;
    private Resident editingResident;

    /**
     * Init từ ListController:
     * dialogStage: cửa sổ Form,
     * resSvc : ResidentService,
     * hhSvc : HouseholdService,
     * 
     */
    public void init(Stage dialogStage,
            @SuppressWarnings("exports") ResidentService resSvc,
            @SuppressWarnings("exports") HouseholdService hhSvc,
            Resident resident // null = create, có resident = edit
    ) {
        this.dialogStage = dialogStage;
        this.residentService = resSvc;
        this.householdService = hhSvc;
        this.editingResident = resident;

        // load danh sách hộ khẩu vào combobox
        List<Household> list = householdService.findAll();
        cbHousehold.setItems(FXCollections.observableArrayList(list));

        // load options relationship
        cbRelationship.setItems(FXCollections.observableArrayList(
                "Chủ hộ", "Thành viên"));
        if (resident != null) {
            txtName.setText(resident.getName());
            txtNationalId.setText(resident.getNationalId());
            dpBirthday.setValue(resident.getBirthday());
            cbRelationship.setValue(resident.getRelationship());
            cbHousehold.setValue(resident.getHousehold());
            txtPhoneNumber.setText(resident.getPhoneNumber() != null ? resident.getPhoneNumber() : "");
        }
    }

    @FXML
    private void onSave() {
        // validate
        if (txtName.getText().isBlank()
                || txtNationalId.getText().isBlank()
                || dpBirthday.getValue() == null
                || cbHousehold.getValue() == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Vui lòng điền đầy đủ thông tin").show();
            return;
        }
        Resident r = (editingResident != null) ? editingResident : new Resident();

        r.setName(txtName.getText().trim());
        r.setBirthday(dpBirthday.getValue());
        r.setRelationship(cbRelationship.getValue());
        r.setNationalId(txtNationalId.getText().trim());
        r.setHousehold(cbHousehold.getValue());
        r.setPhoneNumber(txtPhoneNumber.getText().trim()); // <-- Lưu SĐT vào resident

        // 3. Lưu trong 1 transaction
        try {
            boolean ok = residentService.saveOrUpdate(r);
            if (ok) {
                saved = true;
                dialogStage.close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Lỗi không xác định khi lưu!").show();
            }
        } catch (Exception ex) {
            showConstraintViolationAlert(ex);
        }
    }
    /**
     * Hiển thị thông báo lỗi rõ ràng khi gặp constraint database.
     */
    private void showConstraintViolationAlert(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String msg = root.getMessage();

        if (msg.contains("duplicate key value") && msg.contains("national_id")) {
            showAlert(Alert.AlertType.ERROR, "CCCD/CMND đã tồn tại!");
        } else if (msg.contains("duplicate key value") && msg.contains("phone_number")) {
            showAlert(Alert.AlertType.ERROR, "Số điện thoại đã tồn tại!");
        } else if (msg.contains("check_national_id_length")) {
            showAlert(Alert.AlertType.ERROR, "CCCD/CMND phải có đúng 12 chữ số!");
        } else if (msg.contains("check_phone_number_format")) {
            showAlert(Alert.AlertType.ERROR, "Số điện thoại không hợp lệ!");
        } else if (msg.contains("check_birthday")) {
            showAlert(Alert.AlertType.ERROR, "Ngày sinh không hợp lệ (phải trước hoặc bằng ngày hôm nay)!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi khi lưu dữ liệu: " + msg);
        }
        ex.printStackTrace();
    }

    private void showAlert(Alert.AlertType type, String content) {
        new Alert(type, content).show();
    }

    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
