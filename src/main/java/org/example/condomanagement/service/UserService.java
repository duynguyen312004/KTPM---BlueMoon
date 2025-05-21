package org.example.condomanagement.service;

import java.util.List;

import org.example.condomanagement.config.HibernateUtil;
import org.example.condomanagement.dao.UserDao;
import org.example.condomanagement.model.Transaction;
import org.example.condomanagement.model.User;
import org.hibernate.Session;

/**
 * Business logic for authentication and account/resident management.
 */
public class UserService {
    private final UserDao userDao = new UserDao();

    /**
     * Authenticate staff user by username and password.
     * Only Admin or Accountant roles allowed.
     * 
     * @return User if credentials match and role is Admin or Accountant; otherwise
     *         null.
     */
    public User authenticate(String username, String password) {
        User user = userDao.findByUsername(username);
        if (user != null && user.getPassword().equals(password)
                && (user.getRole() == User.Role.Admin || user.getRole() == User.Role.Accountant)) {
            return user;
        }
        return null;
    }

    public boolean resetPassword(String username, String newPassword) {
        try {
            userDao.updatePassword(username, newPassword);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a new staff account with given role.
     * Only Admin or Accountant roles can be created.
     *
     * @param username the username
     * @param password the plain password
     * @param role     the role (Admin or Accountant)
     * @return true if created successfully; false if username exists or role
     *         invalid.
     */
    public boolean createStaff(String username, String password, User.Role role, String fullName, String phone) {
        if (userDao.findByUsername(username) != null)
            return false;

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        user.setFullName(fullName);
        user.setPhoneNumber(phone);
        user.setIsActive(true);

        userDao.save(user);
        return true;
    }

    /**
     * List all staff users by a given role (Admin or Accountant).
     *
     * @param role the staff role to filter
     * @return list of Users with that role
     */
    public List<User> getAllByRole(User.Role role) {
        if (role == User.Role.Resident) {
            throw new IllegalArgumentException("Cannot list residents here");
        }
        return userDao.findByRole(role);
    }

    /* delete user */
    public boolean deleteUser(Integer userId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.beginTransaction();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.remove(user);
                session.getTransaction().commit();
                return true;
            }
            return false;
        }
    }

    public void updateUser(User user) {
        userDao.updateUser(user);
    }


}
