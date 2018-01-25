/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.util.Map;

import uk.ac.cam.cl.pico.data.pairing.LensPairing;
import uk.ac.cam.cl.pico.data.pairing.LensPairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;

import com.j256.ormlite.dao.Dao;

public class DbLensPairingImpFactory
        implements LensPairingImpFactory {

    private final Dao<DbLensPairingImp, Integer> lensPairingDao;
    private final Dao<DbPairingImp, Integer> pairingDao;
    private final DbServiceImpFactory dbServiceImpFactory;

    public DbLensPairingImpFactory(
            final Dao<DbLensPairingImp, Integer> lensPairingDao,
            final Dao<DbPairingImp, Integer> pairingDao,
            final DbServiceImpFactory dbServiceImpFactory) {
        this.pairingDao = pairingDao;
        this.lensPairingDao = lensPairingDao;
        this.dbServiceImpFactory = dbServiceImpFactory;
    }

    @Override
    public DbLensPairingImp getImp(
            String name,
            Service service,
            Map<String, String> credentials) {
        ServiceImp serviceImp = service.getImp();
        DbServiceImp dbServiceImp;
        if (serviceImp instanceof DbServiceImp) {
            dbServiceImp = (DbServiceImp) serviceImp;
        } else {
            dbServiceImp = dbServiceImpFactory.getImp(service);
        }
        return new DbLensPairingImp(
                name,
                dbServiceImp,
                credentials,
                pairingDao,
                lensPairingDao);
    }

    @Override
    public DbLensPairingImp getImp(
            LensPairing credentialPairing) {
        return getImp(
                credentialPairing.getName(),
                credentialPairing.getService(),
                credentialPairing.getCredentials());
    }
}
