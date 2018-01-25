/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.pairing.KeyPairingAccessor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

public class DbKeyPairingAccessor implements KeyPairingAccessor {

    private final Dao<DbKeyPairingImp, Integer> keyPairingDao;
    private final Dao<DbPairingImp, Integer> pairingDao;
    private final Dao<DbServiceImp, Integer> serviceDao;

    public DbKeyPairingAccessor(
            final Dao<DbKeyPairingImp, Integer> keyPairingDao,
            final Dao<DbPairingImp, Integer> pairingDao,
            final Dao<DbServiceImp, Integer> serviceDao) {
        this.keyPairingDao = keyPairingDao;
        this.pairingDao = pairingDao;
        this.serviceDao = serviceDao;
    }

    @Override
    public KeyPairing getKeyPairingById(int pairingId) throws IOException {
        try {
            SelectArg pairingIdArg = new SelectArg();
            PreparedQuery<DbKeyPairingImp> query = keyPairingDao.queryBuilder()
                    .where()
                    .eq(DbKeyPairingImp.PAIRING_COLUMN, pairingIdArg)
                    .prepare();
            pairingIdArg.setValue(pairingId);
            final DbKeyPairingImp keyPairingImp = keyPairingDao.queryForFirst(query);

            if (keyPairingImp != null) {
                keyPairingImp.setDao(keyPairingDao);
                return new KeyPairing(keyPairingImp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<KeyPairing> getKeyPairingsByServiceCommitment(
            byte[] commitment) throws IOException {
        try {
            // Build a query for all services with matching public key
            // commitment
            SelectArg commitArg = new SelectArg();
            QueryBuilder<DbServiceImp, Integer> serviceQb =
                    serviceDao.queryBuilder();
            serviceQb.where().eq(DbServiceImp.COMMITMENT_COLUMN, commitArg);

            // Join this on a query for pairings
            QueryBuilder<DbPairingImp, Integer> pairingQb =
                    pairingDao.queryBuilder().join(serviceQb);

            // Join this on a query for key pairings
            PreparedQuery<DbKeyPairingImp> query =
                    keyPairingDao.queryBuilder().join(pairingQb).prepare();

            // Execute the query
            commitArg.setValue(DbServiceImp.stringifyCommitment(commitment));
            List<DbKeyPairingImp> imps = keyPairingDao.query(query);

            // Transform results
            List<KeyPairing> keyPairings = new ArrayList<KeyPairing>(imps.size());
            for (DbKeyPairingImp imp : imps) {
                imp.setDao(keyPairingDao);
                keyPairings.add(new KeyPairing(imp));
            }
            return keyPairings;
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

	@Override
	public List<KeyPairing> getAllKeyPairings() throws IOException {
		List<DbKeyPairingImp> imps;
		try {
			imps = keyPairingDao.queryForAll();
		} catch (SQLException e) {
			throw new IOException(e);
		}
		
		// Transform results
        List<KeyPairing> keyPairings = new ArrayList<KeyPairing>(imps.size());
        for (DbKeyPairingImp imp : imps) {
            imp.setDao(keyPairingDao);
            keyPairings.add(new KeyPairing(imp));
        }
		return keyPairings;
	}
}
