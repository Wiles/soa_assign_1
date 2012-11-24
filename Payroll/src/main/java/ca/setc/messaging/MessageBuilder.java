package ca.setc.messaging;

import ca.setc.hl7.Field;
import ca.setc.hl7.Message;
import ca.setc.hl7.Segment;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaParameter;
import ca.setc.service.SoaService;
import ca.setc.soa.SoaException;

import java.util.List;

/**
 * Builds HL7 messages
 */
public class MessageBuilder {

    private static final String DRC = "DRC";

    /**
     * Creates a publish service message based on the given service
     * @param teamName
     * @param teamId
     * @param ip
     * @param service
     *
     * @return
     */
    public Message publishService(String teamName, int teamId, String ip, int port, SoaService service)
    {
        SoaMethod method = service.getMethods().get(0);

        Message message = new Message();

        Segment segment = new Segment();
        segment.add(new Field(DRC));
        segment.add(new Field("PUB-SERVICE"));
        segment.add(new Field(teamName));
        segment.add(new Field(teamId));
        message.add(segment);

        segment = new Segment();
        segment.add(new Field("SRV"));
        segment.add(new Field(service.getName()));
        segment.add(new Field(method.getName()));
        segment.add(new Field(service.getSecurityLevel()));
        segment.add(new Field(method.getParameters().size()));
        segment.add(new Field(method.getReturnDescriptions().length));
        segment.add(new Field(service.getDescription()));
        message.add(segment);

        List<SoaParameter> params = method.getParameters();

        for(int i = 0; i < params.size(); ++i)
        {
            SoaParameter param = params.get(i);
            segment = new Segment();
            segment.add(new Field("ARG"));
            segment.add(new Field(i + 1));
            segment.add(new Field(param.getName()));
            segment.add(new Field(prettyTypeName(param.getType())));
            segment.add(new Field(param.isRequired() ? "mandatory" : "optional"));
            message.add(segment);
        }

        String[] returns = method.getReturnDescriptions();

        for(int i = 0; i < returns.length; ++i)
        {
            String returnMessage = returns[i];

            segment = new Segment();
            segment.add(new Field("RSP"));
            segment.add(new Field(i + 1));
            segment.add(new Field(returnMessage));
            segment.add(new Field(prettyTypeName(method.getReturnType())));
            message.add(segment);
        }

        segment = new Segment();
        segment.add(new Field("MCH"));
        segment.add(new Field(ip));
        segment.add(new Field(port));
        message.add(segment);

        return message;
    }

    /**
     * Creates a register team message
     * @param teamName
     * @return
     */
    public Message registerTeam(String teamName)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.add(new Field(DRC));
        segment.add(new Field("REG-TEAM"));
        segment.add(new Field(""));
        segment.add(new Field(""));

        message.add(segment);

        segment = new Segment();

        segment.add(new Field("INF"));
        segment.add(new Field(teamName));
        segment.add(new Field(""));
        segment.add(new Field(""));
        message.add(segment);

        return message;
    }


    /**
     * Creates a unregister team message
     * @param teamName
     * @param  teamId
     * @return
     */
    public Message unregisterTeam(String teamName, int teamId)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.add(new Field(DRC));
        segment.add(new Field("UNREG-TEAM"));
        segment.add(new Field(teamName));
        segment.add(new Field(teamId));
        message.add(segment);

        return message;
    }

    /**
     * Creates a query team message
     * @param localTeam
     * @param localId
     * @param queryTeam
     * @param queryId
     * @param serviceName
     * @return
     */
    public Message queryTeam(String localTeam, int localId, String queryTeam, int queryId, String serviceName)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.add(new Field(DRC));
        segment.add(new Field("QUERY-TEAM"));
        segment.add(new Field(localTeam));
        segment.add(new Field(localId));
        message.add(segment);

        segment = new Segment();

        segment.add(new Field("INF"));
        segment.add(new Field(queryTeam));
        segment.add(new Field(queryId));
        segment.add(new Field(serviceName));
        message.add(segment);

        return message;
    }

    /**
     * Creates a query service message
     * @param teamName
     * @param teamId
     * @param serviceName
     * @return
     */
    public Message queryService(String teamName, int teamId, String serviceName)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.add(new Field(DRC));
        segment.add(new Field("QUERY-SERVICE"));
        segment.add(new Field(teamName));
        segment.add(new Field(teamId));
        message.add(segment);

        segment = new Segment();

        segment.add(new Field("SRV"));
        segment.add(new Field(serviceName));
        segment.add(new Field(""));
        segment.add(new Field(""));
        segment.add(new Field(""));
        segment.add(new Field(""));
        segment.add(new Field(""));
        message.add(segment);

        return message;
    }

    public Message error(SoaException e)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.add(new Field("SOA"));
        segment.add(new Field("NOT-OK"));
        segment.add(new Field(e.getCode()));
        segment.add(new Field(e.getMessage()));
        segment.add(new Field(""));
        message.add(segment);

        return message;
    }

    public Message response(Object answer, String[] returnDescription, Class<?> returnType)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.add(new Field("PUB"));
        segment.add(new Field("OK"));
        segment.add(new Field(""));
        segment.add(new Field(""));
        segment.add(new Field(1));
        message.add(segment);
        Object[] returns;
        if(returnDescription.length > 1)
        {
            returns = (Object[])answer;
        }
        else
        {
            returns = new Object[]{answer};
        }

        for(int i = 0; i < returnDescription.length; ++i)
        {
            segment = new Segment();

            segment.add(new Field("RSP"));
            segment.add(new Field(i + 1));
            segment.add(new Field(returnDescription[i]));
            segment.add(new Field(prettyTypeName(returnType)));
            segment.add(new Field(returns[i].toString()));
            message.add(segment);
        }

        return message;
    }

    private String prettyTypeName(Class<?> type)
    {
        if(type == Double.class || type == Double[].class)
        {
            return "double";
        }
        else if (type == Integer.class || type == Integer[].class)
        {
            return "int";
        }
        else if (type == String.class || type == String[].class)
        {
            return "String";
        }
        else
        {
            return "unknown";
        }
    }
}
