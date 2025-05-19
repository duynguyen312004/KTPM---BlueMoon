package org.example.condomanagement;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.example.condomanagement.config.HibernateUtil;

public class SchemaTest {
    public static void main(String[] args) {
        System.out.println("Khởi động Hibernate test...");
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        Session session = sessionFactory.openSession(); // ép Hibernate khởi tạo entity + schema
        session.close();
        System.out.println("✅ Hibernate đã chạy xong!");
    }
}
