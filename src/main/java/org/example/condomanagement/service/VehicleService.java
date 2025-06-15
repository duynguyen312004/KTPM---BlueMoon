package org.example.condomanagement.service;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class VehicleService {

    public List<Vehicle> findByHouseholdId(Integer householdId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Vehicle> query = session.createQuery(
                    "FROM Vehicle v WHERE v.household.householdId = :householdId",
                    Vehicle.class);
            query.setParameter("householdId", householdId);
            return query.list();
        }
    }

    public void save(Vehicle vehicle) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.persist(vehicle);
            session.getTransaction().commit();
        }
    }

    @SuppressWarnings({ "rawtypes", "deprecation" })
    public void deleteByHouseholdId(Integer householdId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            Query query = session.createQuery(
                    "DELETE FROM Vehicle v WHERE v.household.householdId = :householdId");
            query.setParameter("householdId", householdId);
            query.executeUpdate();
            session.getTransaction().commit();
        }
    }

    public int countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(v) FROM Vehicle v",
                    Long.class);
            return query.uniqueResult().intValue();
        }
    }
}
