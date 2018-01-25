/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.data;

import uk.ac.cam.cl.pico.data.pairing.KeyPairingImpFactory;
import uk.ac.cam.cl.pico.data.pairing.LensPairingImpFactory;
import uk.ac.cam.cl.pico.data.pairing.PairingImpFactory;
import uk.ac.cam.cl.pico.data.service.ServiceImpFactory;
import uk.ac.cam.cl.pico.data.session.SessionImpFactory;
import uk.ac.cam.cl.pico.data.terminal.Terminal;

public interface DataFactory extends
        ServiceImpFactory,
        PairingImpFactory,
        KeyPairingImpFactory,
        LensPairingImpFactory,
        SessionImpFactory,
        Terminal.ImpFactory {

}
