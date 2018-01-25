package uk.ac.cam.cl.pico.data.test;

import java.util.Date;

import javax.crypto.SecretKey;

import uk.ac.cam.cl.pico.crypto.AuthToken;
import uk.ac.cam.cl.pico.data.pairing.Pairing;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.data.session.Session.Error;
import uk.ac.cam.cl.pico.data.session.Session.Status;
import uk.ac.cam.cl.pico.data.session.SessionImp;
import uk.ac.cam.cl.pico.data.session.SessionImpFactory;

public class TestSessionImpFactory implements SessionImpFactory {

    @Override
    public SessionImp getImp(
            String remoteId,
            SecretKey secretKey,
            Pairing pairing,
            AuthToken authToken,
            Date lastAuthDate,
            Status status,
            Error error) {
        return new TestSessionImp(
                remoteId,
                secretKey,
                pairing,
                status,
                error,
                lastAuthDate,
                authToken);
    }

    @Override
    public SessionImp getImp(Session session) {
        return getImp(
                session.getRemoteId(),
                session.getSecretKey(),
                session.getPairing(),
                session.getAuthToken(),
                session.getLastAuthDate(),
                session.getStatus(),
                session.getError());
    }

}
