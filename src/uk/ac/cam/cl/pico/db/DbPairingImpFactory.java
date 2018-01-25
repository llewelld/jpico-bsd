/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.pairing.PairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;

import com.j256.ormlite.dao.Dao;

public class DbPairingImpFactory implements PairingImpFactory {

    private final Dao<DbPairingImp, Integer> pairingDao;
    private final DbServiceImpFactory dbServiceImpFactory;

    public DbPairingImpFactory(
            final Dao<DbPairingImp, Integer> pairingDao,
            final DbServiceImpFactory dbServiceImpFactory) {
        this.pairingDao = pairingDao;
        this.dbServiceImpFactory = dbServiceImpFactory;
    }

    @Override
    public DbPairingImp getImp(String name, Service service) {
        ServiceImp imp = service.getImp();
        DbServiceImp dbImp;
        if (imp instanceof DbServiceImp) {
            dbImp = (DbServiceImp) imp;
        } else {
            dbImp = dbServiceImpFactory.getImp(service);
        }
        return new DbPairingImp(name, dbImp, pairingDao);
    }

    @Override
    public DbPairingImp getImp(Pairing pairing) {
        return getImp(pairing.getName(), pairing.getService());
    }

    /*
     * // Test
     * 
     * public List<Pairing> getPairingsByServiceId(int serviceId) throws SQLException {
     * Dao<DbServiceImp, Integer> serviceDao; PreparedQuery<DbPairingImp> query = null; // build
     * query using pairingDao and serviceDao
     * 
     * List<DbPairingImp> imps = pairingDao.query(query); List<Pairing> pairings = new
     * ArrayList<Pairing>(imps.size()); for (DbPairingImp imp : imps) { pairings.add(new
     * Pairing(imp)); } return pairings; }
     */
}
