package ca.setc;


import ca.setc.service.SoaService;
import ca.setc.services.CarLoan;
import org.junit.Assert;
import org.junit.Test;
import org.scannotation.ClasspathUrlFinder;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

public class ServiceLoaderTest {
    @Test
    public void loadServices_shouldFindService_whenNormalClassPath() throws ClassNotFoundException, IOException
    {
        URL[] urls = new URL[]{ClasspathUrlFinder.findClassBase(CarLoan.class)};

        Map<String, SoaService> services = ServiceLoader.loadServices(urls);

        Assert.assertEquals(1, services.size());
    }
}
