/**
 * Copyright Pico project, 2016
 */

// Copyright University of Cambridge, 2013

package uk.ac.cam.cl.pico.db;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

import java.sql.SQLException;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * A custom ORMLite persister for PublicKey instances. Persists a Elliptic Curve key to the
 * database. The key is stored as a X509 encoded byte array.
 * 
 * @see PrivateKeyPersister
 * @author Graeme Jenkinson
 * 
 */
public class SecretKeyPersister extends BaseDataType {

    private static final SecretKeyPersister singleton = new SecretKeyPersister();

    public static SecretKeyPersister getSingleton() {
        return singleton;
    }

    protected SecretKeyPersister() {
        super(SqlType.BYTE_ARRAY, new Class<?>[0]);
    }

    protected SecretKeyPersister(SqlType sqlType, Class<?>[] classes) {
        super(sqlType, classes);
    }

    /**
     * Set a SecretKey field to a default value.
     * 
     * @param fieldType The field type (BYTE_ARRAY).
     * @param defaultStr The request default value.
     * @return Object default representation.
     * @throws SQLException PublicKey cannot have a default value.
     */
    @Override
    public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
        throw new SQLException("SecretKey cannot have a default value");
    }

    /**
     * Convert a result of an SQL query into the type of class as the data is persisted in the
     * database.
     * 
     * @param fieldType The field type (BYTE_ARRAY).
     * @param results The result from an SQL query.
     * @param columnPos The column index.
     * @return byte[] of the X509 encoded public key.
     */
    @Override
    public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos)
            throws SQLException {
        return (byte[]) results.getBytes(columnPos);
    }

    /**
     * Convert a persisted byte array into a SecretKey instance.
     * 
     * @param fieldType The field type (BYTE_ARRAY).
     * @param sqlArg The type of the class as it is persisted in the databases.
     * @param columnPos The column index.
     * @return a PrivateKey instance.
     * @throws SQLException TODO
     */
    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos)
            throws SQLException {
        SecretKey key = new SecretKeySpec((byte[]) sqlArg, "AES/GCM/NoPadding");
        return key;
    }

    /**
     * Convert a SecretKey instance to a byte array for persisting.
     * 
     * @param fieldType The field type (BYTE_ARRAY).
     * @param javaObject The POJO Java object, that is, a PublicKey.
     * @return byte[] X509 encoded private key.
     */
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        SecretKey key = (SecretKey) javaObject;
        return key.getEncoded();
    }

    @Override
    public boolean isAppropriateId() {
        return false;
    }

    @Override
    public boolean isArgumentHolderRequired() {
        return true;
    }

}

// End of file
