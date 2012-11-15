package ca.setc.service;

import ca.setc.annotations.ParameterAnno;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class SoaMethod {

    private String name;
    private String methodName;
    private List<SoaParameter> parameters = new LinkedList<SoaParameter>();
    private String[] returnDescriptions;
    private Class<?> returnType;

    public SoaMethod(String name, Method method, String[] returns) {
        this.name = name;
        this.methodName = method.getName();
        returnType = method.getReturnType();
        this.returnDescriptions = returns;
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

    public String getName() {
        return name;
    }

    public Class<?> getReturnType()
    {
        return returnType;
    }

    public String getMethodName() {
        return methodName;
    }

    public List<SoaParameter> getParameters() {
        return parameters;
    }

    public Class<?>[] getTypes() {
        List<Class<?>> types = new LinkedList<Class<?>>();

        for (SoaParameter param : parameters) {
            types.add(param.getType());
        }
        return types.toArray(new Class<?>[types.size()]);
    }

    public String[] getReturnDescriptions()
    {
        return returnDescriptions;
    }

}
