/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.net.URI;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.data.DataFactory;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.pairing.LensPairing;
import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.Session.Error;
import uk.ac.cam.cl.pico.data.session.Session.Status;
import uk.ac.cam.cl.pico.data.terminal.Terminal;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public class DbDataFactory implements DataFactory {

    private final DbServiceImpFactory dbServiceImpFactory;
    private final DbPairingImpFactory dbPairingImpFactory;
    private final DbKeyPairingImpFactory dbKeyPairingImpFactory;
    private final DbLensPairingImpFactory dbCredentialPairingImpFactory;
    private final DbSessionImpFactory dbSessionImpFactory;
    private final DbTerminalImpFactory dbTerminalImpFactory;

    public DbDataFactory(final ConnectionSource dbConnection)
            throws SQLException {

        final Dao<DbServiceImp, Integer> serviceDao =
                DaoManager.createDao(dbConnection, DbServiceImp.class);
        final Dao<DbPairingImp, Integer> pairingDao =
                DaoManager.createDao(dbConnection, DbPairingImp.class);
        final Dao<DbKeyPairingImp, Integer> keyPairingDao =
                DaoManager.createDao(dbConnection, DbKeyPairingImp.class);
        final Dao<DbLensPairingImp, Integer> credentialPairingDao =
                DaoManager.createDao(dbConnection, DbLensPairingImp.class);
        final Dao<DbSessionImp, Integer> sessionDao =
                DaoManager.createDao(dbConnection, DbSessionImp.class);
        final Dao<DbTerminalImp,Integer> terminalDao =
        		DaoManager.createDao(dbConnection, DbTerminalImp.class);

        dbServiceImpFactory = new DbServiceImpFactory(serviceDao);
        dbPairingImpFactory = new DbPairingImpFactory(
                pairingDao, dbServiceImpFactory);
        dbKeyPairingImpFactory = new DbKeyPairingImpFactory(
                keyPairingDao, pairingDao, dbServiceImpFactory);
        dbCredentialPairingImpFactory = new DbLensPairingImpFactory(
                credentialPairingDao, pairingDao, dbServiceImpFactory);
        dbSessionImpFactory = new DbSessionImpFactory(
                sessionDao, dbPairingImpFactory);
        dbTerminalImpFactory = new DbTerminalImpFactory(terminalDao);
    }

    @Override
    public ServiceImp getImp(String name, URI address, byte[] commitment) {
        return dbServiceImpFactory.getImp(name, address, commitment);
    }

    @Override
    public DbServiceImp getImp(Service service) {
        return dbServiceImpFactory.getImp(service);
    }

    @Override
    public DbPairingImp getImp(String name, Service service) {
        return dbPairingImpFactory.getImp(name, service);
    }

    @Override
    public DbPairingImp getImp(Pairing pairing) {
        return dbPairingImpFactory.getImp(pairing);
    }

    @Override
    public DbSessionImp getImp(
            String remoteId,
            SecretKey secretKey,
            Pairing pairing,
            AuthToken authToken,
            Date lastAuthDate,
            Status status,
            Error error) {
        return dbSessionImpFactory.getImp(
                remoteId,
                secretKey,
                pairing,
                authToken,
                lastAuthDate,
                status,
                error);
    }

    @Override
    public DbSessionImp getImp(Session session) {
        return dbSessionImpFactory.getImp(session);
    }

    @Override
    public DbKeyPairingImp getImp(
            String name,
            Service service,
            PublicKey publicKey,
            PrivateKey privateKey) {
        return dbKeyPairingImpFactory.getImp(
                name, service, publicKey, privateKey);
    }

    @Override
    public DbKeyPairingImp getImp(
            String name, Service service, KeyPair keyPair) {
        return dbKeyPairingImpFactory.getImp(name, service, keyPair);
    }

    @Override
    public DbKeyPairingImp getImp(KeyPairing keyPairing) {
        return dbKeyPairingImpFactory.getImp(keyPairing);
    }

    @Override
    public DbLensPairingImp getImp(String name,
            Service service, Map<String, String> credentials) {
        return dbCredentialPairingImpFactory.getImp(
                name, service, credentials);
    }

    @Override
    public DbLensPairingImp getImp(LensPairing credentialPairing) {
        return dbCredentialPairingImpFactory.getImp(credentialPairing);
    }

	@Override
	public Terminal.Imp getImp(
			String name, byte[] commitment, PublicKey picoPublicKey, PrivateKey picoPrivateKey) {
		return dbTerminalImpFactory.getImp(name, commitment, picoPublicKey, picoPrivateKey);
	}

	@Override
	public Terminal.Imp getImp(Terminal terminal) {
		return dbTerminalImpFactory.getImp(terminal);
	}
}
