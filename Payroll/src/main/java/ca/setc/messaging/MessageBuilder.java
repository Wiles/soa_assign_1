package ca.setc.messaging;

import ca.setc.Main;
import ca.setc.hl7.Field;
import ca.setc.hl7.Message;
import ca.setc.hl7.Segment;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaParameter;
import ca.setc.service.SoaService;

import java.util.List;

/**
 * Builds HL7 messages
 */
public class MessageBuilder {

    /**
     * Creates a publish service message based on the given service
     * @param service
     * @return
     */
    public Message publishService(SoaService service)
    {
        SoaMethod method = service.getMethods().get(0);

        Message message = new Message();

        Segment segment = new Segment();
        segment.addField(new Field("DRC"));
        segment.addField(new Field("PUB-SERVICE"));
        segment.addField(new Field(Main.TEAM_NAME));
        segment.addField(new Field(Main.TEAM_ID));
        message.addSegment(segment);

        segment = new Segment();
        segment.addField(new Field("SRV"));
        segment.addField(new Field(service.getName()));
        segment.addField(new Field(method.getName()));
        segment.addField(new Field(service.getSecurityLevel()));
        segment.addField(new Field(method.getParameters().size()));
        segment.addField(new Field(method.getReturnDescriptions().length));
        segment.addField(new Field(service.getDescription()));
        message.addSegment(segment);

        List<SoaParameter> params = method.getParameters();

        for(int i = 0; i < params.size(); ++i)
        {
            SoaParameter param = params.get(i);
            segment = new Segment();
            segment.addField(new Field("ARG"));
            segment.addField(new Field(i + 1));
            segment.addField(new Field(param.getName()));
            segment.addField(new Field(prettyTypeName(param.getType())));
            segment.addField(new Field(param.isRequired()?"mandatory":"optional"));
            message.addSegment(segment);
        }

        String[] returns = method.getReturnDescriptions();

        for(int i = 0; i < returns.length; ++i)
        {
            String returnMessage = returns[i];

            segment = new Segment();
            segment.addField(new Field("RSP"));
            segment.addField(new Field(i + 1));
            segment.addField(new Field(returnMessage));
            segment.addField(new Field(prettyTypeName(method.getReturnType())));
            message.addSegment(segment);
        }

        segment = new Segment();
        segment.addField(new Field("MCH"));
        segment.addField(new Field(Main.IP));
        segment.addField(new Field(Main.PORT));
        message.addSegment(segment);

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

        segment.addField(new Field("DRC"));
        segment.addField(new Field("REG-TEAM"));
        segment.addField(new Field(""));
        segment.addField(new Field(""));

        message.addSegment(segment);

        segment = new Segment();

        segment.addField(new Field("INF"));
        segment.addField(new Field(teamName));
        segment.addField(new Field(""));
        segment.addField(new Field(""));
        message.addSegment(segment);

        return message;

    }


    /**
     * Creates a unregister team message
     * @param teamName
     * @return
     */
    public Message unregisterTeam(String teamName, int teamId)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.addField(new Field("DRC"));
        segment.addField(new Field("UNREG-TEAM"));
        segment.addField(new Field(teamName));
        segment.addField(new Field(teamId));
        message.addSegment(segment);

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

        segment.addField(new Field("DRC"));
        segment.addField(new Field("QUERY-TEAM"));
        segment.addField(new Field(localTeam));
        segment.addField(new Field(localId));
        message.addSegment(segment);

        segment = new Segment();

        segment.addField(new Field("INF"));
        segment.addField(new Field(queryTeam));
        segment.addField(new Field(queryId));
        segment.addField(new Field(serviceName));
        message.addSegment(segment);

        return message;
    }

    public Message queryService(String teamName, int teamId, String serviceName)
    {
        Message message = new Message();

        Segment segment = new Segment();

        segment.addField(new Field("DRC"));
        segment.addField(new Field("QUERY-SERVICE"));
        segment.addField(new Field(teamName));
        segment.addField(new Field(teamId));
        message.addSegment(segment);

        segment = new Segment();

        segment.addField(new Field("SRV"));
        segment.addField(new Field(serviceName));
        segment.addField(new Field(""));
        segment.addField(new Field(""));
        segment.addField(new Field(""));
        segment.addField(new Field(""));
        segment.addField(new Field(""));
        message.addSegment(segment);

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