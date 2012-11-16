package ca.setc.hl7;

import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a segment in an HL7 message
 */
public class Segment {

    private static final byte[] B_PIPE = new byte[]{(byte)0x7C};
    private List<Field> fields = new LinkedList<Field>();


    /**
     * Constructor
     */
    public Segment(){}


    public Segment(byte[] bytes)
    {
        List<Byte> byteList = Arrays.asList(ArrayUtils.toObject(bytes));
        if(!byteList.contains((Byte)B_PIPE[0]))
        {
            throw new IllegalArgumentException("Segment does not contain any fields");
        }
        else if(byteList.get(byteList.size() - 1) != B_PIPE[0])
        {
            throw new IllegalArgumentException("Segment does not end in a field separator");
        }

        List<Byte> field = new ArrayList<Byte>();

        for(int i = 0; i < byteList.size(); ++i)
        {
            if(byteList.get(i) == B_PIPE[0])
            {
                addField(new Field(ArrayUtils.toPrimitive(field.toArray(new Byte[field.size()]))));
                field.clear();
            }
            else
            {
                field.add(byteList.get(i));
            }
        }
    }


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
