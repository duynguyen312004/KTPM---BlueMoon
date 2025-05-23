package org.example.condomanagement.dao;

import jakarta.persistence.EntityManager;
import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.BillingItem;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class BillingItemDao {
    public BillingItem findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(BillingItem.class, id);
        }
    }

    public List<BillingItem> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM BillingItem", BillingItem.class).list();
        }
    }

    public void save(BillingItem hh) {
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

    public void delete(BillingItem hh) {
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

    public static class BillingSummary {
        private String householdId;
        private String householdName;
        private Double totalExpectedAmount;
        private Double totalActualAmount;

        public BillingSummary(String householdId,String householdName, Double totalExpectedAmount, Double totalActualAmount) {
            this.householdId = householdId;
            this.householdName = householdName;
            this.totalExpectedAmount = totalExpectedAmount;
            this.totalActualAmount = totalActualAmount;
        }

        public String getHouseholdId() {
            return householdId;
        }

        public void setHouseholdId(String householdId) {
            this.householdId = householdId;
        }

        public String getHouseholdName() {
            return householdName;
        }

        public void setHouseholdName(String householdName) {
            this.householdName = householdName;
        }

        public Double getTotalExpectedAmount() {
            return totalExpectedAmount;
        }

        public void setTotalExpectedAmount(Double totalExpectedAmount) {
            this.totalExpectedAmount = totalExpectedAmount;
        }

        public Double getTotalActualAmount() {
            return totalActualAmount;
        }

        public void setTotalActualAmount(Double totalActualAmount) {
            this.totalActualAmount = totalActualAmount;
        }
    }

    public List<BillingSummary> getBillingSummaryByHousehold() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT new org.example.condomanagement.dao.BillingItemDao$BillingSummary(" +
                    "b.household.apartmentCode, b.household.address, SUM(b.expectedAmount), SUM(b.actualAmount)) " +
                    "FROM BillingItem b " +
                    "GROUP BY b.household.apartmentCode, b.household.address";

            return session.createQuery(hql, BillingSummary.class).getResultList();
        }
    }

}
