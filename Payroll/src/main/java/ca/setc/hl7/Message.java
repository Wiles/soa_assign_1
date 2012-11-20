package ca.setc.hl7;

import org.apache.commons.lang.ArrayUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a full HL7 message
 */
public class Message {

    private static final byte[] B_BOM = new byte[]{(byte)0x0B};
    private static final byte[] B_EOS = new byte[]{(byte)0x0D};
    private static final byte[] B_EOM = new byte[]{(byte)0x1C, (byte)0x0D, (byte)0x0A};

    private List<Segment> segments = new LinkedList<Segment>();

    /**
     * Constructor
     */
    public Message(){}

    /**
     * Creates a message based on an hl7 byte array
     * @param raw hl7 message
     */
    public Message(byte[] raw)
    {
        if(raw.length < B_BOM.length + B_EOM.length)
        {
            throw new IllegalArgumentException("raw input is too short");
        }
        if(raw[0] != Message.B_BOM[0])
        {
            throw new IllegalArgumentException("raw does not contain a BOM");
        }

        if( raw[raw.length - 3] != Message.B_EOM[0] ||
                raw[raw.length - 2] != Message.B_EOM[1] ||
                raw[raw.length - 1] != Message.B_EOM[2])
        {
            throw new IllegalArgumentException("End of message is missing");
        }

        byte[] lessRaw = Arrays.copyOfRange(raw, B_BOM.length, raw.length - B_EOM.length);

        List<Byte> segment = new ArrayList<Byte>();

        for(int i = 0; i < lessRaw.length; ++i)
        {
            if(lessRaw[i] == Message.B_EOS[0])
            {
                Segment s = new Segment(ArrayUtils.toPrimitive(segment.toArray(new Byte[segment.size()])));
                add(s);
                segment.clear();
            }
            else
            {
                segment.add(lessRaw[i]);
            }
        }
    }

    /**
     * Adds an HL7 Segment
     * @param segment
     */
    public void add(Segment segment)
    {
        this.segments.add(segment);
    }

    /**
     * Returns the segment at the given index or null if it does not exist
     * @param index
     * @return
     */
    public Segment get(int index)
    {
        return this.segments.get(index);
    }

    /**
     * Returns the message in HL7 format
     * @return
     */
    public byte[] toHl7()
    {

        ByteArrayOutputStream wr = new ByteArrayOutputStream();
        try
        {
            wr.write(B_BOM);
            for(Segment segment : segments)
            {
                wr.write(segment.toHl7());
                wr.write(B_EOS);
            }
            wr.write(B_EOM);

        }
        catch(IOException ignore)
        {
            //ignore
        }
        return wr.toByteArray();
    }

    /**
     * number of segments in the message
     * @return
     */
    public int count()
    {
        return segments.size();
    }

    public Segment[] getSegments()
    {
        return segments.toArray(new Segment[segments.size()]);
    }
}
