package ca.setc.messaging;

import ca.setc.Main;
import ca.setc.service.SoaMethod;
import ca.setc.service.SoaParameter;
import ca.setc.service.SoaService;

import java.util.ArrayList;
import java.util.List;

public class MessageFactory {

    public Byte[] registerService(SoaService service)
    {
        List<Byte> message = new ArrayList<Byte>();
        SoaMethod method = service.getMethods().get(0);
        message.add((byte)11);
        message.addAll(toByteList(String.format("DRC|PUB-SERVICE|%s|%d|\r\n", Main.teamName, Main.teamId)));
        message.addAll(toByteList(String.format(
                "SRV|%s|%s|%d|%d|%d|%s|\r\n",
                service.getName(),
                method.getName(),
                service.getSecurityLevel(),
                method.getParameters().size(),
                method.getReturnDescriptions().length,
                service.getDescription()
        )));

        List<SoaParameter> params = method.getParameters();

        for(int i = 0; i < params.size(); ++i)
        {
            SoaParameter param = params.get(i);
            message.addAll(toByteList(String.format("ARG|%d|%s|%s|%s|\r\n",
                    i + 1,
                    param.getName(),
                    prettyTypeName(param.getType()),
                    param.isRequired()?"mandatory":"optional"
            )));
        }

        String[] returns = method.getReturnDescriptions();

        for(int i = 0; i < returns.length; ++i)
        {
            String returnMessage = returns[i];
            message.addAll(toByteList(String.format("RSP|%d|%s|%s||\r\n",
                    i + 1,
                    returnMessage,
                    prettyTypeName(method.getReturnType())
            )));
        }
        message.addAll(toByteList(String.format("MCH|%s|%s|\r\n", Main.ip, Main.port)));
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
