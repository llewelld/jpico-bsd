/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.security.PrivateKey;
import java.security.PublicKey;

import uk.ac.cam.cl.pico.data.terminal.Terminal;

import com.j256.ormlite.dao.Dao;

public class DbTerminalImpFactory implements Terminal.ImpFactory {
	
	private final Dao<DbTerminalImp, Integer> dao;
	
	public DbTerminalImpFactory(Dao<DbTerminalImp, Integer> dao) {
		this.dao = dao;
	}

	@Override
	public DbTerminalImp getImp(
			String name,
			byte[] commitment,
			PublicKey picoPublicKey,
			PrivateKey picoPrivateKey) {
		return new DbTerminalImp(name, commitment, picoPublicKey, picoPrivateKey, dao);
	}

	@Override
	public DbTerminalImp getImp(Terminal terminal) {
		return getImp(
				terminal.getName(),
				terminal.getCommitment(), 
				terminal.getPicoPublicKey(),
				terminal.getPicoPrivateKey());
	}

}
