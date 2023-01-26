package oop.ex6.type_checker;

import oop.ex6.main.Method;
import oop.ex6.main.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MethodChecker implements TypeChecker {
    public static final String REGEX_VALID_TYPE = "^(int|double|String|boolean|char)$";
    private final String params;
    private final int scopeLevel;
    private final String methodName;
    private Method method;
    private static final String METHOD_NAME_REGEX = "^[a-zA-Z][\\w]*";
    private boolean throwException;
    private TypeCheckerFactory factory ;

    /**
     * constructor
     * @param line line to be checked
     * @param scopeLevel scope level of method
     * @param name name of method
     */
    public MethodChecker(String line, int scopeLevel, String name) {
        this.params = line;
        this.methodName = name;
        this.scopeLevel = scopeLevel;
        this.factory = new TypeCheckerFactory();

    }

    @Override
    public void checkValidity() throws InvalidTypeException, InvalidMethodNameException {
        HashMap<String, Variable> varMap = new HashMap<>();
        checkMethodName();
        //check if method name is valid . if valid - continue, if not-return
        if(throwException){
            throw new InvalidMethodNameException();
        }
        if (params != null) {

            for (String param : params.split(",")) {
                param = trimLine(param);
                String[] var = param.split(" ");
                if (var.length == 2) {
                    String varType = var[0].trim();
                    String varName = var[1].trim();

                    // check type is valid
                    Pattern typePattern = Pattern.compile(REGEX_VALID_TYPE);
                    Matcher matcher = typePattern.matcher(varType);
                    if(!matcher.matches()){
                        throwException = false;
                        break;
                    }

                    if (!varMap.containsKey(varName)) {

                        Variable variable = new Variable(varName, varType, scopeLevel, false);
                        varMap.put(varName, variable);
                        throwException = false;
                    }

                } else {
                    throwException = true;
                }

            }

        } else {
            throwException = false;
        }

        this.method = new Method(methodName, varMap);
    }

    /**
     * this method checks that the name of the method is valid
     */
    private void checkMethodName() {
        Pattern methodNamePattern = Pattern.compile(METHOD_NAME_REGEX);
        Matcher matcher = methodNamePattern.matcher(methodName);
        throwException= !matcher.matches();
    }

    /**
     * getter for method
     * @return Method
     */
    public Method getMethod() {
        return method;
    }

    /**
     * getter
     * @return boolean throwException
     */
    public boolean getThrowException() {
        return throwException;
    }
}

