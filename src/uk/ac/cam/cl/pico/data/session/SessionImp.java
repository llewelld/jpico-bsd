/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.session;

import java.util.Date;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.data.Saveable;
import uk.ac.cam.cl.pico.data.pairing.Pairing;

/**
 * Interface of concrete implementations underlying {@link Session} instances.
 * 
 * <p>
 * This interface is part of a <a href="http://en.wikipedia.org/wiki/Bridge_pattern">Bridge
 * pattern</a>. Each <code>Session</code> instance has a reference to a concrete {@link SessionImp}
 * instance. See {@link uk.ac.cam.cl.pico.data.session} package documentation for more information
 * on this pattern.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Session
 * @see SessionImpFactory
 * 
 */
public interface SessionImp extends Saveable {

    int getId();

    String getRemoteId();

    SecretKey getSecretKey();

    Pairing getPairing();

    Session.Status getStatus();

    void setStatus(Session.Status status);

    Session.Error getError();

    void setError(Session.Error error);

    Date getLastAuthDate();

    void setLastAuthDate(Date date);

    boolean hasAuthToken();

    AuthToken getAuthToken() throws IllegalStateException;

    void clearAuthToken();
}
