/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data.session;

import java.util.Date;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.data.pairing.Pairing;

/**
 * Interface of a factory which produces concrete {@link SessionImp} instances.
 * 
 * @author Max Spencer <ms955@cl.cam.ac.uk>
 * 
 * @see Session
 * @see SessionImp
 * 
 */
public interface SessionImpFactory {

    public SessionImp getImp(
            String remoteId,
            SecretKey secretKey,
            Pairing pairing,
            AuthToken authToken,
            Date lastAuthDate,
            Session.Status status,
            Session.Error error);

    public SessionImp getImp(Session session);
}
