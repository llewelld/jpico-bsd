/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import java.io.IOException;

/**
 * An AuthToken must provide a {@link #getFull() getFull} method which returns the full form of the
 * token to be transferred automatically to the terminal and a {@link #getFallback() getFallback}
 * method which returns a string to be displayed to the user when no communication channel between
 * the Pico and user's terminal is available.
 * 
 * <p>
 * An AuthToken is sent from the service to the Pico in the final message of the Pico authentication
 * protocol.
 * 
 * @see SessionDelegationMessage
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 */
public interface AuthToken {
    public abstract String getFull();

    public abstract String getFallback();

    public abstract byte[] toByteArray() throws IOException;
}
