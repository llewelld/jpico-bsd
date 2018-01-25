/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.net.URI;

import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImpFactory;

import com.j256.ormlite.dao.Dao;

public class DbServiceImpFactory implements ServiceImpFactory {

    private final Dao<DbServiceImp, Integer> serviceDao;

    public DbServiceImpFactory(final Dao<DbServiceImp, Integer> serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Override
    public DbServiceImp getImp(String name, URI address, byte[] commitment) {
        return new DbServiceImp(name, address, commitment, serviceDao);
    }

    @Override
    public DbServiceImp getImp(Service service) {
        return getImp(
                service.getName(),
                service.getAddress(),
                service.getCommitment());
    }
}
