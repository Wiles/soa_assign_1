package ca.setc.hl7;

import ca.setc.service.SoaParameter;
import ca.setc.soa.SoaException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a service request
 */
public class ServiceRequest {

    private String method;
    private Map<String, SoaParameter> params = new LinkedHashMap<String, SoaParameter>();

    private String team;
    private int teamId;

    /**
     * Constructor
     * @param message to turn into a request
     * @throws SoaException
     */
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
                    SoaParameter param = new SoaParameter(seg.get(2).get(), seg.get(5).get());
                    params.put(message.get(i).get(1).get(), param);
                }
            }
        }
        catch (Exception e)
        {
            throw new SoaException(e);
        }
    }

    /**
     * get method name
     * @return method name
     */
    public String getMethod()
    {
        return this.method;
    }

    /**
     * get parameters
     * @return parameter map
     */
    public Map<String, SoaParameter> getParameters()
    {
        return params;
    }

    /**
     * Name of requesting team
     * @return team name
     */
    public String getTeam() {
        return team;
    }

    /**
     * id of requesting team
     * @return team id
     */
    public int getTeamId()
    {
        return teamId;
    }
}
