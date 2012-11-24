package ca.setc.soa;

import ca.setc.configuration.Config;
import ca.setc.hl7.Message;
import ca.setc.messaging.MessageBuilder;
import ca.setc.service.SoaService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handles communicate to and from the SoaRegistry
 */
public final class SoaRegistry {

    private String ip;
    private int teamId;
    private String teamName;
    private int port;
    private MessageBuilder mb = new MessageBuilder();

    private static SoaRegistry instance;

    private SoaRegistry(){}

    /**
     * Set the IP of the registry to connect to
     *
     * @param ip of the registry
     */
    public void setIP(String ip)
    {
        this.ip = ip;
    }

    /**
     * Sets the port of the registry to connect to
     *
     * @param port of the registry
     */
    public void setPort(int port)
    {
        this.port = port;
    }

    /**
     * Set the current team name
     *
     * @param teamName team name
     */
    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    /**
     * Get the singleton instance of the SoaRegistry
     *
     * @return singleton instance
     */
    public static SoaRegistry getInstance()
    {
        if(instance == null)
        {
            instance = new SoaRegistry();
        }
        return instance;
    }

    /**
     * Send a register team message
     * @return team id
     * @throws SoaException if a error occurs
     */
    public int registerTeam() throws SoaException {

        return registerTeam(true);
    }

    private int registerTeam(boolean retry) throws SoaException {

        Message response = sendMessage(mb.registerTeam(teamName), retry);
        try
        {
            teamId = Integer.parseInt(response.get(0).get(2).get());
        }
        catch(NumberFormatException ex)
        {
            throw new SoaException(ex);
        }

        return teamId;
    }

    /**
     * Send a publish service message
     * @param ip ip the service is running on
     * @param port the service is running on
     * @param service name of the service
     * @throws SoaException on error
     */
    public void publishService(String ip, int port, SoaService service) throws SoaException
    {
        try
        {
            publishService(ip, port, service, true);
        }
        catch(SoaException ex)
        {
            if(!ex.getErrorMessage().equals("Team '"+teamName+"' (ID : "+teamId+") has already published service " + Config.get("Tag")))
            {
                throw ex;
            }
        }
    }

    private void publishService(String ip, int port, SoaService service, boolean retry) throws SoaException
    {
        sendMessage(mb.publishService(teamName, teamId, ip, port, service), retry);
    }

    /**
     * Check if a team is authorized to use the service.
     *
     * Throws an exception if they are not.
     *
     * @param queryTeam
     * @param queryId
     * @param serviceName
     * @throws SoaException
     */
    public void queryTeam(String queryTeam, int queryId, String serviceName) throws SoaException {
        Message response = sendMessage(mb.queryTeam(teamName, teamId, queryTeam, queryId, serviceName), true);
        if(Config.get("not-ok").equals(response.get(0).get(1)))
        {
            throw new SoaException(Integer.parseInt(response.get(0).get(2).get()), response.get(0).get(3).get());
        }
    }

    private synchronized Message sendMessage(Message message, boolean retry) throws SoaException {
        Socket sock = null;
        OutputStream writer = null;
        BufferedReader reader = null;
        try
        {
            sock = new Socket(ip, port);

            writer = sock.getOutputStream();
            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));

            writer.write(message.toHl7());
            writer.flush();
            int response;
            StringBuilder sb = new StringBuilder();
            while((response = reader.read()) != -1)
            {
                sb.append((char)response);
            }

            Message responseMessage = new Message(sb.toString().getBytes("UTF-8"));
            SoaLogger.sentServiceRequest(message, responseMessage);

            if(responseMessage.get(0).get(1).get().equals(Config.get("not-ok")))
            {
                int codeNumber = Integer.parseInt(responseMessage.get(0).get(2).get());
                String error = responseMessage.get(0).get(3).get();
                if(retry &&
                   error.equals("Team '" + teamName + "' (ID : " + teamId + ") is not registered"))
                {
                    registerTeam(false);
                    publishService(Config.get("registry.ip"), Integer.parseInt(Config.get("service.publish.port")), ServiceLoader.getService(Config.get("Tag")), false);
                }

                throw new SoaException(codeNumber, error);
            }

            return responseMessage;
        }
        catch(SoaException e)
        {
            throw e;
        }
        catch(Exception e)
        {
            throw new SoaException(e);
        }
        finally
        {
            if(sock != null)
            {
                try
                {
                    sock.close();
                }
                catch(IOException ignore)
                {
                    //ignore
                }
            }
            if(writer != null)
            {
                try
                {
                    writer.close();
                }
                catch(IOException ignore)
                {
                    //ignore
                }
            }
            if(reader != null)
            {
                try
                {
                    reader.close();
                }
                catch(IOException ignore)
                {
                    //ignore
                }
            }
        }
    }
}
