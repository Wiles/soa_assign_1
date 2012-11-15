package ca.setc;

import ca.setc.messaging.MessageFactory;
import ca.setc.service.SoaService;
import org.scannotation.ClasspathUrlFinder;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.Map;

public class Main {
    static Map<String, SoaService> services;

    public static String teamName = "Blotto";
    public static int teamId = 1180;
    public static int port = 5000;
    public static String ip = "127.0.0.1";
    public static String registryIp = "localhost";
    public static int registryPort = 3128;

    public static void main(String[] args) throws Exception {
        URL[] urls = ClasspathUrlFinder.findClassPaths();
        services = ServiceLoader.LoadServices(urls);
        MessageFactory mf = new MessageFactory();
        Byte[] l = mf.registerService(services.get("CAR-LOAN"));
        byte[] array = new byte[l.length];
        int i = 0;

        for (Byte current : l) {
            array[i] = current;
            i++;
        }
        System.out.println(new String(array));

        Socket sock = new Socket(registryIp, registryPort);

        PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        writer.println(new String(array));
        String response;
        while( (response= reader.readLine()) != null)
        {
            System.out.println(response);
        }
        reader.close();
        writer.close();
        sock.close();
        SoaService s = services.get("CAR-LOAN");
        Object o = s.execute("carLoanCalculator", new String[]{"10000.0", "5.0"});

        Double[] dd = (Double[]) o;
        for (Double d : dd) {
            System.out.println(d);
        }

        s = services.get("PAYROLL");
        o = s.execute("payCheckMaker", new String[]{"HOUR","39","10","0.0","0"});
        System.out.println((Double)o);
    }
}