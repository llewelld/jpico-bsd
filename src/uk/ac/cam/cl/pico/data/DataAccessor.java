/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data;

import uk.ac.cam.cl.pico.data.pairing.KeyPairingAccessor;
import uk.ac.cam.cl.pico.data.pairing.LensPairingAccessor;
import uk.ac.cam.cl.pico.data.pairing.PairingAccessor;
import uk.ac.cam.cl.pico.data.service.ServiceAccessor;
import uk.ac.cam.cl.pico.data.session.SessionAccessor;
import uk.ac.cam.cl.pico.data.terminal.Terminal;

public interface DataAccessor
        extends LensPairingAccessor, KeyPairingAccessor,
        PairingAccessor, ServiceAccessor, SessionAccessor, Terminal.Accessor {

}
