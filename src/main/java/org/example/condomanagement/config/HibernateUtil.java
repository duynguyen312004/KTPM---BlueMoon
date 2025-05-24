package org.example.condomanagement.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.example.condomanagement.util.Constants;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration cfg = new Configuration();
            cfg.configure(); // đọc src/main/resources/hibernate.cfg.xml

            // Ghi đè URL/credentials nếu muốn dùng Constants
            cfg.setProperty("hibernate.connection.url", Constants.DB_URL);
            cfg.setProperty("hibernate.connection.username", Constants.DB_USER);
            cfg.setProperty("hibernate.connection.password", Constants.DB_PASS);

            return cfg.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}