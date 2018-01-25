package uk.ac.cam.cl.pico.data.pairing;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.pico.data.DataFactory;
import uk.ac.cam.cl.pico.data.service.Service;
import uk.ac.cam.cl.pico.data.service.ServiceTest;

public abstract class LensPairingAccessorTest {

    private DataFactory factory;
    @SuppressWarnings("unused")
	private LensPairingAccessor accessor;

    protected abstract DataFactory getFactory();

    protected abstract LensPairingAccessor getAccessor();

    /*
     * Create a new LensPairing instance, catching any exceptions so they don't interfere with tests
     * which expect exceptions.
     */
    private LensPairing getPairing(String name) {
        try {
            Service s = ServiceTest.getService(factory);
            Map<String, String> cs = new HashMap<String, String>();
            cs.put("foo", "bar");
            return new LensPairing(factory, name, s, cs);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occured while creating LensPairing"
                            + " instance", e);
        }
    }

    /*
     * Create and save a new LensPairing instance, catching any exceptions so they don't interfere
     * with tests which expect exceptions.
     */
    @SuppressWarnings("unused")
	private LensPairing savePairing(String name) {
        LensPairing p = getPairing(name);
        try {
            p.save();
        } catch (Exception e) {
            throw new RuntimeException(
                    "Exception occured while saving LensPairing"
                            + " instance", e);
        }
        return p;
    }

    @Before
    public void setUp() throws Exception {
        factory = checkNotNull(getFactory());
        accessor = checkNotNull(getAccessor());
    }

    @Test
    public void testGetLensPairingById() {
        fail("Not yet implemented");
    }

    @Test
    public void testGetLensPairingsByServiceCommitment() {
        fail("Not yet implemented");
    }

}
