package org.example.condomanagement.service;

import org.example.condomanagement.dao.ResidentDao;

public class ResidentService {
    private final ResidentDao dao = new ResidentDao();

    public int countAll() {
        return dao.countAll();
    }
}
