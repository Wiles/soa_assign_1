package ca.setc.hl7;

import ca.setc.service.SoaParameter;
import ca.setc.soa.SoaException;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceRequest {

    private String method;
    private Map<String, SoaParameter> params = new LinkedHashMap<String, SoaParameter>();

    private String team;
    private int teamId;


    public ServiceRequest(Message message) throws SoaException {
        try
        {
            method = message.get(1).get(2).get();
            team = message.get(0).get(2).get();
            teamId = Integer.parseInt(message.get(0).get(3).get());
            for(int i = 2; i < message.count(); ++i)
            {
                if("ARG".equals(message.get(i).get(0).get()))
                {
                    Segment seg = message.get(i);
                    SoaParameter param = new SoaParameter(seg.get(2).get(), seg.get(3).get(), seg.get(5).get());
                    params.put(message.get(i).get(1).get(), param);
                }
            }
        }
        catch (Exception e)
        {
            throw new SoaException(e);
        }
    }

    public String getMethod()
    {
        return this.method;
    }

    public Map<String, SoaParameter> getParameters()
    {
        return params;
    }

    public String getTeam() {
        return team;
    }

    public int getTeamId()
    {
        return teamId;
    }
}
