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
     * Constructor for byte array values
     */
    public Field(byte[] bytes)
    {
        try
        {
            this.value = new String(bytes, "UTF-8");
        }
        catch(UnsupportedEncodingException ex)
        {
            this.value = "";
        }
    }


    /**
     * Constructor for Integer values
     * @param value integer value
     */
    public Field(int value)
    {
        this.value = Integer.toString(value);
    }

    /**
     * get the value
     * @return the valure
     */
    public String get()
    {
        return this.value;
    }

    /**
     * returns the value formatted as a byte array ready to put into an HL7 message
     * @return values in bytes
     */
    public byte[] getBytes()
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
