module CondoManagement {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    // requires java.persistence;
    // Hibernate & JPA
    requires org.hibernate.orm.core;

    // Optional UI framework
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires jakarta.persistence;

    opens org.example.condomanagement to javafx.fxml;
    exports org.example.condomanagement to javafx.graphics;

    opens org.example.condomanagement.controller to javafx.fxml;
    exports org.example.condomanagement.controller to javafx.graphics;

    opens org.example.condomanagement.model to org.hibernate.orm.core, javafx.base;
    exports org.example.condomanagement.model to org.hibernate.orm.core;

    opens org.example.condomanagement.dao to org.hibernate.orm.core;
    exports org.example.condomanagement.dao to org.hibernate.orm.core;
}