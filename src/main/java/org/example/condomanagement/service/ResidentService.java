package org.example.condomanagement.service;

import org.example.condomanagement.dao.ResidentDao;
import org.example.condomanagement.model.Resident;

import java.util.List;

public class ResidentService {
    private final ResidentDao dao = new ResidentDao();

    /** Lấy tất cả nhân khẩu, JOIN-fetch household để tránh Lazy lỗi */
    public List<Resident> findAllWithAssociations() {
        return dao.findAllWithAssociations();
    }
    public Resident findById(Integer id) {
        return dao.findById(id);
    }

    /** Tạo mới hoặc cập nhật nhân khẩu */
    public boolean saveOrUpdate(Resident r) {
        try {
            dao.save(r);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Xóa nhân khẩu theo ID */
    public boolean deleteResident(Integer  residentId) {
        try {
            Resident r = dao.findById(residentId);
            if (r != null) {
                dao.delete(r);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public int countAll() {
        return dao.countAll();
    }
}
