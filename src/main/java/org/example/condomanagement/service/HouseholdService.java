package org.example.condomanagement.service;

import java.util.List;
import org.example.condomanagement.dao.HouseholdDao;
import org.example.condomanagement.model.Household;

/**
 * Business logic for managing households (CRUD operations).
 * Delegates persistence actions to HouseholdDao.
 */
public class HouseholdService {
    private final HouseholdDao dao = new HouseholdDao();

    /**
     * Retrieve all households.
     * @return List of all Household entities
     */
    public List<Household> findAll() {
        return dao.findAll();
    }

    /**
     * Find a household by its ID.
     * @param id Primary key of the household
     * @return Household or null if not found
     */
    public Household findById(Integer id) {
        return dao.findById(id);
    }

    public int countOccupiedHouseholds() {
        return (int) dao.countOccupiedHouseholds();
    }

    /**
     * Create a new household or update an existing one.
     * @param h Household entity to save
     * @return true if operation succeeded
     */
    public Household  saveOrUpdate (Household h) {

         return    dao.save(h);  // merge() handles insert or update

    }

    /**
     * Delete a household by its ID.
     * @param id Primary key of the household to delete
     * @return true if deletion succeeded
     */
    public boolean deleteHousehold(Integer id) {
        try {
            Household h = dao.findById(id);
            if (h != null) {
                dao.delete(h);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Đếm tổng số hộ khẩu
     */
    public int countAll() {
        return dao.countAll();
    }
}
