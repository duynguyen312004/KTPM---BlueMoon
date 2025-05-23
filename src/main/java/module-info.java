module CondoManagement {
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
    requires java.desktop;

    opens org.example.condomanagement to javafx.fxml;
    exports org.example.condomanagement to javafx.graphics;

    opens org.example.condomanagement.controller to javafx.fxml;
    exports org.example.condomanagement.controller to javafx.graphics;

    opens org.example.condomanagement.model to org.hibernate.orm.core;
    exports org.example.condomanagement.model to org.hibernate.orm.core;

    opens org.example.condomanagement.dao to org.hibernate.orm.core;
    exports org.example.condomanagement.dao to org.hibernate.orm.core;
}