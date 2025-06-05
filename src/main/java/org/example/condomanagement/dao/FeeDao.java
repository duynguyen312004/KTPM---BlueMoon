package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Fee;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class FeeDao {
    public Fee findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Fee.class, id);
        }
    }

    public List<Fee> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Fee", Fee.class).list();
        }
    }

    public List<String> findAllName() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<String> query = s.createQuery("SELECT t.feeName FROM Fee t", String.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Fee.FeeCategory> findAllCategory() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<Fee.FeeCategory> query = s.createQuery("SELECT t.feeCategory FROM Fee t", Fee.FeeCategory.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public List<Fee.CalculationMethod> findAllMethod() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            Query<Fee.CalculationMethod> query = s.createQuery(
                    "SELECT distinct t.calculationMethod distinct FROM Fee t", Fee.CalculationMethod.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public Fee save(Fee fee) {
        Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            Fee saved = (Fee) s.merge(fee); // merge trả về bản đã có id
            tx.commit();
            return saved;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public void update(Fee fee) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.merge(fee); // ⚠️ khác với merge(): chỉ hoạt động nếu entity đã tồn tại và được quản lý
            tx.commit();
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public void delete(Fee hh) {
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
}
