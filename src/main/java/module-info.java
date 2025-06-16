module CondoManagement {
    // JavaFX modules
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;

    // Hibernate & JPA
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires java.sql;

    // Optional UI framework
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires static lombok;

    // Cho phép JavaFX launcher và FXMLLoader khởi tạo Main & Controllers
    opens org.example.condomanagement to javafx.graphics, javafx.fxml;
    opens org.example.condomanagement.controller to javafx.fxml;

    // Cho phép Hibernate truy cập entity
    opens org.example.condomanagement.dao to org.hibernate.orm.core;
    opens org.example.condomanagement.model;


}