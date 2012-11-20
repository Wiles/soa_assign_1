package ca.setc.soa;

import ca.setc.hl7.Message;
import ca.setc.messaging.MessageBuilder;
import ca.setc.service.SoaService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SoaRegistry {

    private String ip;
    private int teamId;
    private String teamName;
    private int port;
    private MessageBuilder mb = new MessageBuilder();

    private static SoaRegistry instance;

    private SoaRegistry(){}

    public void setIP(String ip)
    {
        this.ip = ip;
    }

    public void setPort(int port)
    {
        this.port = port;
    }

    public void setTeamName(String teamName)
    {
        this.teamName = teamName;
    }

    public static SoaRegistry getInstance()
    {
        if(instance == null)
        {
            instance = new SoaRegistry();
        }
        return instance;
    }

    public int registerTeam() throws SoaException {

        Message response = sendMessage(mb.registerTeam(teamName));
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

    public void publishService(String ip, int port, SoaService service) throws SoaException
    {
        sendMessage(mb.publishService(teamName, teamId, ip, port, service));
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
        Message response = sendMessage(mb.queryTeam(teamName, teamId, queryTeam, queryId, serviceName));
        if("NOT-OK".equals(response.get(0).get(1)))
        {
            throw new SoaException(Integer.parseInt(response.get(0).get(2).get()), response.get(0).get(3).get());
        }
    }

    private synchronized Message sendMessage(Message message) throws SoaException {
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
            if(responseMessage.get(0).get(1).get().equals("NOT-OK"))
            {
                throw new SoaException(
                        Integer.parseInt(responseMessage.get(0).get(2).get()),
                        responseMessage.get(0).get(3).get()
                );
            }

            return responseMessage;
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
