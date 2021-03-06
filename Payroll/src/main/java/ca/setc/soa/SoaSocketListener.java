package ca.setc.soa;

import ca.setc.configuration.Config;
import ca.setc.hl7.Message;
import ca.setc.hl7.ServiceRequest;
import ca.setc.messaging.MessageBuilder;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Listens for and handles requests
 */
public class SoaSocketListener extends Thread {

    private Logger log = LoggerFactory.getLogger(SoaSocketListener.class);

    private Socket socket = null;
    private MessageBuilder mb = new MessageBuilder();

    /**
     * Constructor
     * @param socket serversocket to use
     */
    public SoaSocketListener(Socket socket)
    {
        super("SoaSocketListener");
        this.socket = socket;
    }

    @Override
    /**
     * Run the thread
     */
    public void run() {
        OutputStream writer = null;
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            writer = socket.getOutputStream();
            reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            int response;
            while((response = reader.read()) != Message.B_EOM[0])
            {
                sb.append((char) response);
            }

            sb.append(new String(Message.B_EOM));

            Message request = new Message(sb.toString().getBytes("UTF-8"));
            SoaLogger.receivedRequest(request);

            ServiceRequest sr = new ServiceRequest(request);

            SoaRegistry.getInstance().queryTeam(sr.getTeam(), sr.getTeamId(), Config.get("Tag"));

            SoaService service = ServiceLoader.getService(Config.get("Tag"));
            SoaMethod method = service.getMethod(sr.getMethod());

            Object answer = service.execute(sr.getMethod(), sr.getParameters());
            Message responseMessage = mb.response(answer, method.getReturnDescriptions(), method.getReturnType());
            SoaLogger.respond(responseMessage);
            writer.write(responseMessage.toHl7());
            writer.flush();

        }
        catch(Exception e)
        {
            log.error(e.getMessage(), e);
            SoaException root = null;
            if(ExceptionUtils.getRootCause(e) instanceof SoaException)
            {
                root = (SoaException)ExceptionUtils.getRootCause(e);
            }
            else if (e instanceof SoaException)
            {
                root = (SoaException)e;
            }

            if(writer != null)
            {
                try
                {
                    Message m;
                    if(root != null)
                    {
                        m = mb.error(root);
                    }
                    else
                    {
                        m = mb.error(new SoaException(e));
                    }
                    SoaLogger.respond(m);
                    writer.write(m.toHl7());
                    writer.flush();
                }
                catch(IOException ex)
                {
                    log.error("Could not write response", ex);
                }
            }
        }
        catch(Error e)
        {
            log.error(e.getMessage(), e);
        }
        finally
        {
            if(socket != null)
            {
                try
                {
                    socket.close();
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
