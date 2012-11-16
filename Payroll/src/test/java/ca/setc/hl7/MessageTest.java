package ca.setc.hl7;

import ca.setc.messaging.MessageBuilder;
import org.junit.Assert;
import org.junit.Test;

public class MessageTest {
    @Test
    public void ctor_shouldBuildMessage_whenBytes()
    {
        MessageBuilder mb = new MessageBuilder();

        byte[] expected = mb.queryService("Blotto",1180,"CAR-LOAN").toHl7();

        byte[] actual = new Message(expected).toHl7();

        Assert.assertArrayEquals(expected, actual);

    }

}
