package ca.setc.messaging;

import ca.setc.Main;
import ca.setc.hl7.Field;
import ca.setc.hl7.Message;
import ca.setc.hl7.Segement;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaParameter;
import ca.setc.service.SoaService;

import java.util.List;

public class MessageFactory {

    byte OA = (byte)0x0A;
    byte OB = (byte)0x0B;
    byte OD = (byte)0x0D;
    byte IC = (byte)0x1C;

    public Message registerService(SoaService service)
    {
        Message message = new Message();
        SoaMethod method = service.getMethods().get(0);
        Segement segement = new Segement();
        segement.addField(new Field("DRC"));
        segement.addField(new Field("PUB-SERVICE"));
        segement.addField(new Field(Main.teamName));
        segement.addField(new Field(Main.teamId));
        message.addSegement(segement);
        segement = new Segement();
        segement.addField(new Field("SRV"));
        segement.addField(new Field(service.getName()));
        segement.addField(new Field(method.getName()));
        segement.addField(new Field(service.getSecurityLevel()));
        segement.addField(new Field(method.getParameters().size()));
        segement.addField(new Field(method.getReturnDescriptions().length));
        segement.addField(new Field(service.getDescription()));
        message.addSegement(segement);

        List<SoaParameter> params = method.getParameters();

        for(int i = 0; i < params.size(); ++i)
        {
            SoaParameter param = params.get(i);
            segement = new Segement();
            segement.addField(new Field("ARG"));
            segement.addField(new Field(i + i));
            segement.addField(new Field(param.getName()));
            segement.addField(new Field(prettyTypeName(param.getType())));
            segement.addField(new Field(param.isRequired()?"mandatory":"optional"));
            message.addSegement(segement);
        }

        String[] returns = method.getReturnDescriptions();

        for(int i = 0; i < returns.length; ++i)
        {
            String returnMessage = returns[i];

            SoaParameter param = params.get(i);
            segement = new Segement();
            segement.addField(new Field("RSP"));
            segement.addField(new Field(i + i));
            segement.addField(new Field(returnMessage));
            segement.addField(new Field(prettyTypeName(method.getReturnType())));
            message.addSegement(segement);
        }



        segement = new Segement();
        segement.addField(new Field("MCH"));
        segement.addField(new Field(Main.ip));
        segement.addField(new Field(Main.port));
        message.addSegement(segement);
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
