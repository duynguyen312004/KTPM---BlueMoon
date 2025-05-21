package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Resident;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ResidentDao {
    public Resident findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Resident.class, id);
        }
    }

    public List<Resident> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Resident", Resident.class).list();
        }
    }
    public List<Resident> findAllWithAssociations() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            // JOIN FETCH household để tránh lazy load error khi truy cập household từ resident
            return s.createQuery("SELECT DISTINCT r FROM Resident r JOIN FETCH r.household h", Resident.class).list();
        }
    }
    public int countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long result = session.createQuery("SELECT COUNT(r) FROM Resident r", Long.class).uniqueResult();
            return result != null ? result.intValue() : 0;
        }
    }

    public void save(Resident r) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.merge(r);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public void delete(Resident r) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            s.remove(r);
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }


}
