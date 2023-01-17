package type_checker;
import Sjavac.Method;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;


public class MethodChecker implements TypeChecker {
    private final String params;
    private final int scopeLevel;
    private final String methodName;
    private Method method;

    public MethodChecker(String line,int scopeLevel,String name) {
        this.params = line;
        this.methodName = name;
        this.scopeLevel = scopeLevel;

    }

    @Override
    public void checkValidity() throws InvalidTypeException {
            HashMap<String, Variable> varMap = new HashMap<>();
            boolean throwException=true;
            if (params != null) {

                for (String param : params.split(",")) {
                    param = trimLine(param);
                    String[] var = param.split(" ");
                    if (var.length == 2) {
                        String varType = var[0];
                        String varName = var[1];

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

    public Method getMethod() {
        return method;
    }
}

