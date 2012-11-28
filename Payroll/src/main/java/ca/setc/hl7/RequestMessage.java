package ca.setc.hl7;

public class RequestMessage extends Message {

    public String getMethod()
    {
        return get(1).get(2).get();
    }

    public String getTeam()
    {
        return get(0).get(2).get();
    }

    public int getTeamId()
    {
        return Integer.parseInt(get(0).get(3).get());
    }
}
