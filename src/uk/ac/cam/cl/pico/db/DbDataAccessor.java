/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import uk.ac.cam.cl.pico.data.DataAccessor;
import uk.ac.cam.cl.pico.data.pairing.LensPairing;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.terminal.Terminal;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public class DbDataAccessor implements DataAccessor {

    private final DbServiceAccessor dbServiceAccessor;
    private final DbPairingAccessor dbPairingAccessor;
    private final DbKeyPairingAccessor dbKeyPairingAccessor;
    private final DbLensPairingAccessor dbLensPairingAccessor;
    private final DbTerminalAccessor dbTerminalAccessor;
    private final DbSessionAccessor dbSessionAccessor;
    
    public DbDataAccessor(final ConnectionSource dbConnection)
            throws SQLException {

        final Dao<DbServiceImp, Integer> serviceDao =
                DaoManager.createDao(dbConnection, DbServiceImp.class);
        final Dao<DbPairingImp, Integer> pairingDao =
                DaoManager.createDao(dbConnection, DbPairingImp.class);
        final Dao<DbKeyPairingImp, Integer> keyPairingDao =
                DaoManager.createDao(dbConnection, DbKeyPairingImp.class);
        final Dao<DbLensPairingImp, Integer> lensPairingDao =
                DaoManager.createDao(dbConnection, DbLensPairingImp.class);
        final Dao<DbTerminalImp, Integer> terminalDao =
        		DaoManager.createDao(dbConnection, DbTerminalImp.class);
        final Dao<DbSessionImp, Integer> sessionDao =
                DaoManager.createDao(dbConnection, DbSessionImp.class);
        
        dbServiceAccessor = new DbServiceAccessor(serviceDao);
        dbPairingAccessor = new DbPairingAccessor(pairingDao);
        dbKeyPairingAccessor = new DbKeyPairingAccessor(
                keyPairingDao, pairingDao, serviceDao);
        dbLensPairingAccessor = new DbLensPairingAccessor(
                lensPairingDao, pairingDao, serviceDao);
        dbTerminalAccessor = new DbTerminalAccessor(terminalDao);
        dbSessionAccessor = new DbSessionAccessor(sessionDao);
    }

    @Override
    public Pairing getPairingById(int id) throws IOException {
        return dbPairingAccessor.getPairingById(id);
    }

    @Override
    public LensPairing getLensPairingById(int pairingId)
            throws IOException {
        return dbLensPairingAccessor.getLensPairingById(pairingId);
    }

    @Override
    public List<LensPairing> getLensPairingsByServiceCommitment(
            byte[] commitment) throws IOException {
        return dbLensPairingAccessor.getLensPairingsByServiceCommitment(
                commitment);
    }

    @Override
    public List<LensPairing> getLensPairingsByServiceCommitmentAndCredentials(byte[] commitment,
            Map<String, String> credentials) throws IOException {
        return dbLensPairingAccessor.getLensPairingsByServiceCommitmentAndCredentials(
                commitment, credentials);
    }
    
	@Override
	public List<LensPairing> getAllLensPairings() throws IOException {
		return dbLensPairingAccessor.getAllLensPairings();
	}

    @Override
    public KeyPairing getKeyPairingById(int pairingId) throws IOException {
        return dbKeyPairingAccessor.getKeyPairingById(pairingId);
    }

    @Override
    public List<KeyPairing> getKeyPairingsByServiceCommitment(byte[] commitment)
            throws IOException {
        return dbKeyPairingAccessor.getKeyPairingsByServiceCommitment(
                commitment);
    }

	@Override
	public List<KeyPairing> getAllKeyPairings() throws IOException {
		return dbKeyPairingAccessor.getAllKeyPairings();
	}

    @Override
    public Service getServiceById(int serviceId) throws IOException {
        return dbServiceAccessor.getServiceById(serviceId);
    }

    @Override
    public Service getServiceByCommitment(byte[] uri) throws IOException {
        return dbServiceAccessor.getServiceByCommitment(uri);
    }

	@Override
	public Terminal getTerminalById(int id) throws IOException {
		return dbTerminalAccessor.getTerminalById(id);
	}

	@Override
	public Terminal getTerminalByCommitment(byte[] commitment)
			throws IOException {
		return dbTerminalAccessor.getTerminalByCommitment(commitment);
	}

	@Override
	public List<Terminal> getAllTerminals() throws IOException {
		return dbTerminalAccessor.getAllTerminals();
	}

    @Override
    public Session getSessionById(int sessionId) throws IOException {
        return dbSessionAccessor.getSessionById(sessionId);
    }
}
