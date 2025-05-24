package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Household;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class HouseholdDao {
    public Household findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Household.class, id);
        }
    }

    public List<Household> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT h " +
                            "FROM Household h " +
                            "LEFT JOIN FETCH h.residents",  // đảm bảo residents được load luôn
                    Household.class
            ).list();
        }
    }

    public int countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long result = session.createQuery("SELECT COUNT(h) FROM Household h", Long.class).uniqueResult();
            return result != null ? result.intValue() : 0;
        }
    }

    public Household  save(Household hh) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            if (hh.getHouseholdId() == null) {
                // tạo mới: persist + flush để ID gán về object gốc
                s.persist(hh);
                s.flush();
            } else {
                // cập nhật: merge và gán trả về cho hh
                hh = s.merge(hh);
            }
            tx.commit();
            return hh;
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    public void delete(Household hh) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();

            // Xử lý: set head_resident_id về null trước khi xóa resident
            hh = s.merge(hh); // Đảm bảo entity đã managed

            hh.setHeadResidentId(null);  // Quan trọng! Giải phóng FK trước
            s.merge(hh);
            s.flush(); // Sync với DB

            s.remove(hh); // Lúc này Hibernate sẽ xóa household và toàn bộ residents nhờ cascade+orphanRemoval
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }

    /**
     * Đếm số hộ khẩu đã có chủ (head_resident_id != NULL)
     */
    public long countOccupiedHouseholds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long cnt = session.createQuery(
                    "SELECT COUNT(h) FROM Household h WHERE h.headResidentId IS NOT NULL",
                    Long.class
            ).getSingleResult();
            return cnt != null ? cnt : 0L;
        }
    }
}
