package ca.setc.service;

import ca.setc.annotations.MethodAnno;
import ca.setc.annotations.ServiceAnno;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class SoaService {

    private String name;
    private Class<?> c;
    private int securityLevel;
    private String description;
    private Map<String, SoaMethod> methods = new HashMap<String, SoaMethod>();

    public SoaService(String className) throws ClassNotFoundException {
        boolean foundService = false;
        c = Class.forName(className);
        Annotation[] anns = c.getAnnotations();
        //Load service annotation
        for (Annotation ann : anns) {
            if (ann instanceof ServiceAnno) {
                foundService = true;
                ServiceAnno service = (ServiceAnno) ann;
                this.name = service.name();
                this.description = service.description();
                this.securityLevel = service.securityLevel();

                //Load method annotation
                for (Method method : c.getMethods()) {
                    anns = method.getAnnotations();

                    for (Annotation anny : anns) {
                        if (anny instanceof MethodAnno) {
                            MethodAnno m = (MethodAnno) anny;
                            SoaMethod meth = new SoaMethod(m.name(), method, m.returnDescriptions());

                            methods.put(meth.getName(), meth);
                        }
                    }
                }
            }
        }
        if (!foundService) {
            throw new IllegalArgumentException("Class did not have service annotation");
        }
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Object execute(String methodName, String[] params) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        SoaMethod meth = methods.get(methodName);
        List<Object> paramList = new LinkedList<Object>();
        if (meth == null) {
            //TODO
            throw new IllegalArgumentException();
        }

        if (params.length != meth.getParameters().size()) {
            //TODO
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < params.length; ++i) {
            SoaParameter param = meth.getParameters().get(i);
            if (param.getType() == Double.class) {
                paramList.add(Double.parseDouble(params[i]));
            } else if (param.getType() == double.class) {
                paramList.add(Double.parseDouble(params[i]));
            }
            else if(param.getType() == Integer.class)
            {
                paramList.add(Integer.parseInt(params[i]));
            }
            else if (param.getType() == String.class)
            {
                paramList.add(params[i]);
            }
            else {
                //TODO
                throw new IllegalArgumentException("Cannot parse parameters of type" + param.getType());
            }
        }

        Method method = c.getMethod(meth.getMethodName(), meth.getTypes());

        Object o = method.invoke(null, paramList.toArray(new Object[paramList.size()]));

        Class<?> methodReturn = method.getReturnType();
        return methodReturn.cast(o);

    }

    public String getName() {
        return name;
    }

    public List<SoaMethod> getMethods()
    {
        return new ArrayList<SoaMethod>(this.methods.values());
    }
}
