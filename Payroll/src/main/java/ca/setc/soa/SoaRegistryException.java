package ca.setc.soa;

/**
 * An exception for a NOT-OK message from the soa
 * or other errors when communicating
 */
public class SoaRegistryException extends Exception {

    private int code;

    /**
     * Constructor
     * @param code error code
     * @param message error message
     */
    public SoaRegistryException(int code, String message)
    {
        super(message);
        this.code = code;
    }

    /**
     * Constructor
     * @param cause error cause
     */
    public SoaRegistryException(Throwable cause)
    {
        super(cause);
        this.code = -6;
    }

    /**
     * Get the error code number
     * @return code number
     */
    public int getCode()
    {
        return this.code;
    }
}
