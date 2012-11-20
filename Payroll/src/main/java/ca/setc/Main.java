package ca.setc;

import ca.setc.configuration.Config;
import ca.setc.soa.ServiceLoader;
import ca.setc.soa.SoaException;
import ca.setc.soa.SoaRegistry;
import ca.setc.service.SoaService;
import ca.setc.soa.SoaSocketListener;
import org.scannotation.ClasspathUrlFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Map;

/**
 * Main class containing main start method
 */
public final class Main {

    private static Logger log = LoggerFactory.getLogger(Main.class);

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
    public static void main(String[] args) throws ClassNotFoundException, IOException, InvocationTargetException, NoSuchMethodException, IllegalAccessException, SoaException {
        try
        {
            URL[] urls = ClasspathUrlFinder.findClassPaths();
            Map<String, SoaService> services;
            services = ServiceLoader.loadServices(urls);

            String teamName = Config.get("team.name");
            String registryIp = Config.get("registry.ip");
            int registryPort = 0;
            int servicePort = 0;
            try
            {
                registryPort = Integer.parseInt(Config.get("registry.port"));
            }
            catch(NumberFormatException e)
            {
                log.error("Could not parse soa port", e);
                System.err.println("Could not parse soa port");
                System.exit(-1);
            }

            try
            {
                servicePort = Integer.parseInt(Config.get("service.port"));
            }
            catch(NumberFormatException e)
            {
                log.error("Could not parse service port", e);
                System.err.println("Could not parse service port");
                System.exit(-1);
            }

            String serviceIp = Config.get("registry.ip");

            SoaRegistry soa = null;
            soa = new SoaRegistry(registryIp, registryPort);

            Integer teamId = soa.registerTeam(teamName);

            log.info("Team Id: {}", teamId);

            try
            {
                soa.publishService(teamName, teamId, serviceIp, servicePort, services.get("PAYROLL"));
            }
            catch(SoaException ex)
            {
                if(!ex.getErrorMessage().equals("Team '"+teamName+"' (ID : "+teamId+") has already published service PAYROLL"))
                {
                    System.out.println(ex.getMessage());
                    throw ex;
                }
            }

            ServerSocket serverSocket = null;
            boolean listening = true;

            try {
                serverSocket = new ServerSocket(servicePort);
            } catch (IOException e) {
                log.error("Could not listen on port: " + servicePort);
                System.err.println("Could not listen on port: " + servicePort);
                System.exit(-1);
            }
            catch(NumberFormatException e)
            {
                log.error("Could not parse service port: " + servicePort);
                System.err.println("Could not parse service port: " + servicePort);
                System.exit(-1);
            }

            while (listening)
            {
                new SoaSocketListener(serverSocket.accept()).start();
            }

            serverSocket.close();
        }
        catch(Exception ex)
        {
            log.error("Unexpected error occurred.", ex);
            System.err.println("Unexpected error occurred, see log for details. Shutting Down.");
            System.exit(-1);
        }
    }
}