package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Household;
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

    public void save(Household hh) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.merge(hh);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public void delete(Household hh) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.remove(hh);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
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
