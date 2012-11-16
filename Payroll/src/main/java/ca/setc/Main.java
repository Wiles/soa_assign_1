package ca.setc;

import ca.setc.hl7.Message;
import ca.setc.messaging.MessageBuilder;
import ca.setc.service.SoaService;
import org.scannotation.ClasspathUrlFinder;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

/**
 * Main class containing main start method
 */
public final class Main {
    private static Map<String, SoaService> services;

    public static final String TEAM_NAME = "Blotto";
    public static final Integer TEAM_ID = 1180;
    public static final Integer PORT = 5000;
    public static final String IP = "127.0.0.1";
    public static final String REGISTRY_IP = "localhost";
    public static final Integer REGISTRY_PORT = 3128;

    private Main(){}

    /**
     * Start method
     *
     * @param args command line arguments
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     */
    public static void main(String[] args) throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        URL[] urls = ClasspathUrlFinder.findClassPaths();
        services = ServiceLoader.loadServices(urls);
        MessageBuilder mf = new MessageBuilder();
        Message message = mf.publishService(services.get("PAYROLL"));

        Socket sock = new Socket(REGISTRY_IP, REGISTRY_PORT);

        OutputStream writer = sock.getOutputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        writer.write(message.toHl7());
        writer.flush();
        String response;
        PrintWriter out = new PrintWriter(System.out, true);
        while( (response = reader.readLine()) != null)
        {
            out.println(response);
        }
        reader.close();
        writer.close();
        sock.close();
        SoaService s = services.get("PAYROLL");
        Object o = s.execute("payCheckMaker", new String[]{"HOUR","39","10","0.0","0"});
        out.println(o);
    }
}