package org.example.condomanagement.service;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.dao.ResidentDao;
import org.example.condomanagement.dao.UserDao;
import org.example.condomanagement.model.Household;
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
        return dao.findAll();
    }
    public Resident findById(Integer id) {
        return dao.findById(id);
    }

    /** Tạo mới hoặc cập nhật nhân khẩu */
    public boolean saveOrUpdate(Resident r) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

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

            // Tìm các household mà resident này là chủ hộ
            List<Household> households = session.createQuery(
                            "FROM Household h WHERE h.headResidentId = :rid", Household.class)
                    .setParameter("rid", residentId)
                    .list();
            for (Household h : households) {
                h.setHeadResidentId(null);
                session.merge(h);
            }
            session.flush();

            // Giờ mới xóa Resident
            Resident r = session.get(Resident.class, residentId);
            if (r != null) session.delete(r);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            return false;
        }
    }
    public int countAll() {
        return dao.countAll();
    }
}
