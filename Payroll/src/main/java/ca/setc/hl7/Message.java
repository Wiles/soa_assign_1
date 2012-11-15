package ca.setc.hl7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Message {

    private static final byte[] b0B = new byte[]{(byte)0x0B};
    private static final byte[] b0D = new byte[]{(byte)0x0D};
    private static final byte[] bEnd = new byte[]{(byte)0x1C, (byte)0x0D, (byte)0x0A};

    List<Segment> segments = new LinkedList<Segment>();

    public void addSegment(Segment segment)
    {
        this.segments.add(segment);
    }

    public Segment getSegment(int index)
    {
        return this.segments.get(index);
    }

    public byte[] toHl7()
    {

        ByteArrayOutputStream wr = new ByteArrayOutputStream();
        try
        {
            wr.write(b0B);
            for(Segment segment : segments)
            {
                wr.write(segment.toHl7());
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
