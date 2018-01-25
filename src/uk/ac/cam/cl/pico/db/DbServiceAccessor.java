/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.io.IOException;
import java.sql.SQLException;

import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceAccessor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;

public class DbServiceAccessor implements ServiceAccessor {

    private final Dao<DbServiceImp, Integer> serviceDao;

    public DbServiceAccessor(final Dao<DbServiceImp, Integer> serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Override
    public Service getServiceById(int serviceId) throws IOException {
        try {
            // Build query
            SelectArg idArg = new SelectArg();
            PreparedQuery<DbServiceImp> query = serviceDao.queryBuilder()
                    .where()
                    .eq(DbServiceImp.ID_COLUMN, idArg)
                    .prepare();
            idArg.setValue(serviceId);

            // Execute query
            final DbServiceImp serviceImp = serviceDao.queryForFirst(query);

            if (serviceImp != null) {
                serviceImp.setDao(serviceDao);
                return new Service(serviceImp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Service getServiceByCommitment(byte[] commitment)
            throws IOException {
        try {
            // Build query
            SelectArg commitArg = new SelectArg();
            PreparedQuery<DbServiceImp> query = serviceDao.queryBuilder()
                    .where()
                    .eq(DbServiceImp.COMMITMENT_COLUMN, commitArg)
                    .prepare();
            commitArg.setValue(DbServiceImp.stringifyCommitment(commitment));

            // Execute query
            final DbServiceImp serviceImp = serviceDao.queryForFirst(query);
            if (serviceImp != null) {
                serviceImp.setDao(serviceDao);
                return new Service(serviceImp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
