package Sjavac;

import java.util.HashMap;

public class Method {

    private String methodName ;
    private HashMap<String,Variable> methodParameters;

    /**
     * constuctor
     * @param methodName name of method
     * @param methodParameters method parameters
     */
    public Method(String methodName, HashMap<String,Variable> methodParameters) {
        this.methodName = methodName;
        this.methodParameters = methodParameters;
    }

    /**
     * getter
     * @return arraylist of method parameters
     */
    public HashMap<String,Variable> getMethodParameters() {
        return methodParameters;
    }

    /**
     * getter
     * @return String name of method
     */
    public String getMethodName() {
        return methodName;
    }
}
