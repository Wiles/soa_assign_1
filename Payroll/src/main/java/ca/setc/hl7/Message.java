package ca.setc.hl7;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Message {

    private static final byte[] B_0B = new byte[]{(byte)0x0B};
    private static final byte[] B_0D = new byte[]{(byte)0x0D};
    private static final byte[] B_END = new byte[]{(byte)0x1C, (byte)0x0D, (byte)0x0A};

    private List<Segment> segments = new LinkedList<Segment>();

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
            wr.write(B_0B);
            for(Segment segment : segments)
            {
                wr.write(segment.toHl7());
                wr.write(B_0D);
            }
            wr.write(B_END);

        }
        catch(IOException ignore)
        {
            //ignore
        }
        return wr.toByteArray();
    }
}
