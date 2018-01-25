/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.util.Date;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.pairing.PairingImp;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.SessionImpFactory;

import com.j256.ormlite.dao.Dao;

public class DbSessionImpFactory implements SessionImpFactory {

    Dao<DbSessionImp, Integer> sessionDao;
    private DbPairingImpFactory dbPairingImpFactory;

    public DbSessionImpFactory(
            final Dao<DbSessionImp, Integer> sessionDao,
            final DbPairingImpFactory dbPairingImpFactory) {
        this.sessionDao = sessionDao;
        this.dbPairingImpFactory = dbPairingImpFactory;
    }

    @Override
    public DbSessionImp getImp(
            String remoteId,
            SecretKey secretKey,
            Pairing pairing,
            AuthToken authToken,
            Date lastAuthDate,
            Session.Status status,
            Session.Error error) {
        PairingImp imp = pairing.getImp();
        DbPairingImp dbImp;
        if (imp instanceof DbPairingImp) {
            dbImp = (DbPairingImp) imp;
        } else {
            dbImp = dbPairingImpFactory.getImp(pairing);
        }
        return new DbSessionImp(
                remoteId,
                secretKey,
                dbImp,
                authToken,
                lastAuthDate,
                status,
                error,
                sessionDao);
    }

    @Override
    public DbSessionImp getImp(Session session) {
        return getImp(
                session.getRemoteId(),
                session.getSecretKey(),
                session.getPairing(),
                session.hasAuthToken() ? session.getAuthToken() : null,
                session.getLastAuthDate(),
                session.getStatus(),
                session.getError());
    }
}
