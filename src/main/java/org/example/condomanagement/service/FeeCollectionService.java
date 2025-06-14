package org.example.condomanagement.service;

import org.example.condomanagement.model.FeeCollectionRow;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.example.condomanagement.config.HibernateUtil;

import java.util.ArrayList;
import java.util.List;

public class FeeCollectionService {
    public List<FeeCollectionRow> getAllFeeCollections() {
        List<FeeCollectionRow> result = new ArrayList<>();
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT bi.billingItemId, bi.household.apartmentCode, bi.fee.feeName, bi.batch.name, " +
                         "bi.expectedAmount, bi.actualAmount, bi.createdAt, bi.status " +
                         "FROM BillingItem bi";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            for (Object[] row : query.list()) {
                Integer billingItemId = (Integer) row[0];
                String householdCode = (String) row[1];
                String feeName = (String) row[2];
                String batchName = (String) row[3];
                Double expectedAmount = (Double) row[4];
                Double actualAmount = (Double) row[5];
                String date = row[6] != null ? row[6].toString() : "";
                String status = row[7] != null ? row[7].toString() : "Pending"; // Default to "Pending" if null

                result.add(new FeeCollectionRow(billingItemId, householdCode, feeName, batchName, expectedAmount, actualAmount, date, status));
            }
        }
        return result;
    }
}
