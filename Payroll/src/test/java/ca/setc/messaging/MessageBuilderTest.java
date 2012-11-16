package ca.setc.messaging;

import ca.setc.ServiceLoader;
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
                124,67,65,82,45,76,79,65,78,124,99,97
                ,114,76,111,97,110,67,97,108,99,117,
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
        Message message = mb.publishService(services.get("CAR-LOAN"));
        Assert.assertArrayEquals(expected, message.toHl7());
    }

}
