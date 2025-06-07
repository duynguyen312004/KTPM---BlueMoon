package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.model.Resident;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;

public class CreateHouseholdDialogController {
    @FXML private TextField  txtApartmentCode;
    @FXML private TextField  txtAddress;
    @FXML private TextField  txtArea;
    @FXML private TextField  txtHeadName;
    @FXML private DatePicker dpHeadBirthday;
    @FXML private TextField  txtHeadNationalId;
    @FXML private TextField  txtHeadPhone;
    @FXML private Button     btnSave, btnCancel;

    private Household household; // nếu != null là edit

    /**
     * Được gọi để truyền vào đối tượng edit (null = thêm mới).
     */
    public void setHousehold(Household hh) {
        this.household = hh;
        if (hh != null) {
            txtApartmentCode.setText(hh.getApartmentCode());
            txtAddress.setText(hh.getAddress());
            txtArea.setText(hh.getArea().toString());
            // load thông tin chủ hộ nếu có
            if (hh.getHeadResidentId() != null) {
                // ta dùng một Session tạm để load head
                try (Session s = HibernateUtil.getSessionFactory().openSession()) {
                    Resident head = s.get(Resident.class, hh.getHeadResidentId());
                    if (head != null) {
                        txtHeadName.setText(head.getName());
                        dpHeadBirthday.setValue(head.getBirthday());
                        txtHeadNationalId.setText(head.getNationalId());
                        txtHeadPhone.setText(head.getPhoneNumber());
                    }
                }
            }
        }
    }

    @FXML
    public void onSave() {
        // 1) Validate input hộ khẩu
        String code    = txtApartmentCode.getText().trim();
        String address = txtAddress.getText().trim();
        String areaStr = txtArea.getText().trim();
        if (code.isEmpty() || address.isEmpty() || areaStr.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đủ thông tin hộ khẩu!").show();
            return;
        }
        double area;
        try {
            area = Double.parseDouble(areaStr);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Diện tích phải là số!").show();
            return;
        }
        // Bổ sung kiểm tra không âm:
        if (area < 0) {
            new Alert(Alert.AlertType.ERROR, "Diện tích không được âm!").show();
            return;
        }

        // 2) Validate input chủ hộ
        String headName = txtHeadName.getText().trim();
        LocalDate headBd= dpHeadBirthday.getValue();
        String headCid  = txtHeadNationalId.getText().trim();
        String headPhone= txtHeadPhone.getText().trim();
        if (headName.isEmpty() || headBd == null || headCid.isEmpty() || headPhone.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin chủ hộ!").show();
            return;
        }

        // 3) Bắt đầu Transaction chung
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // a) Tạo hoặc cập nhật Household
            boolean isNew = (household == null);
            if (isNew) {
                household = new Household();
                household.setApartmentCode(code);
                household.setAddress(address);
                household.setArea(area);
                session.persist(household);
                session.flush(); // để có ID
            } else {
                household.setApartmentCode(code);
                household.setAddress(address);
                household.setArea(area);
                household = session.merge(household);
            }

            // b) Tạo hoặc cập nhật Resident làm chủ hộ
            Resident head;
            if (!isNew && household.getHeadResidentId() != null) {
                head = session.get(Resident.class, household.getHeadResidentId());
                // nếu head bị xóa hoặc không tồn tại thì tạo mới
                if (head == null) head = new Resident();
            } else {
                head = new Resident();
            }
            head.setName(headName);
            head.setBirthday(headBd);
            head.setNationalId(headCid);
            head.setPhoneNumber(headPhone);
            head.setRelationship("Chủ hộ");
            head.setHousehold(household);

            if (head.getResidentId() == null) {
                session.persist(head);
                session.flush();
            } else {
                head = session.merge(head);
            }

            // c) Cập nhật lại head_resident_id trên Household
            household.setHeadResidentId(head.getResidentId());
            session.merge(household);

            tx.commit();
        } catch (Exception ex) {
            showConstraintViolationAlert(ex);
            return;
        }


        // 4) Đóng dialog
        ((Stage) btnSave.getScene().getWindow()).close();
    }

    @FXML
    public void onCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }
    private void showConstraintViolationAlert(Throwable ex) {
        Throwable root = ex;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String msg = root.getMessage();

        if (msg.contains("unique_national_id")) {
            showAlert(Alert.AlertType.ERROR, "CCCD/CMND đã tồn tại!");
        } else if (msg.contains("unique_phone_number")) {
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
}
