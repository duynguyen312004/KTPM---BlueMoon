package org.example.condomanagement.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.example.condomanagement.model.*;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            // Add annotated classes
            configuration.addAnnotatedClass(Household.class);
            configuration.addAnnotatedClass(Resident.class);
            configuration.addAnnotatedClass(Vehicle.class);
            configuration.addAnnotatedClass(Fee.class);
            configuration.addAnnotatedClass(VehicleFeeMapping.class);
            configuration.addAnnotatedClass(Payment.class);
            configuration.addAnnotatedClass(Notification.class);
            configuration.addAnnotatedClass(User.class);
            
            return configuration.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
} 