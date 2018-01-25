package uk.ac.cam.cl.pico.visualcode;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import uk.ac.cam.cl.pico.gson.VisualCodeGsonTest;

@RunWith(Suite.class)
@SuiteClasses({NewKeyVisualCodeTest.class, KeyPairingVisualCodeTest.class,
        VisualCodeGsonTest.class})
public class VisualCodeTestSuite {

}
