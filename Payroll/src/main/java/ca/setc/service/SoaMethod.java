package ca.setc.service;

import ca.setc.annotations.ParameterAnno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a SOA Service Method
 */
public class SoaMethod {

    private String name;
    private String methodName;
    private List<SoaParameter> parameters = new LinkedList<SoaParameter>();
    private String[] returnDescriptions;
    private Class<?> returnType;

    /**
     * Constructor
     * @param name
     * @param method
     * @param returns
     */
    public SoaMethod(String name, Method method, String[] returns) {
        this.name = name;
        this.methodName = method.getName();
        returnType = method.getReturnType();
        this.returnDescriptions = returns.clone();
        final Annotation[][] paramAnnotations = method.getParameterAnnotations();
        final Class[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramAnnotations.length; i++) {
            for (Annotation a : paramAnnotations[i]) {
                if (a instanceof ParameterAnno) {
                    ParameterAnno b = (ParameterAnno) a;
                    parameters.add(new SoaParameter(b.name(), b.required(), paramTypes[i]));
                }
            }
        }
    }

    /**
     * returns the name of the method
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * returns the return type of the method
     * @return
     */
    public Class<?> getReturnType()
    {
        return returnType;
    }

    /**
     * returns the name of the underlying method
     * @return
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * return the SOAParameters for the method
     * @return
     */
    public List<SoaParameter> getParameters() {
        return parameters;
    }

    /**
     * returns an array of the parameter types in order
     * @return
     */
    public Class<?>[] getTypes() {
        List<Class<?>> types = new LinkedList<Class<?>>();

        for (SoaParameter param : parameters) {
            types.add(param.getType());
        }
        return types.toArray(new Class<?>[types.size()]);
    }

    /**
     * the return values descriptions
     * @return
     */
    public String[] getReturnDescriptions()
    {
        return returnDescriptions.clone()   ;
    }

}
