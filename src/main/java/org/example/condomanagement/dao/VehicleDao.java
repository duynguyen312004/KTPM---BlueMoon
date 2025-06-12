package org.example.condomanagement.dao;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.model.Vehicle;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class VehicleDao {
    public Vehicle findById(Integer id) {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.get(Vehicle.class, id);
        }
    }

    public List<Vehicle> findAll() {
        try (Session s = HibernateUtil.getSessionFactory().openSession()) {
            return s.createQuery("FROM Vehicle", Vehicle.class).list();
        }
    }

    public int countAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Long result = session.createQuery("SELECT COUNT(v) FROM Vehicle v", Long.class).uniqueResult();
            return result != null ? result.intValue() : 0;
        }
    }

    public void save(Vehicle hh) {
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

    public void delete(Vehicle hh) {
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

    public Long countMotorbikesByHouseholdId(int householdId) {
        Transaction transaction = null;
        Long count = 0L;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            count = session.createQuery(
                            "SELECT COUNT(v) FROM Vehicle v WHERE v.household.id = :householdId AND v.type = 'MOTORBIKE'",
                            Long.class
                    ).setParameter("householdId", householdId)
                    .uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return count;
    }

    public Long countCarsByHouseholdId(int householdId) {
        Transaction transaction = null;
        Long count = 0L;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            count = session.createQuery(
                            "SELECT COUNT(v) FROM Vehicle v WHERE v.household.id = :householdId AND v.type = 'CAR'",
                            Long.class
                    ).setParameter("householdId", householdId)
                    .uniqueResult();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }

        return count;
    }


}
