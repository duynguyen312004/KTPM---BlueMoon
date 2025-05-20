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

//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    private User userAccount;

    @ManyToOne(optional = false)
    @JoinColumn(name = "household_id")
    private Household household;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private String relationship;
    // Mã hộ khẩu: Many-to-One với Household
    @ManyToOne(fetch = FetchType.LAZY)

    private Household households;

    // CCCD (national_id) mới thêm
    @Column(name = "national_id", nullable = false, unique = true)
    private String nationalId;

    // Khóa ngoại user_id → lấy số điện thoại
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    // getters & setters
    public Integer getResidentId() {
        return residentId;
    }

//    public User getUserAccount() {
//        return userAccount;
//    }
//
//    public void setUserAccount(User userAccount) {
//        this.userAccount = userAccount;
//    }

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
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
