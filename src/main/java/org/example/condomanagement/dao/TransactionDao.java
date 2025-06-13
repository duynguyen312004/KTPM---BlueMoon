package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Transaction;
import org.hibernate.Session;
import java.util.List;

public class TransactionDao {
    public Transaction findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Transaction.class, id);
        }
    }

    public List<Transaction> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Transaction", Transaction.class).list();
        }
    }

    public Transaction save(Transaction transaction) {
        org.hibernate.Transaction tx = null;
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            tx = s.beginTransaction();
            if (transaction.getTransactionId() == null) {
                s.persist(transaction);
                s.flush();
            } else {
                transaction = s.merge(transaction);
            }
            tx.commit();
            return transaction;
        } catch (Exception e) {
            if (tx != null)
                tx.rollback();
            throw e;
        }
    }

    public void delete(Transaction hh) {
        org.hibernate.Transaction tx = null;
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