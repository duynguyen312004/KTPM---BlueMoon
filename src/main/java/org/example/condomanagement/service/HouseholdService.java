package org.example.condomanagement.service;

import org.example.condomanagement.dao.HouseholdDao;

public class HouseholdService {
    private final HouseholdDao dao = new HouseholdDao();

    public int countAll() {
        return dao.countAll();
    }
}
