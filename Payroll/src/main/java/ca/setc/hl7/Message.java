package ca.setc.hl7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Message {

    private static final byte[] b0B = new byte[]{(byte)0x0B};
    private static final byte[] b0D = new byte[]{(byte)0x0D};
    private static final byte[] bEnd = new byte[]{(byte)0x1C, (byte)0x0D, (byte)0x0A};

    List<Segement> segements = new LinkedList<Segement>();

    public void addSegement(Segement segement)
    {
        this.segements.add(segement);
    }

    public Segement getSegement(int index)
    {
        return this.segements.get(index);
    }

    public byte[] toHl7()
    {

        ByteArrayOutputStream wr = new ByteArrayOutputStream();
        try
        {
            wr.write(b0B);
            for(Segement segement : segements)
            {
                wr.write(segement.toHl7());
                wr.write(b0D);
            }
            wr.write(bEnd);

        }
        catch(IOException ignore)
        {
            //ignore
        }
        return wr.toByteArray();
    }
}
