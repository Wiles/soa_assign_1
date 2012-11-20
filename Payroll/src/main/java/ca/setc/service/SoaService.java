package ca.setc.service;

import ca.setc.annotations.MethodAnno;
import ca.setc.annotations.ServiceAnno;
import ca.setc.soa.SoaException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Represents a SOA service
 */
public class SoaService {

    private String name;
    private Class<?> c;
    private int securityLevel;
    private String description;
    private Map<String, SoaMethod> methods = new HashMap<String, SoaMethod>();

    /**
     * Constructor
     * @param className
     * @throws ClassNotFoundException
     */
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

    /**
     * get the security level of the service
     * @return
     */
    public int getSecurityLevel() {
        return securityLevel;
    }

    /**
     * get the description of the service
     * @return
     */
    public String getDescription()
    {
        return this.description;
    }

    public Object execute(String methodName, Map<String, SoaParameter> params) throws SoaException {
        SoaMethod method = methods.get(methodName);
        String[] parameters = new String[method.getParameters().size()];

        for(Integer i = 0; i < params.size(); ++i)
        {
            String key = String.format("%d", i + 1);
            SoaParameter sent = params.get(key);
            SoaParameter needed = method.getParameters().get(i);
            if(sent == null && needed.isRequired())
            {
                throw new SoaException(SoaException.CONTENT_ERROR, "Required parameter not supplied");
            }
            else
            {
                if(!sent.getName().equals(needed.getName()))
                {
                    throw new SoaException(SoaException.CONTENT_ERROR, "Parameter name mismatch");
                }
                else
                {
                    parameters[i] = sent.getValue();
                }

            }
        }
        return execute(methodName, parameters);
    }

    /**
     * Executes a method against the service
     * @param methodName
     * @param params
     * @return the return value of the method called
     */
    public Object execute(String methodName, String[] params) throws SoaException {
        SoaMethod meth = methods.get(methodName);
        List<Object> paramList = new LinkedList<Object>();
        if (meth == null) {
            throw new SoaException(SoaException.CONTENT_ERROR, "Insufficient parameters");
        }

        if (params.length != meth.getParameters().size()) {
            throw new SoaException(SoaException.CONTENT_ERROR, "Insufficient parameters");
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
        try
        {
            Method method = c.getMethod(meth.getMethodName(), meth.getTypes());

            Object o = method.invoke(null, paramList.toArray(new Object[paramList.size()]));

            Class<?> methodReturn = method.getReturnType();
            return methodReturn.cast(o);
        }
        catch(Exception e)
        {
            throw new SoaException(e);
        }

    }

    /**
     * get the name
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Get the methods
     * @return
     */
    public List<SoaMethod> getMethods()
    {
        return new ArrayList<SoaMethod>(this.methods.values());
    }

    public SoaMethod getMethod(String name)
    {
        return this.methods.get(name);
    }
}
