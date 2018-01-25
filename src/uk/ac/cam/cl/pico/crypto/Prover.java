/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto;

import javax.security.auth.Destroyable;

import uk.ac.cam.cl.pico.crypto.ContinuousProver.ProverStateChangeNotificationInterface;
import uk.ac.cam.cl.pico.crypto.ContinuousProver.SchedulerInterface;
import uk.ac.cam.cl.pico.data.session.Session;

public interface Prover extends Destroyable {

    public ContinuousProver getContinuousProver(
            final Session session,
            final ProverStateChangeNotificationInterface
            proverStateChangeNotificationInterface,
            final SchedulerInterface schedulerInterface);

    public Session startSession() throws CryptoRuntimeException;

}
