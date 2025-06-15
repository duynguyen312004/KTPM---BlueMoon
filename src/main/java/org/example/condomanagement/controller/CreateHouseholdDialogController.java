package org.example.condomanagement.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Household;
import org.example.condomanagement.model.Resident;
import org.example.condomanagement.model.Vehicle;
import org.example.condomanagement.model.VehicleType;
import org.example.condomanagement.service.VehicleService;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class CreateHouseholdDialogController {
    @FXML
    private TextField txtApartmentCode;
    @FXML
    private TextField txtAddress;
    @FXML
    private TextField txtArea;
    @FXML
    private TextField txtHeadName;
    @FXML
    private DatePicker dpHeadBirthday;
    @FXML
    private TextField txtHeadNationalId;
    @FXML
    private TextField txtHeadPhone;
    @FXML
    private TextField txtMotorbikeCount;
    @FXML
    private TextArea txtMotorbikePlates;
    @FXML
    private TextField txtCarCount;
    @FXML
    private TextArea txtCarPlates;
    @FXML
    private Button btnSave, btnCancel;

    private Household household; // nếu != null là edit
    private VehicleService vehicleService = new VehicleService();
    private Stage dialogStage;

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    /**
     * Được gọi để truyền vào đối tượng edit (null = thêm mới).
     */
    public void setHousehold(Household hh) {
        this.household = hh;
        if (hh != null) {
            txtApartmentCode.setText(hh.getApartmentCode());
            txtAddress.setText(hh.getAddress());
            txtArea.setText(hh.getArea().toString());

            // Load thông tin chủ hộ
            if (hh.getHeadResidentId() != null) {
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

            // Load thông tin phương tiện
            List<Vehicle> vehicles = vehicleService.findByHouseholdId(hh.getHouseholdId());
            List<Vehicle> motorbikes = vehicles.stream()
                    .filter(v -> VehicleType.MOTORBIKE.equals(v.getType()))
                    .collect(Collectors.toList());
            List<Vehicle> cars = vehicles.stream()
                    .filter(v -> VehicleType.CAR.equals(v.getType()))
                    .collect(Collectors.toList());

            txtMotorbikeCount.setText(String.valueOf(motorbikes.size()));
            txtMotorbikePlates.setText(motorbikes.stream()
                    .map(Vehicle::getPlateNumber)
                    .collect(Collectors.joining("\n")));

            txtCarCount.setText(String.valueOf(cars.size()));
            txtCarPlates.setText(cars.stream()
                    .map(Vehicle::getPlateNumber)
                    .collect(Collectors.joining("\n")));
        }
    }

    @SuppressWarnings("deprecation")
    @FXML
    public void onSave() {
        // Validate input hộ khẩu
        String code = txtApartmentCode.getText().trim();
        // 🔥 THÊM MỚI: kiểm tra mã hộ khẩu đã tồn tại
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Household existing = session.createQuery(
                            "FROM Household h WHERE h.apartmentCode = :code", Household.class)
                    .setParameter("code", code)
                    .uniqueResult();

            if (existing != null && (household == null || !existing.getHouseholdId().equals(household.getHouseholdId()))) {
                new Alert(Alert.AlertType.ERROR, "Mã hộ khẩu đã tồn tại. Vui lòng nhập mã khác!").show();
                return;
            }
        }
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
        if (dpHeadBirthday.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Vui lòng chọn ngày sinh cho chủ hộ!").show();
            return;
        }

        // Validate số lượng và biển số xe
        try {
            int motorbikeCount = Integer.parseInt(txtMotorbikeCount.getText().trim());
            int carCount = Integer.parseInt(txtCarCount.getText().trim());

            if (motorbikeCount < 0 || carCount < 0) {
                new Alert(Alert.AlertType.ERROR, "Số lượng xe không được âm!").show();
                return;
            }

            String[] motorbikePlates = txtMotorbikePlates.getText().split("\\n");
            long motorbikePlateCount = java.util.Arrays.stream(motorbikePlates)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .count();

            String[] carPlates = txtCarPlates.getText().split("\\n");
            long carPlateCount = java.util.Arrays.stream(carPlates)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .count();

            if (motorbikePlateCount != motorbikeCount || carPlateCount != carCount) {
                new Alert(Alert.AlertType.ERROR,
                        "DEBUG: Số lượng xe máy: " + motorbikeCount + ", biển số xe máy: " + motorbikePlateCount +
                                "\nSố lượng ô tô: " + carCount + ", biển số ô tô: " + carPlateCount +
                                "\n\nSố lượng biển số xe không khớp với số lượng xe!")
                        .show();
                return;
            }
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Số lượng xe phải là số!").show();
            return;
        }

        // Bắt đầu Transaction
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Cập nhật thông tin hộ khẩu
            boolean isNew = (household == null);
            if (isNew) {
                household = new Household();
                household.setApartmentCode(code);
                household.setAddress(address);
                household.setArea(Double.parseDouble(areaStr));
                session.persist(household);
                session.flush();
            } else {
                household.setApartmentCode(code);
                household.setAddress(address);
                household.setArea(Double.parseDouble(areaStr));
                household = session.merge(household);
            }

            // Cập nhật thông tin chủ hộ
            Resident head;
            if (!isNew && household.getHeadResidentId() != null) {
                head = session.get(Resident.class, household.getHeadResidentId());
                if (head == null)
                    head = new Resident();
            } else {
                head = new Resident();
            }
            head.setName(txtHeadName.getText().trim());
            head.setBirthday(dpHeadBirthday.getValue());
            head.setNationalId(txtHeadNationalId.getText().trim());
            head.setPhoneNumber(txtHeadPhone.getText().trim());
            head.setRelationship("Chủ hộ");
            head.setHousehold(household);

            if (head.getResidentId() == null) {
                session.persist(head);
                session.flush();
            } else {
                head = session.merge(head);
            }

            // Cập nhật head_resident_id
            household.setHeadResidentId(head.getResidentId());
            session.merge(household);

            // Cập nhật thông tin phương tiện
            // Xóa các phương tiện cũ
            if (!isNew) {
                session.createQuery("DELETE FROM Vehicle v WHERE v.household = :household")
                        .setParameter("household", household)
                        .executeUpdate();
            }

            // Thêm xe máy mới
            String[] motorbikePlates = txtMotorbikePlates.getText().split("\\n");
            for (String plate : motorbikePlates) {
                if (!plate.trim().isEmpty()) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setHousehold(household);
                    vehicle.setType(VehicleType.MOTORBIKE);
                    vehicle.setPlateNumber(plate.trim());
                    session.persist(vehicle);
                }
            }

            // Thêm ô tô mới
            String[] carPlates = txtCarPlates.getText().split("\\n");
            for (String plate : carPlates) {
                if (!plate.trim().isEmpty()) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setHousehold(household);
                    vehicle.setType(VehicleType.CAR);
                    vehicle.setPlateNumber(plate.trim());
                    session.persist(vehicle);
                }
            }

            tx.commit();
            if (dialogStage != null) {
                dialogStage.close();
            } else {
                ((Stage) btnSave.getScene().getWindow()).close();
            }
        } catch (Exception e) {
            showConstraintViolationAlert(e);
        }
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
}
