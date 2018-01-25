/**
 * Copyright Pico project, 2016
 */

package uk.ac.cam.cl.pico.crypto.messages;

public enum ReauthState {
    CONTINUE,
    PAUSE,
    STOP,
    ERROR;

    @SuppressWarnings("serial")
    public static class InvalidReauthStateIndexException extends Exception {
        public InvalidReauthStateIndexException(byte state) {
            super(String.format("State %d does not exist. Valid states between 0 and %d", state,
                    ReauthState.values().length - 1));
        }
    }

    public static ReauthState fromByte(byte reauthStateIndex)
            throws InvalidReauthStateIndexException {
        if (reauthStateIndex < 0 || reauthStateIndex >= ReauthState.values().length)
            throw new InvalidReauthStateIndexException(reauthStateIndex);
        final ReauthState reauthState = ReauthState.values()[reauthStateIndex];
        return reauthState;
    }

    public byte toByte() {
        return (byte) ordinal();
    }
}
