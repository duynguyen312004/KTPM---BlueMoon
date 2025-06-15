package org.example.condomanagement.service;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Fee;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class FeeService {

    public List<Fee> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Fee", Fee.class).list();
        }
    }

    public Fee findById(Integer id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Fee.class, id);
        }
    }

    public void saveOrUpdate(Fee fee) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            if (fee.getFeeId() == null) {
                session.persist(fee);
            } else {
                session.merge(fee);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public void delete(Fee fee) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.remove(session.contains(fee) ? fee : session.merge(fee));
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public boolean existsByName(String feeName) {
        return existsByName(feeName, null);
    }

    public boolean existsByName(String feeName, Integer excludeId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT COUNT(f) FROM Fee f WHERE f.feeName = :name";
            if (excludeId != null) {
                hql += " AND f.feeId <> :id";
            }
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("name", feeName);
            if (excludeId != null)
                query.setParameter("id", excludeId);

            Long count = query.uniqueResult();
            return count != null && count > 0;
        }
    }
}
