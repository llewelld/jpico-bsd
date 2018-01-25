/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.io.IOException;
import java.sql.SQLException;

import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.pairing.PairingAccessor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;

public class DbPairingAccessor implements PairingAccessor {

    private final Dao<DbPairingImp, Integer> pairingDao;

    public DbPairingAccessor(final Dao<DbPairingImp, Integer> pairingDao) {
        this.pairingDao = pairingDao;
    }

    @Override
    public Pairing getPairingById(int id) throws IOException {
        try {
            // Build query
            SelectArg idArg = new SelectArg();
            PreparedQuery<DbPairingImp> query = pairingDao.queryBuilder()
                    .where()
                    .eq(DbPairingImp.ID_COLUMN, idArg)
                    .prepare();
            idArg.setValue(id);

            // Execute query
            final DbPairingImp pairingImp = pairingDao.queryForFirst(query);

            if (pairingImp != null) {
                pairingImp.setDao(pairingDao);
                return new Pairing(pairingImp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}
