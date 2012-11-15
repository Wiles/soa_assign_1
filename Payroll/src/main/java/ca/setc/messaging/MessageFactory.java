package ca.setc.messaging;

import ca.setc.Main;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaParameter;
import ca.setc.service.SoaService;

import java.util.ArrayList;
import java.util.List;

public class MessageFactory {

    byte OA = (byte)0x0A;
    byte OB = (byte)0x0B;
    byte OD = (byte)0x0D;
    byte IC = (byte)0x1C;

    public Byte[] registerService(SoaService service)
    {
        List<Byte> message = new ArrayList<Byte>();
        SoaMethod method = service.getMethods().get(0);
        message.add(OB);
        message.addAll(toByteList(String.format(
                "DRC|PUB-SERVICE|%s|%d|",
                Main.teamName,
                Main.teamId)));

        message.add(OD);
        message.addAll(toByteList(String.format(
                "SRV|%s|%s|%d|%d|%d|%s|",
                service.getName(),
                method.getName(),
                service.getSecurityLevel(),
                method.getParameters().size(),
                method.getReturnDescriptions().length,
                service.getDescription()
        )));
        message.add(OD);
        List<SoaParameter> params = method.getParameters();

        for(int i = 0; i < params.size(); ++i)
        {
            SoaParameter param = params.get(i);
            message.addAll(toByteList(String.format(
                    "ARG|%d|%s|%s|%s|",
                    i + 1,
                    param.getName(),
                    prettyTypeName(param.getType()),
                    param.isRequired()?"mandatory":"optional"
            )));
            message.add(OD);
        }

        String[] returns = method.getReturnDescriptions();

        for(int i = 0; i < returns.length; ++i)
        {
            String returnMessage = returns[i];
            message.addAll(toByteList(String.format(
                    "RSP|%d|%s|%s||",
                    i + 1,
                    returnMessage,
                    prettyTypeName(method.getReturnType())
            )));
            message.add(OD);
        }
        message.addAll(toByteList(String.format(
                "MCH|%s|%s|",
                Main.ip,
                Main.port)));
        
        message.add(OD);
        message.add(IC);
        message.add(OD);
        message.add(OA);
        return message.toArray(new Byte[message.size()]);
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

    private List<Byte> toByteList(String message)
    {
        byte[] bytea = message.getBytes();
        List<Byte> byteb = new ArrayList<Byte>();
        for(int i = 0; i < bytea.length; ++i)
        {
            byteb.add(bytea[i]);
        }
        return byteb;
    }
}
