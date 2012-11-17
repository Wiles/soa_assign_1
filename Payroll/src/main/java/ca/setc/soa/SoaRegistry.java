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
    private int port;
    private MessageBuilder mb = new MessageBuilder();

    public SoaRegistry(String ip, int port) throws SoaRegistryException {
        this.ip = ip;
        this.port = port;
    }

    public int registerTeam(String teamName) throws SoaRegistryException {
        int teamId;

        Message response = sendMessage(mb.registerTeam(teamName));
        try
        {
            teamId = Integer.parseInt(response.get(0).get(2).get());
        }
        catch(NumberFormatException ex)
        {
            throw new SoaRegistryException(ex);
        }
        return teamId;
    }

    public void publishService(String teamName, int teamId, String ip, int port, SoaService service) throws SoaRegistryException
    {
        sendMessage(mb.publishService(teamName, teamId, ip, port, service));
    }

    private Message sendMessage(Message message) throws SoaRegistryException {
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

            if(responseMessage.get(0).get(1).get().equals("NOT-OK"))
            {
                throw new SoaRegistryException(
                        Integer.parseInt(responseMessage.get(0).get(2).get()),
                        responseMessage.get(0).get(3).get()
                );
            }

            return responseMessage;
        }
        catch(Exception e)
        {
            throw new SoaRegistryException(e);
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
