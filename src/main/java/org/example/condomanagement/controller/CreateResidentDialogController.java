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

public class CreateResidentDialogController {

    @FXML private TextField txtName, txtNationalId, txtPhoneNumber;
    @FXML private DatePicker dpBirthday;
    @FXML private ComboBox<String> cbRelationship;
    @FXML private ComboBox<Household> cbHousehold;

    @FXML private Button btnSave, btnCancel;

    private Stage dialogStage;
    private ResidentService residentService;
    private HouseholdService householdService;

    private boolean saved = false;
    private Resident editingResident;
    /**
     * Init từ ListController:
     *   dialogStage: cửa sổ Form,
     *   resSvc     : ResidentService,
     *   hhSvc      : HouseholdService,
     *   usrSvc     : UserService để lưu số điện thoại
     */
    public void init(Stage dialogStage,
                     ResidentService resSvc,
                     HouseholdService hhSvc,
                     Resident resident // null = create, có resident = edit
                     ) {
        this.dialogStage      = dialogStage;
        this.residentService  = resSvc;
        this.householdService = hhSvc;
        this.editingResident  = resident;

        // load danh sách hộ khẩu vào combobox
        List<Household> list = householdService.findAll();
        cbHousehold.setItems(FXCollections.observableArrayList(list));

        // load options relationship
        cbRelationship.setItems(FXCollections.observableArrayList(
                "Chủ hộ", "Thành viên"
        ));
        if (resident != null) {
            txtName.setText(resident.getName());
            txtNationalId.setText(resident.getNationalId());
            dpBirthday.setValue(resident.getBirthday());
            cbRelationship.setValue(resident.getRelationship());
            cbHousehold.setValue(resident.getHousehold());
            txtPhoneNumber.setText(
                    resident.getUser() != null ? resident.getUser().getPhoneNumber() : ""
            );
        }
    }


    @FXML
    private void onSave() {
        // validate
        if (txtName.getText().isBlank()
                || txtNationalId.getText().isBlank()
                || dpBirthday.getValue()==null
                || cbHousehold.getValue()==null) {
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
        // Xử lý user (1 Resident gắn 1 User)
        User u;
        if (r.getUser() != null) {
            u = r.getUser();
        } else {
            u = new User();
            u.setRole(User.Role.Resident);
            u.setIsActive(true);
        }
        u.setUsername(txtNationalId.getText().trim()); // dùng số CMT làm username, hoặc tự sinh
        u.setPassword("123456"); // hoặc random, hoặc để người dùng đổi sau
        u.setFullName(txtName.getText().trim());
        u.setPhoneNumber(txtPhoneNumber.getText().trim());

        r.setUser(u);
        // TODO: nếu cần username/password, set thêm ở đây




        // 3. Lưu trong 1 transaction
        boolean ok = residentService.saveOrUpdate(r);
        if (ok) {
            saved = true;
            dialogStage.close();
        } else {
            new Alert(Alert.AlertType.ERROR,
                    "Lỗi khi lưu dữ liệu!").show();
        }
    }


    @FXML
    private void onCancel() {
        dialogStage.close();
    }

    public boolean isSaved() {
        return saved;
    }
}
