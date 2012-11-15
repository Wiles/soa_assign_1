package ca.setc;


import ca.setc.service.SoaService;
import org.junit.Assert;
import org.junit.Test;
import org.scannotation.ClasspathUrlFinder;

import java.io.IOException;
import java.util.Map;

public class ServiceLoaderTest {
    @Test
    public void loadServices_shouldFindService_whenNormalClassPath() throws ClassNotFoundException, IOException
    {
        Map<String, SoaService> services = ServiceLoader.LoadServices(ClasspathUrlFinder.findClassPaths());

        Assert.assertEquals(2, services.size());
    }
}
