/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.db;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.sql.SQLException;

import uk.ac.cam.cl.pico.comms.org.apache.commons.codec.binary.Base64;
import uk.ac.cam.cl.pico.crypto.CryptoRuntimeException;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceImp;

import com.google.common.base.Preconditions;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * A concrete {@link Service} class which persists services to a database using ORMLite object
 * relational mapping annotations. Public key fields are persisted using a custom persister class.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Service
 * @see PublicKeyPersister
 * 
 */
@DatabaseTable(tableName = DbServiceImp.SERVICES_TABLE)
public class DbServiceImp implements ServiceImp {

    static final String SERVICES_TABLE = "services";

    static final String ID_COLUMN = "id";
    static final String NAME_COLUMN = "name";
    static final String ADDRESS_STRING_COLUMN = "address";
    static final String COMMITMENT_COLUMN = "commitment";

    /**
     * Initial value of a DbServiceImp's id, before it is saved to the database. DbServiceImps are
     * automatically assigned an id when they are saved to the database for the first time.
     */
    public static final int UNSAVED_ID = 0;

    static String stringifyCommitment(byte[] commitment) {
        return Base64.encodeBase64String(commitment);
    }

    static byte[] unstringifyCommitment(String commitmentString) {
        return Base64.decodeBase64(commitmentString);
    }

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
            columnName = ADDRESS_STRING_COLUMN,
            canBeNull = false,
            useGetSet = true)
    private String addressString;

    @DatabaseField(
            columnName = COMMITMENT_COLUMN,
            index = true,
            canBeNull = false,
            useGetSet = true)
    private String commitmentString;

    private Dao<DbServiceImp, Integer> dao;

    /**
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public DbServiceImp() {}

    DbServiceImp(
            final String name,
            final URI address,
            final byte[] commitment,
            final Dao<DbServiceImp, Integer> dao) {
        this.id = UNSAVED_ID;
        this.name = name;
        this.addressString = address.toString();
        this.commitmentString = stringifyCommitment(commitment);
        this.dao = Preconditions.checkNotNull(
                dao, "Cannot construct DbServiceImp with null dao");
    }

    void setDao(final Dao<DbServiceImp, Integer> dao) {
        this.dao = Preconditions.checkNotNull(
                dao, "Cannot set dao to null");
    }

    @Override
    public void save() throws IOException {
        Preconditions.checkNotNull(
                dao, "DbServiceImp cannot be saved with null DAO");
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

    /**
     * @param id new id value for this <code>DbServiceImp</code>.
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public void setId(final int id) {
        DbServiceImp.checkId(id);
        this.id = id;
    }

    /**
     * Return the id of this <code>DbServiceImp</code>. The id of a <code>DbServiceImp</code>Imp is
     * automatically assigned when it is first saved to the database. This id (see
     * {@link #ID_COLUMN ID_COLUMN}) is the primary key in the services database table.
     * 
     * @return id of this <code>DbServiceImp</code>.
     * @see <code>DbServiceImp</code>Imp#UNSAVED_ID
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * @param name new name value for this <code>DbServiceImp</code>.
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public void setAddressString(final String addressString) {
        this.addressString = addressString;
    }

    /**
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public String getAddressString() {
        return addressString;
    }

    @Override
    public void setAddress(URI address) {
        this.addressString = address.toString();
    }

    @Override
    public URI getAddress() {
        try {
            return new URI(addressString);
        } catch (URISyntaxException e) {
            // Should never happen because addressString should only ever be
            // set in the constructor or in setAddress from URI.toString()
            throw new RuntimeException("addressString not a valid URI", e);
        }
    }

    /**
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public void setCommitmentString(final String commitmentString) {
        this.commitmentString = commitmentString;
    }

    /**
     * @deprecated required by ORMLite, but should not be used.
     */
    @Deprecated
    public String getCommitmentString() {
        return commitmentString;
    }

    @Override
    public byte[] getCommitment() {
        return unstringifyCommitment(commitmentString);
    }

    /**
     * Get the commitment of a public key using the SHA256 digest algorithm. The output byte array
     * is converted to hexadecimal String.
     * 
     * @param publicKey to return the commitment of.
     * @return SHA256 hash of the public key encoded as a hexdecimal String.
     */
    @Deprecated
    static String getCommitment(final PublicKey publicKey) {
        Preconditions.checkNotNull(publicKey);
        try {
            // Generate a hash of the public key to index the Pairings
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(publicKey.getEncoded());

            byte byteData[] = md.digest();

            // Convert the byte to hex format
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {

                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoRuntimeException(e);
        }
    }

    // Database-specific argument checks:

    public static int checkId(int id) {
        if (id <= 0) {
            throw new NumberFormatException(
                    "DbServiceImp id cannot be negative");
        }
        return id;
    }
}
