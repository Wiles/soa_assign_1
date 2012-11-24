package ca.setc.soa;

import ca.setc.configuration.Config;
import ca.setc.hl7.Message;
import ca.setc.hl7.Segment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class SoaLogger {

    private static Logger log = LoggerFactory.getLogger(SoaLogger.class);
    private static File logFile = new File(Config.get("soa.log"));
    private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SoaLogger(){}

    public static void header()
    {
        synchronized (logFile){
            PrintWriter pw = null;
            try
            {
                pw = new PrintWriter(new FileOutputStream(logFile, true),true);
                pw.println(String.format("%s %s", date(), "======================================================="));
                pw.println(String.format("%s Team    : Blotto (%s)",
                        date(),
                        Config.get("team.members")));
                pw.println(String.format("%s Tag-Name: %s", date(), Config.get("Tag")));
                pw.println(String.format("%s Service : %s", date(), Config.get("service")));
                pw.println(String.format("%s %s", date(), "======================================================="));
            }
            catch(Exception ex)
            {
                log.error("Failed to log", ex);
            }
            finally
            {
                if(pw != null)
                {
                    pw.close();
                }
            }
        }
    }

    public static void sentServiceRequest(Message sent, Message response)
    {
        synchronized (logFile){
            PrintWriter pw = null;
            try
            {
                pw = new PrintWriter(new FileOutputStream(logFile, true),true);
                pw.println(String.format("%s %s", date(), "---"));
                pw.println(String.format("%s %s", date(), "Calling SOA-Registry with message :"));
                for(Segment s : sent.getSegments())
                {
                    pw.println(String.format("%s   >> %s", date(), new String(s.toHl7())));
                }
                pw.println(String.format("%s %s", date(), "  >> Response from SOA-Registry :"));
                for(Segment s : response.getSegments())
                {
                    pw.println(String.format("%s      >> %s", date(), new String(s.toHl7())));
                }
            }
            catch(Exception ex)
            {
                log.error("Failed to log", ex);
            }
            finally
            {
                if(pw != null)
                {
                    pw.close();
                }
            }
        }
    }

    public static void receivedRequest(Message received)
    {
        synchronized (logFile){
            PrintWriter pw = null;
            try
            {
                pw = new PrintWriter(new FileOutputStream(logFile, true),true);
                pw.println(String.format("%s %s", date(), "---"));
                pw.println(String.format("%s %s", date(), "Receiving service request :"));
                for(Segment s : received.getSegments())
                {
                    pw.println(String.format("%s   >> %s", date(), new String(s.toHl7())));
                }
            }
            catch(Exception ex)
            {
                log.error("Failed to log", ex);
            }
            finally
            {
                if(pw != null)
                {
                    pw.close();
                }
            }
        }
    }

    public static void respond(Message response)
    {
        synchronized (logFile){
            PrintWriter pw = null;
            try
            {
                pw = new PrintWriter(new FileOutputStream(logFile, true),true);
                pw.println(String.format("%s %s", date(), "---"));
                pw.println(String.format("%s %s", date(), "Responding to service request :"));
                for(Segment s : response.getSegments())
                {
                    pw.println(String.format("%s   >> %s", date(), new String(s.toHl7())));
                }
            }
            catch(Exception ex)
            {
                log.error("Failed to log", ex);
            }
            finally
            {
                if(pw != null)
                {
                    pw.close();
                }
            }
        }
    }

    private static String date()
    {
        synchronized (format)
        {
            return format.format(new Date());
        }
    }
}
