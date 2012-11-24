package ca.setc.service;

/**
 * A SOA service parameter
 */
public class SoaParameter {
    private boolean required;
    private Class<?> type;
    private String name;
    private String value;

    /**
     * Constructor
     * @param name
     * @param required
     * @param type
     */
    public SoaParameter(String name, boolean required, Class<?> type) {
        this.required = required;
        this.type = type;
        this.name = name;
    }

    public SoaParameter(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    /**
     * the type the parameter takes
     * @return
     */
    public Class<?> getType() {
        return type;
    }

    /**
     * If the parameter is mandatory
     * @return
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * get the parameter name
     * @return
     */
    public String getName() {
        return name;
    }

    public String getValue()
    {
        return value;
    }
}
