/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import uk.ac.cam.cl.pico.data.terminal.Terminal;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;

public class DbTerminalAccessor implements Terminal.Accessor {
	
	private final Dao<DbTerminalImp, Integer> dao;

    public DbTerminalAccessor(final Dao<DbTerminalImp, Integer> dao) {
        this.dao = dao;
    }

    @Override
    public Terminal getTerminalById(int id) throws IOException {
        try {
            // Build query
            SelectArg idArg = new SelectArg();
            PreparedQuery<DbTerminalImp> query = dao.queryBuilder()
                    .where()
                    .eq(DbTerminalImp.ID_COLUMN, idArg)
                    .prepare();
            idArg.setValue(id);

            // Execute query
            final DbTerminalImp imp = dao.queryForFirst(query);

            if (imp != null) {
                imp.setDao(dao);
                return new Terminal(imp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public Terminal getTerminalByCommitment(byte[] commitment)
    		throws IOException {
        try {
            // Build query
            SelectArg commitArg = new SelectArg();
            PreparedQuery<DbTerminalImp> query = dao.queryBuilder()
                    .where()
                    .eq(DbServiceImp.COMMITMENT_COLUMN, commitArg)
                    .prepare();
            commitArg.setValue(DbServiceImp.stringifyCommitment(commitment));

            // Execute query
            final DbTerminalImp imp = dao.queryForFirst(query);
            if (imp != null) {
                imp.setDao(dao);
                return new Terminal(imp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

	@Override
	public List<Terminal> getAllTerminals() throws IOException {
		try {
			List<DbTerminalImp> imps = dao.queryForAll();
			ArrayList<Terminal> terminals = new ArrayList<Terminal>(imps.size());
			for (DbTerminalImp imp: imps) {
				imp.setDao(dao);
				terminals.add(new Terminal(imp));
			}
			return terminals;
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}
}
