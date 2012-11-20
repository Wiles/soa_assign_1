package ca.setc.soa;

import ca.setc.annotations.ServiceAnno;
import ca.setc.service.SoaService;
import org.scannotation.AnnotationDB;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Loads in services
 */
public final class ServiceLoader {

    private ServiceLoader(){}
    private static Map<String, SoaService> services = new LinkedHashMap<String, SoaService>();

    /**
     * Parses in services based on annotation
     * @param classpath
     * @return the services that can be run
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Map<String, SoaService> loadServices(URL[] classpath) throws IOException, ClassNotFoundException {
        services = new HashMap<String, SoaService>();
        AnnotationDB db = new AnnotationDB();
        db.scanArchives(classpath);
        Set<String> entityClasses = db.getAnnotationIndex().get(ServiceAnno.class.getName());

        for (String name : entityClasses) {
            SoaService soaService = new SoaService(name);
            services.put(soaService.getName(), soaService);
        }

        return services;
    }

    public static SoaService getService(String name)
    {
        return services.get(name);
    }


}
