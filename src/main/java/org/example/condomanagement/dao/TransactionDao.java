package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Transaction;
import org.example.condomanagement.model.TransactionView;

import org.hibernate.Session;
// import jakarta.persistence.Query; // Remove this line
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class TransactionDao {
    public List<String> getDistinctApartmentCodes() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT bi.household.apartmentCode FROM BillingItem bi", String.class).getResultList();
        }
    }

    public List<String> getDistinctReceiptNumbers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(
                    "SELECT DISTINCT r.receiptNumber FROM Receipt r", String.class).getResultList();
        }
    }

    public List<TransactionView> getTransactionViews(LocalDate from, LocalDate to,
            String apartmentCode, String receiptNumber) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            StringBuilder hql = new StringBuilder("""
                        SELECT new org.example.condomanagement.model.TransactionView(
                            r.receiptNumber,
                            t.paymentDate,
                            h.apartmentCode,
                            CONCAT(f.feeName, ' - ', cb.name),
                            t.amountPaid,
                            u.fullName
                        )
                        FROM Transaction t
                        JOIN t.billingItem bi
                        JOIN bi.household h
                        JOIN bi.fee f
                        JOIN bi.batch cb
                        JOIN t.createdBy u
                        JOIN Receipt r ON r.transaction = t
                        WHERE 1=1
                    """);

            if (from != null)
                hql.append(" AND t.paymentDate >= :from");
            if (to != null)
                hql.append(" AND t.paymentDate <= :to");
            if (apartmentCode != null && !apartmentCode.isEmpty())
                hql.append(" AND h.apartmentCode = :apartmentCode");
            if (receiptNumber != null && !receiptNumber.isEmpty())
                hql.append(" AND r.receiptNumber = :receiptNumber");

            hql.append(" ORDER BY t.paymentDate DESC");

            Query<TransactionView> query = session.createQuery(hql.toString(), TransactionView.class);

            if (from != null)
                query.setParameter("from", from);
            if (to != null)
                query.setParameter("to", to);
            if (apartmentCode != null && !apartmentCode.isEmpty())
                query.setParameter("apartmentCode", apartmentCode);
            if (receiptNumber != null && !receiptNumber.isEmpty())
                query.setParameter("receiptNumber", receiptNumber);

            return query.getResultList();
        }
    }

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