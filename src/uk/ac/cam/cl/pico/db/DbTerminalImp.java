/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.sql.SQLException;

import uk.ac.cam.cl.pico.data.terminal.Terminal;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName=DbTerminalImp.TERMINALS_TABLE)
public class DbTerminalImp implements Terminal.Imp {
	
	public static final String TERMINALS_TABLE = "terminals";
	
	public static final String ID_COLUMN = "id";
	static final String NAME_COLUMN = "name";
    static final String COMMITMENT_COLUMN = "commitment";
    static final String PICO_PUBLIC_KEY_COLUMN = "pico_public_key";
    static final String PICO_PRIVATE_KEY_COLUMN = "pico_private_key";

    /**
     * Initial value of an instance's id, before it is saved to the database.
     * They will automatically be assigned a different unique id when they are
     * saved to the database for the first time.
     */
    public static final int UNSAVED_ID = 0;
    
    @DatabaseField(
            columnName = ID_COLUMN,
            generatedId = true, // Primary key -- automatically generated on save
            throwIfNull = true,
            useGetSet = true)
    private int id;

    @DatabaseField(
            columnName = NAME_COLUMN,
            canBeNull = false,
            useGetSet = true)
    private String name;

    @DatabaseField(
            columnName = COMMITMENT_COLUMN,
            index = true,
            canBeNull = false,
            useGetSet = true)
    private String commitmentString;

    @DatabaseField(
            columnName = PICO_PUBLIC_KEY_COLUMN,
            canBeNull = false,
            useGetSet = true,
            persisterClass = PublicKeyPersister.class)
    private PublicKey picoPublicKey;
    
    @DatabaseField(
            columnName = PICO_PRIVATE_KEY_COLUMN,
            canBeNull = false,
            useGetSet = true,
            persisterClass = PrivateKeyPersister.class)
    private PrivateKey picoPrivateKey;

    private Dao<DbTerminalImp, Integer> dao;
    
    /**
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public DbTerminalImp() {}
    
    DbTerminalImp(
            final String name,
            final byte[] commitment,
            final PublicKey picoPublicKey,
            final PrivateKey picoPrivateKey,
            final Dao<DbTerminalImp, Integer> dao) {
        this.id = UNSAVED_ID;
        this.name = name;
        this.commitmentString = DbServiceImp.stringifyCommitment(commitment);
        this.picoPublicKey = picoPublicKey;
        this.picoPrivateKey = picoPrivateKey;
        this.dao = checkNotNull(dao, "dao cannot be null");
    }

    void setDao(final Dao<DbTerminalImp, Integer> dao) {
        this.dao = checkNotNull(dao, "dao cannot be null");
    }

    @Override
    public void save() throws IOException {
        checkNotNull(dao, "cannot be saved with null dao");
        try {
            dao.createOrUpdate(this);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean isSaved() {
        return (id != UNSAVED_ID);
    }
    
    @Override
    public void delete() throws IOException {
    	checkNotNull(dao, "cannot be saved with null dao");
    	if (isSaved()) {
    		try {
				dao.delete(this);
				id = UNSAVED_ID;
			} catch (SQLException e) {
				throw new IOException(e);
			}
    	} else {
    		throw new IllegalStateException("cannot delete an unsaved terminal");
    	}
    }
    
    /**
     * @deprecated Required by ORMLite, should not be used.
     */
    @Deprecated
    public void setId(int id) {
    	this.id = id;
    }

	@Override
	public int getId() {
		return id;
	}

	/**
     * @deprecated Required by ORMLite, should not be used.
     */
    @Deprecated
    public void setName(String name) {
    	this.name = name;
    }
    
	@Override
	public String getName() {
		return name;
	}
	
	/**
     * @deprecated Required by ORMLite, should not be used.
     */
    @Deprecated
    public void setCommitmentString(String commitmentString) {
    	this.commitmentString = commitmentString;
    }
    
    /**
     * @deprecated Required by ORMLite, should not be used.
     */
    @Deprecated
    public String getCommitmentString() {
    	return commitmentString;
    }
    
	@Override
	public byte[] getCommitment() {
		return DbServiceImp.unstringifyCommitment(commitmentString);
	}
	
	/**
     * @deprecated Required by ORMLite, should not be used.
     */
	@Deprecated
	public void setPicoPublicKey(PublicKey picoPublicKey) {
		this.picoPublicKey = picoPublicKey;
	}

	@Override
	public PublicKey getPicoPublicKey() {
		return picoPublicKey;
	}
	
	/**
     * @deprecated Required by ORMLite, should not be used.
     */
	@Deprecated
	public void setPicoPrivateKey(PrivateKey picoPrivateKey) {
		this.picoPrivateKey = picoPrivateKey;
	}

	@Override
	public PrivateKey getPicoPrivateKey() {
		return picoPrivateKey;
	}
}
