package ca.setc.soa;

/**
 * An exception for a NOT-OK message from the soa
 * or other errors when communicating
 */
public class SoaException extends Exception {

    private int code;

    public static final int FORMAT_ERROR = -1;
    public static final int SEGMENT_ERROR = -2;
    public static final int CONTENT_ERROR = -3;
    public static final int RUNTIME_ERROR = -4;
    public static final int SQL_ERROR = -5;
    public static final int OTHER_ERROR = -6;

    private String msg;

    /**
     * Constructor
     * @param code error code
     * @param message error message
     */
    public SoaException(int code, String message)
    {
        super(message);
        this.code = code;
        this.msg = message;
    }

    /**
     * Constructor
     * @param cause error cause
     */
    public SoaException(Throwable cause)
    {
        super(cause);
        this.code = -6;
        this.msg = cause.getMessage();
    }

    /**
     * Get the error code number
     * @return code number
     */
    public int getCode()
    {
        return this.code;
    }

    public String getErrorMessage()
    {
        return this.msg;
    }
}
