package org.example.condomanagement.service;

import org.example.condomanagement.dao.VehicleDao;

public class VehicleService {
    private final VehicleDao dao = new VehicleDao();

    public int countAll() {
        return dao.countAll();
    }
}
