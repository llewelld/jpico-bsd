/**
 * Copyright Pico project, 2016
 */

// Copyright University of Cambridge, 2013

package uk.ac.cam.cl.pico.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Configuration of the Pico.
 * 
 * <p>The Config class implements the <code>Map<Object, Object></code> interface, delegating its
 * methods to an internal Properties object. The class implements the Singleton pattern.
 * 
 * @author Graeme Jenkinson <gcj21@cl.cam.ac.uk>
 * @deprecated in favour of the {@link CryptoFactory} class.
 * 
 */
@Deprecated
public class Config implements Map<Object, Object> {

    private static Config instance;

    private final Properties properties;

    private Config(InputStream is) {

        // Verify the method's preconditions
        if (is == null)
            throw new NullPointerException();

        properties = new Properties();
        try {

            properties.load(is);
        } catch (IOException e) {

            // TODO
            e.printStackTrace();
        }
    }

    /**
     * Virtual constructor.
     * 
     * @param is InputStream to read the configuration from.
     * @return the Config instance.
     */
    public static Config newInstance(InputStream is) {
        // Replace old instance if one existed
        instance = new Config(is);
        return instance;
    }

    /**
     * Virtual constructor.
     * 
     * @return the Config instance.
     */
    public static Config getInstance() {

        // Ensure that the class was been initialised.
        if (instance == null) {

            throw new IllegalStateException();
        }
        return instance;
    }

    /**
     * @see Properties.toString
     */
    @Override
    public String toString() {
        return properties.toString();
    }

    /**
     * @see Properties.clear
     */
    @Override
    public void clear() {
        properties.clear();
    }

    /**
     * @see Properties.containsKey
     */
    @Override
    public boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    /**
     * @see Properties.containsValue
     */
    @Override
    public boolean containsValue(Object value) {
        return properties.containsValue(value);
    }

    /**
     * @see Properties.entrySet
     */
    @Override
    public Set<java.util.Map.Entry<Object, Object>> entrySet() {
        return properties.entrySet();
    }

    /**
     * @see Properties.get
     */
    @Override
    public Object get(Object key) {
        return properties.get(key);
    }

    /**
     * @see Properties.isEmpty
     */
    @Override
    public boolean isEmpty() {
        return properties.isEmpty();
    }

    /**
     * @see Properties.keySet
     */
    @Override
    public Set<Object> keySet() {
        return properties.keySet();
    }

    /**
     * @see Properties.put
     */
    @Override
    public Object put(Object key, Object value) {
        return properties.put(key, value);
    }

    /**
     * @see Properties.putAll
     */
    @Override
    public void putAll(Map<? extends Object, ? extends Object> m) {
        properties.putAll(m);
    }

    /**
     * @see Properties.remove
     */
    @Override
    public Object remove(Object key) {
        return properties.remove(key);
    }

    /**
     * @see Properties.size
     */
    @Override
    public int size() {
        return properties.size();
    }

    /**
     * @see Properties.values
     */
    @Override
    public Collection<Object> values() {
        return properties.values();
    }

}

// End of file
