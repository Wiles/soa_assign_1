package ca.setc.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * Loads and contains global config values
 */
public final class Config {

    private static Logger log = LoggerFactory.getLogger(Config.class);

    private static Properties properties;

    private static List<String> requiredFields = getRequiredFields();

    private static List<String> getRequiredFields()
    {
        List<String> fields = new ArrayList<String>();

        fields.add("team.name");
        fields.add("team.members");
        fields.add("service.port");
        fields.add("service.ip");
        fields.add("registry.port");
        fields.add("registry.ip");
        fields.add("service.publish.port");
        fields.add("soa.log");
        fields.add("Tag");
        fields.add("service");
        fields.add("ok");
        fields.add("not-ok");

        return Collections.unmodifiableList(fields);
    }

    private Config(){}

    /**
     * Get a config value by key
     * @param key key
     * @return config value or null
     */
    public static String get(String key)
    {
        if(properties == null)
        {
            loadProperties();
        }
        return (String)properties.get(key);
    }

    private static void loadProperties()
    {
        properties = new Properties();
        InputStream is = null;
        try
        {
            is = new FileInputStream(new File("./config.xml"));
            properties.loadFromXML(is);
            for(String field : requiredFields)
            {
                if(!properties.containsKey(field))
                {
                    log.error("Configuration file is missing required field: " + field);
                    System.exit(-1);
                }
            }
        }
        catch(IOException ex)
        {
            log.error(ex.getMessage(), ex);
            System.exit(-1);
        }
        finally
        {
            if(is != null)
            {
                try{is.close();}catch(IOException ignore){}
            }
        }
    }
}
