package ca.setc.hl7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a segment in an HL7 message
 */
public class Segment {

    private static final byte[] B_PIPE = new byte[]{(byte)0x7C};
    private List<Field> fields = new LinkedList<Field>();

    /**
     * Adds a field to onto the end of the message
     * @param field
     */
    public void addField(Field field)
    {
        this.fields.add(field);
    }

    /**
     * Returns the field from the given index or null if it does not exist
     * @param index
     * @return
     */
    public Field getField(int index)
    {
       return this.fields.get(index);
    }

    /**
     * returns the segment ready to be put into an HL7 message
     * @return
     */
    public byte[] toHl7()
    {
        ByteArrayOutputStream wr = new ByteArrayOutputStream();
        for(Field field : fields)
        {
            try
            {
                wr.write(field.getValue());
                wr.write(B_PIPE);
            }
            catch(IOException ignore)
            {
                //ignore
            }
        }
        return wr.toByteArray();
    }
}
