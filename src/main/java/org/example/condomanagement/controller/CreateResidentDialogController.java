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

import java.text.Normalizer;
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
        String name = cleanInput(txtName.getText());
        String nationalId = cleanInput(txtNationalId.getText());
        String phoneNumber = cleanInput(txtPhoneNumber.getText());
        String relationship = cbRelationship.getValue();
        Resident r = (editingResident != null) ? editingResident : new Resident();

        r.setName(txtName.getText().trim());
        r.setBirthday(dpBirthday.getValue());
        r.setRelationship(cbRelationship.getValue());
        r.setNationalId(txtNationalId.getText().trim());
        r.setHousehold(cbHousehold.getValue());
        r.setPhoneNumber(txtPhoneNumber.getText().trim()); // <-- Lưu SĐT vào resident

        // ✅ Kiểm tra: nếu chỉ có 1 resident và chọn "Thành viên" thì báo lỗi
        Household household = r.getHousehold();
        if ("Thành viên".equals(relationship) && household.getResidents().size() == 1) {
            new Alert(Alert.AlertType.ERROR,
                    "Hộ khẩu phải có ít nhất 1 Chủ hộ!").show();
            return;
        }

        // 3. Lưu trong 1 transaction
        try {
            boolean ok = residentService.saveOrUpdate(r);
            // Nếu là chủ hộ, tự động chuyển các resident khác trong household thành "Thành viên"
            if ("Chủ hộ".equals(relationship)) {


                // 1. Lấy danh sách tất cả resident trong household
                List<Resident> residentsInHousehold = household.getResidents();

                // 2. Chuyển role của tất cả resident thành "Thành viên", trừ resident hiện tại
                for (Resident other : residentsInHousehold) {
                    if (!other.getResidentId().equals(r.getResidentId())) {
                        other.setRelationship("Thành viên");
                        residentService.saveOrUpdate(other); // Lưu lại resident khác
                    }
                }

                // 3. Cập nhật headResidentId của household
                household.setHeadResidentId(r.getResidentId());
                householdService.saveOrUpdate(household);
            }

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
    private String cleanInput(String input) {
        if (input == null) return null;
        // Chỉ giữ ký tự Unicode chuẩn (loại bỏ ký tự lỗi - dấu hỏi vuông vỡ font)
        String cleaned = Normalizer.normalize(input.trim(), Normalizer.Form.NFC);
        cleaned = cleaned.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", ""); // loại control
        return cleaned;
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
            showAlert(Alert.AlertType.ERROR, "Số điện thoại không hợp lệ!nhập đủ 10 số ");
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
