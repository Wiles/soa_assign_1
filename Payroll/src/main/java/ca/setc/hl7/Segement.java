package ca.setc.hl7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Segement {

    private static final byte[] bPipe = new byte[]{(byte)0x7C};
    List<Field> fields = new LinkedList<Field>();

    public void addField(Field field)
    {
        this.fields.add(field);
    }

    public Field getField(int index)
    {
       return this.fields.get(index);
    }

    public byte[] toHl7()
    {
        ByteArrayOutputStream wr = new ByteArrayOutputStream();
        for(Field field : fields)
        {
            try
            {
                wr.write(field.getValue());
                wr.write(bPipe);
            }
            catch(IOException ignore)
            {
                //ignore
            }
        }
        return wr.toByteArray();
    }
}
