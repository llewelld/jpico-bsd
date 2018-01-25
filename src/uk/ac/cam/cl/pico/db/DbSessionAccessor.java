/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.sql.SQLException;

import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.SessionAccessor;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;

public class DbSessionAccessor implements SessionAccessor {

    private final Dao<DbSessionImp, Integer> sessionDao;

    public DbSessionAccessor(
            final Dao<DbSessionImp, Integer> sessionDao) {
        this.sessionDao = checkNotNull(sessionDao);
    }

    @Override
    public Session getSessionById(final int sessionId)
            throws IOException {
        try {
            // Build query
            final SelectArg sessionIdArg = new SelectArg();
            final PreparedQuery<DbSessionImp> query =
                    sessionDao.queryBuilder()
                            .where()
                            .eq(DbSessionImp.ID_COLUMN, sessionIdArg)
                            .prepare();

            // Execute query
            sessionIdArg.setValue(sessionId);
            final DbSessionImp imp =
                    sessionDao.queryForFirst(query);

            // Prepare result
            if (imp != null) {
                imp.setDao(sessionDao);
                return new Session(imp);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }
}