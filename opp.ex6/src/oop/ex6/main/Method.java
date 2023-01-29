package oop.ex6.main;

import java.util.ArrayList;
import java.util.HashMap;

public class Method {

     private String methodName ;
    private ArrayList<Variable> methodParameters;
    private int methodScope;
    private boolean hasReturn;



    /**
     * constructor
     * @param methodName name of method
     * @param methodParameters method parameters
     */
    public Method(String methodName, ArrayList<Variable> methodParameters) {
        this.methodName = methodName;
        hasReturn = false;
        this.methodParameters = methodParameters;

    }

    /**
     * getter
     * @return arraylist of method parameters
     */
    public ArrayList<Variable> getMethodParameters() {
        return methodParameters;
    }

    /**
     * getter
     * @return String name of method
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * getter for method scope
     * @return method scope
     */
    public int getMethodScope() {
        return methodScope;
    }

    /**
     * sets if method has return
     */
    public void hasReturn(){
        hasReturn = true;
    }
    public void hasNoReturn(){
        hasReturn = false;
    }

    /**
     * getter for hasReturn
     * @return whether method has return or not
     */
    public boolean GetHasReturn(){
        return hasReturn;
    }
    public ArrayList<Variable> getMethodParams(){
        return methodParameters;
    }

}
