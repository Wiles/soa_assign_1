package ca.setc.hl7;

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
        return value.getBytes();
    }
}
