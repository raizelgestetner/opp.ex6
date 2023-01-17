import java.util.ArrayList;

public class Method {

    private String methodName ;
    private ArrayList<Variable> methodParameters;

    /**
     * constuctor
     * @param methodName name of method
     * @param methodParameters method parameters
     */
    public Method(String methodName, ArrayList<Variable> methodParameters) {
        this.methodName = methodName;
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
}
