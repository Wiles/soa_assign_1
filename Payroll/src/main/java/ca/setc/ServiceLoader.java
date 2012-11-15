package ca.setc;

import ca.setc.annotations.ServiceAnno;
import ca.setc.service.SoaService;
import org.scannotation.AnnotationDB;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class ServiceLoader {

    private ServiceLoader(){}

    public static Map<String, SoaService> loadServices(URL[] classpath) throws IOException, ClassNotFoundException {
        Map<String, SoaService> services = new HashMap<String, SoaService>();
        AnnotationDB db = new AnnotationDB();
        db.scanArchives(classpath);
        Set<String> entityClasses = db.getAnnotationIndex().get(ServiceAnno.class.getName());

        for (String name : entityClasses) {
            SoaService soaService = new SoaService(name);
            services.put(soaService.getName(), soaService);
        }
        return services;
    }
}
