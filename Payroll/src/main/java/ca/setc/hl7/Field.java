package ca.setc.hl7;

import java.io.UnsupportedEncodingException;

public class Field {
    private String value;

    public Field(String value)
    {
        this.value = value;
    }

    public Field(int value)
    {
        this.value = Integer.toString(value);
    }

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
