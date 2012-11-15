package ca.setc.service;

public class SoaParameter {
    private boolean required;
    private Class<?> type;
    private String name;

    public SoaParameter(String name, boolean required, Class<?> type) {
        this.required = required;
        this.type = type;
        this.name = name;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isRequired() {
        return required;
    }

    public String getName() {
        return name;
    }
}
