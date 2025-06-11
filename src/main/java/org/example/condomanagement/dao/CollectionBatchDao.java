package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.CollectionBatch;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class CollectionBatchDao {
    public CollectionBatch findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(CollectionBatch.class, id);
        }
    }

    public List<CollectionBatch> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM CollectionBatch", CollectionBatch.class).list();
        }
    }

    public void save(CollectionBatch collectionBatch) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(collectionBatch);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }


    public void delete(CollectionBatch hh) {
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

    public boolean existsByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long count = session.createQuery(
                            "SELECT COUNT(cb.id) FROM CollectionBatch cb WHERE cb.name = :name", Long.class)
                    .setParameter("name", name)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }
}
