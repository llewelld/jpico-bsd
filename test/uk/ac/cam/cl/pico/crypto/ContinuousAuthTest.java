package uk.ac.cam.cl.pico.crypto;

import java.lang.reflect.Field;
import java.net.URI;
import java.security.KeyPair;

import javax.crypto.SecretKey;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.cam.cl.pico.crypto.ContinuousProver.ProverStateChangeNotificationInterface;
import uk.ac.cam.cl.pico.crypto.ContinuousProver.SchedulerInterface;
import uk.ac.cam.cl.pico.crypto.messages.MessageTest;
import uk.ac.cam.cl.pico.crypto.messages.SequenceNumber;
import uk.ac.cam.cl.pico.data.pairing.KeyPairing;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.session.Session;
import uk.ac.cam.cl.pico.db.DbDataFactory;
import uk.ac.cam.cl.pico.util.DatabaseHelper;

import com.j256.ormlite.support.ConnectionSource;

@SuppressWarnings("unused")
public class ContinuousAuthTest extends MessageTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(
            ContinuousAuthTest.class.getSimpleName());

    private static DbDataFactory factory;

    final static Field verifierSequenceNumber;
    final static Field proverSequenceNumber;
    final static Field proverState;
    final static Field verifierState;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        MessageTest.setUpBeforeClass();

        // Get a connection to the database so we can create a DbDataFactory.
        // Nothing will ever actually be persisted to the database and the
        // tables are never created.
        LOGGER.debug("Connecting to the database...");
        ConnectionSource dbConnection = DatabaseHelper.getConnection();
        LOGGER.info("Connected to database");

        factory = new DbDataFactory(dbConnection);
    }

    static {
        try {
            verifierSequenceNumber =
                    ContinuousVerifier.class
                            .getDeclaredField("currentSequenceNumber");
            proverSequenceNumber =
                    ContinuousProver.class.getDeclaredField("currentSequenceNumber");
            proverState = ContinuousProver.class.getDeclaredField("state");
            verifierState = ContinuousVerifier.class.getDeclaredField("state");

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        verifierSequenceNumber.setAccessible(true);
        proverSequenceNumber.setAccessible(true);
        proverState.setAccessible(true);
        verifierState.setAccessible(true);
    }

    @Test
    public void testProver() throws Exception {
        SecretKey sharedKey = encKg.generateKey();

		KeyPair picoID = CryptoFactory.INSTANCE.ecKpg().generateKeyPair();
		KeyPair serviceID = CryptoFactory.INSTANCE.ecKpg().generateKeyPair();

        Service service = new Service(
                factory,
                "test service",
                new URI("http://testservice.com"),
                HashUtils.sha256Key(serviceID.getPublic()));
        KeyPairing pairing = new KeyPairing(
                factory, "test pairing", service, picoID);

        AuthToken authToken = new SimpleAuthToken("auth token");

        Session s = Session.newInstanceActive(
                factory,
                "remoteid",
                sharedKey,
                pairing,
                authToken);

        SequenceNumber c = SequenceNumber.getRandomInstance();

        ProverStateChangeNotificationInterface pscni =
                new ProverStateChangeNotificationInterface() {

                    @Override
                    public void sessionPaused(final Session session) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void sessionContinued(final Session session) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void sessionStopped(final Session session) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void sessionError(final Session session) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void tick(final Session session) {

                    }
                };
        SchedulerInterface si = new SchedulerInterface() {

            @Override
            public void setTimer(int msec, ContinuousProver prover) {
                // prover.timer();

            }

            @Override
            public void clearTimer(ContinuousProver prover) {
                // TODO Auto-generated method stub

            }
        };

    }

    private void printState(ContinuousProver p, IContinuousVerifier v)
            throws IllegalArgumentException, IllegalAccessException {
        System.out.println("Prover:   " + proverState.get(p).toString() + " "
                + ((SequenceNumber) proverSequenceNumber.get(p)).toString());
        System.out.println("Verifier: " + verifierState.get(v).toString() + " "
                + ((SequenceNumber) verifierSequenceNumber.get(v)).toString());
        System.out.println();

    }
}
