package ca.setc.messaging;

import ca.setc.soa.ServiceLoader;
import ca.setc.hl7.Message;
import ca.setc.service.SoaService;
import ca.setc.services.CarLoan;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.scannotation.ClasspathUrlFinder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class MessageBuilderTest {
    private static Map<String, SoaService> services;
    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException, IOException {
        URL[] urls = new URL[]{ClasspathUrlFinder.findClassBase(CarLoan.class)};

        services = ServiceLoader.loadServices(urls);
    }

    @Test
    public void publishService_shouldCreateMessage_whenValidService()
    {
        byte[] expected = new byte[]{
                11,68,82,67,124,80,85,66,45,83,69,82,
                86,73,67,69,124,66,108,111,116,116,
                111,124,49,49,56,48,124,13,83,82,86,
                124,67,65,82,45,76,79,65,78,124,99,97,
                114,76,111,97,110,67,97,108,99,117,
                108,97,116,111,114,124,49,124,50,124,
                49,124,67,97,114,32,76,111,97,110,32,
                116,104,105,110,103,124,13,65,82,71,
                124,49,124,112,114,105,110,99,105,112,
                97,108,124,100,111,117,98,108,101,124,
                109,97,110,100,97,116,111,114,121,124,
                13,65,82,71,124,50,124,114,97,116,101,
                124,100,111,117,98,108,101,124,109,97,
                110,100,97,116,111,114,121,124,13,82,
                83,80,124,49,124,80,97,121,109,101,110,
                116,124,100,111,117,98,108,101,124,13,
                77,67,72,124,49,50,55,46,48,46,48,46,49,
                124,53,48,48,48,124,13,28,13,10};

        MessageBuilder mb = new MessageBuilder();
        Message message = mb.publishService("Blotto", 1180, "127.0.0.1", 5000, services.get("CAR-LOAN"));
        Assert.assertArrayEquals(expected, message.toHl7());
    }

    @Test
    public void registerTeam_ShouldCreateMessage_whenValidTeamName()
    {
        byte[] expected = new byte[]{
                0x0b, 0x44, 0x52, 0x43, 0x7c, 0x52, 0x45, 0x47,
                0x2d, 0x54, 0x45, 0x41, 0x4d, 0x7c, 0x7c, 0x7c,
                0x0d, 0x49, 0x4e, 0x46, 0x7c, 0x66, 0x6F, 0x6F,
                0x62, 0x61, 0x72, 0x7c, 0x7c, 0x7c, 0x0d, 0x1c,
                0x0d, 0x0a
        };

        MessageBuilder mb = new MessageBuilder();
        Message message = mb.registerTeam("foobar");
        Assert.assertArrayEquals(expected, message.toHl7());
    }

    @Test
    public void unregisterTeam_ShouldCreateMessage_whenValid()
    {
        byte[] expected = new byte[]{
                0x0b, 0x44, 0x52, 0x43, 0x7c, 0x55, 0x4e, 0x52,
                0x45, 0x47, 0x2d, 0x54, 0x45, 0x41, 0x4d, 0x7c,
                0x66, 0x6f, 0x6f, 0x62, 0x61, 0x72, 0x7c, 0x31,
                0x32, 0x33, 0x34, 0x7c, 0x0d, 0x1c, 0x0d, 0x0a
        };

        MessageBuilder mb = new MessageBuilder();
        Message message = mb.unregisterTeam("foobar", 1234);
        Assert.assertArrayEquals(expected, message.toHl7());
    }

    @Test
    public void queryTeam_ShouldCreateMessage_whenValid()
    {
        byte[] expected = new byte[]{
                0x0b, 0x44, 0x52, 0x43, 0x7c, 0x51, 0x55, 0x45,
                0x52, 0x59, 0x2d, 0x54, 0x45, 0x41, 0x4d, 0x7c,
                0x42, 0x6c, 0x6f, 0x74, 0x74, 0x6f, 0x7c, 0x31,
                0x31, 0x38, 0x30, 0x7c, 0x0d, 0x49, 0x4e, 0x46,
                0x7c, 0x53, 0x68, 0x63, 0x61, 0x72, 0x70, 0x6f,
                0x7c, 0x31, 0x31, 0x38, 0x33, 0x7c, 0x43, 0x41,
                0x52, 0x2d, 0x4c, 0x4f, 0x41, 0x4e, 0x7c, 0x0d,
                0x1c, 0x0d, 0x0a
        };

        MessageBuilder mb = new MessageBuilder();
        Message message = mb.queryTeam("Blotto", 1180, "Shcarpo", 1183, "CAR-LOAN");
        Assert.assertArrayEquals(expected, message.toHl7());
    }

    @Test
    public void queryService_ShouldCreateMessage_whenValid()
    {
        byte[] expected = new byte[]{
                0x0b, 0x44, 0x52, 0x43, 0x7c, 0x51, 0x55, 0x45,
                0x52, 0x59, 0x2d, 0x53, 0x45, 0x52, 0x56, 0x49,
                0x43, 0x45, 0x7c, 0x42, 0x6c, 0x6f, 0x74, 0x74,
                0x6f, 0x7c, 0x31, 0x31, 0x38, 0x30, 0x7c, 0x0d,
                0x53, 0x52, 0x56, 0x7c, 0x43, 0x41, 0x52, 0x2d,
                0x4c, 0x4f, 0x41, 0x4e, 0x7c, 0x7c, 0x7c, 0x7c,
                0x7c, 0x7c, 0x0d, 0x1c, 0x0d, 0x0a
        };

        MessageBuilder mb = new MessageBuilder();
        Message message = mb.queryService("Blotto", 1180, "CAR-LOAN");
        Assert.assertArrayEquals(expected, message.toHl7());
    }
}
