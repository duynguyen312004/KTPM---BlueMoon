    package org.example.condomanagement.model;

    import jakarta.persistence.*;
    import java.time.LocalDate;

    @Entity
    @Table(name = "residents")
    public class Resident extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "resident_id")
        private Integer residentId;

        @ManyToOne(optional = false)
        @JoinColumn(name = "household_id")
        private Household household;

        @Column(nullable = false)
        private String name;

        @Column(nullable = false)
        private LocalDate birthday;

        @Column(nullable = false)
        private String relationship;

        // CCCD (national_id) mới thêm
        @Column(name = "national_id", nullable = false, unique = true)
        private String nationalId;

        // Số điện thoại riêng của Resident
        @Column(name = "phone_number", nullable = false, unique = true)
        private String phoneNumber;
        // getters & setters
        public Integer getResidentId() {
            return residentId;
        }

        public Household getHousehold() {
            return household;
        }

        public void setHousehold(Household household) {
            this.household = household;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDate getBirthday() {
            return birthday;
        }

        public void setBirthday(LocalDate birthday) {
            this.birthday = birthday;
        }

        public String getRelationship() {
            return relationship;
        }

        public void setRelationship(String relationship) {
            this.relationship = relationship;
        }
        public String getNationalId() {
            return nationalId;
        }
        public void setNationalId(String nationalId) {
            this.nationalId = nationalId;
        }
        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
