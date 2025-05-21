package org.example.condomanagement.service;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.dao.ResidentDao;
import org.example.condomanagement.dao.UserDao;
import org.example.condomanagement.model.Resident;
import org.example.condomanagement.model.User;
import org.hibernate.Transaction;


import org.hibernate.Session;

import java.util.List;

public class ResidentService {
    private final ResidentDao dao = new ResidentDao();
    private final UserDao userDao     = new UserDao();


    /** Lấy tất cả nhân khẩu, JOIN-fetch household để tránh Lazy lỗi */
    public List<Resident> findAllWithAssociations() {
        return dao.findAllWithAssociations();
    }
    public Resident findById(Integer id) {
        return dao.findById(id);
    }

    /** Tạo mới hoặc cập nhật nhân khẩu */
    /**
     * Tạo mới hoặc cập nhật một nhân khẩu và luôn tạo hoặc cập nhật User kèm theo.
     * Nếu r.getUser() != null và u.getUserId() == null thì sẽ tạo User mới.
     * @return true nếu thành công
     */
    public boolean saveOrUpdate(Resident r) {
        // Dùng chung session để vừa save User vừa save Resident
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // 1) Lưu hoặc update User trước
            User u = r.getUser();
            if (u != null) {
                if (u.getUserId() == null) {
                    if (u.getFullName() == null) u.setFullName(r.getName());
                    if (u.getUsername() == null) u.setUsername("auto_" + System.currentTimeMillis());
                    if (u.getPassword() == null) u.setPassword("123456");
                    if (u.getRole() == null) u.setRole(User.Role.Resident);
                    session.persist(u);
                } else {
                    session.merge(u);
                }
                // gán lại vào resident (nếu cần)
                r.setUser(u);
            }

            // 2) Lưu hoặc update Resident
            if (r.getResidentId() == null) {
                session.persist(r);
            } else {
                session.merge(r);
            }

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            throw e;
        }
        }

    public boolean deleteResident(Integer residentId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            Resident resident = session.get(Resident.class, residentId);
            if (resident != null) {
                User user = resident.getUser();
                session.delete(resident);  // xóa resident

                // Nếu muốn xóa luôn user, kiểm tra không có resident khác dùng user này:
                if (user != null) {
                    // Nếu chắc chắn mỗi user chỉ có 1 resident, cứ xóa
                    session.delete(user);
                }
                tx.commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    public int countAll() {
        return dao.countAll();
    }
}
