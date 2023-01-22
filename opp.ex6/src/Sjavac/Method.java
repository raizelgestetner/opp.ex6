package Sjavac;

import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Method {

     private String methodName ;
    private HashMap<String,Variable> methodParameters;
    private int methodScope;
    private boolean hasReturn;



    /**
     * constuctor
     * @param methodName name of method
     * @param methodParameters method parameters
     */
    public Method(String methodName, HashMap<String,Variable> methodParameters) {
        this.methodName = methodName;
        this.methodScope = methodScope;
        hasReturn = false;
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

    public int getMethodScope() {
        return methodScope;
    }
    public void hasReturn(){
        hasReturn = true;
    }
    public void hasNoReturn(){
        hasReturn = false;
    }
    public boolean GetHasReturn(){
        return hasReturn;
    }

}
