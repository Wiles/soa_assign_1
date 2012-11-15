package ca.setc.hl7;

import java.io.UnsupportedEncodingException;

/**
 * Represents a field in an HL7 message
 */
public class Field {
    private String value;

    /**
     * Constructor for string values
     *
     * @param value
     */
    public Field(String value)
    {
        this.value = value;
    }

    /**
     * Constructor for Integer values
     * @param value
     */
    public Field(int value)
    {
        this.value = Integer.toString(value);
    }

    /**
     * returns the value formatted as a byte array ready to put into an HL7 message
     * @return
     */
    public byte[] getValue()
    {
        try
        {
            return value.getBytes("UTF-8");
        }
        catch(UnsupportedEncodingException ignore)
        {
            return new byte[0];
        }
    }
}
