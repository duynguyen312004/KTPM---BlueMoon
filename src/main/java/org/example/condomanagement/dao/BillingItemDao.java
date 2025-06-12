package org.example.condomanagement.dao;

import jakarta.persistence.EntityManager;
import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.BillingItem;
import org.example.condomanagement.model.CollectionBatch;
import org.example.condomanagement.model.Fee;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

@SuppressWarnings("unused")
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

    public BillingItem save(BillingItem item) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Dùng merge để xử lý các entity detached
            BillingItem mergedItem = (BillingItem) session.merge(item);

            transaction.commit();
            return mergedItem;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace(); // rollback cũng có thể ném lỗi nếu session đã bị đóng
                }
            }
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

    public void update(BillingItem item) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(item); // dùng merge thay vì update
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    public static class BillingSummary {
        private String householdId;
        private String householdName;
        private Double totalExpectedAmount;
        private Double totalActualAmount;

        public BillingSummary(String householdId, String householdName, Double totalExpectedAmount,
                Double totalActualAmount) {
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

        public Double getDebtAmount() {
            return this.totalExpectedAmount - this.totalActualAmount;
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
