package ca.setc.soa;

import ca.setc.hl7.Message;
import ca.setc.hl7.ServiceRequest;
import ca.setc.messaging.MessageBuilder;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class SoaSocketListener extends Thread {

    private Logger log = LoggerFactory.getLogger(SoaSocketListener.class);

    private Socket socket = null;
    private MessageBuilder mb = new MessageBuilder();

    public SoaSocketListener(Socket socket)
    {
        super("SoaSocketListener");
        this.socket = socket;
    }


    @Override
    public void run() {
        OutputStream writer = null;
        BufferedReader reader = null;
        try {
            writer = socket.getOutputStream();
            reader = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            int response;
            StringBuilder sb = new StringBuilder();
            while((response = reader.read()) != 0x1c)
            {
                sb.append((char) response);
            }
            sb.append((char)0x1c);
            sb.append((char)0x0d);
            sb.append((char)0x0a);


            Message request = new Message(sb.toString().getBytes("UTF-8"));

            SoaLogger.receivedRequest(request);

            ServiceRequest sr = new ServiceRequest(request);


            SoaService service = ServiceLoader.getService("PAYROLL");
            SoaMethod method = service.getMethod(sr.getMethod());

            Object answer = service.execute(sr.getMethod(), sr.getParameters());
            Message responseMessage = mb.response(answer, method.getReturnDescriptions(), method.getReturnType());
            SoaLogger.respond(responseMessage);
            writer.write(responseMessage.toHl7());
            writer.flush();

        } catch (SoaException e) {
            log.error(e.getMessage(), e);
            if(writer != null)
            {
                try
                {
                    writer.write(mb.error(e).toHl7());
                    writer.flush();
                }
                catch(IOException ex)
                {
                    log.error("Could not write response", ex);
                }
            }
        } catch(Exception e)
        {
            log.error(e.getMessage(), e);
            if(writer != null)
            {
                try
                {
                    writer.write(mb.error(new SoaException(e)).toHl7());
                    writer.flush();
                }
                catch(IOException ex)
                {
                    log.error("Could not write response", ex);
                }
            }
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
                    //ignore5
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
