package ca.setc.soa;

import ca.setc.configuration.Config;

public class KeepAlive extends Thread {

    private static final long SLEEP_DURATION = 60 * 1000;

    private SoaRegistry soa = SoaRegistry.getInstance();

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                soa.registerTeam();
                soa.publishService(Config.get("registry.ip"), Integer.parseInt(Config.get("service.publish.port")), ServiceLoader.getService(Config.get("Tag")));
            }
            catch(Exception ignore)
            {
                //ignore
            }
            finally
            {
                try {
                    Thread.sleep(SLEEP_DURATION);
                } catch (InterruptedException ignore) {
                    //ignore
                }
            }
        }
    }

}
