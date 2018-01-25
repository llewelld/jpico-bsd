/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.pairing.KeyPairingImpFactory;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;

import com.j256.ormlite.dao.Dao;

public class DbKeyPairingImpFactory implements KeyPairingImpFactory {

    private final Dao<DbKeyPairingImp, Integer> keyPairingDao;
    private final Dao<DbPairingImp, Integer> pairingDao;
    private final DbServiceImpFactory dbServiceImpFactory;

    public DbKeyPairingImpFactory(
            final Dao<DbKeyPairingImp, Integer> keyPairingDao,
            final Dao<DbPairingImp, Integer> pairingDao,
            final DbServiceImpFactory dbServiceImpFactory) {
        this.pairingDao = pairingDao;
        this.keyPairingDao = keyPairingDao;
        this.dbServiceImpFactory = dbServiceImpFactory;
    }

    @Override
    public DbKeyPairingImp getImp(
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey) {
        ServiceImp serviceImp = service.getImp();
        DbServiceImp dbServiceImp;
        if (serviceImp instanceof DbServiceImp) {
            dbServiceImp = (DbServiceImp) serviceImp;
        } else {
            dbServiceImp = dbServiceImpFactory.getImp(service);
        }
        return new DbKeyPairingImp(
                name,
                dbServiceImp,
                publicKey,
                privateKey,
                pairingDao,
                keyPairingDao);
    }

    @Override
    public DbKeyPairingImp getImp(
            String name, Service service, KeyPair keyPair) {
        return getImp(
                name, service, keyPair.getPublic(), keyPair.getPrivate());
    }

    @Override
    public DbKeyPairingImp getImp(KeyPairing keyPairing) {
        return getImp(
                keyPairing.getName(),
                keyPairing.getService(),
                keyPairing.getPublicKey(),
                keyPairing.getPrivateKey());
    }
}
