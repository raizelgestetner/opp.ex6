package type_checker;

import Sjavac.Parser;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleTypeChecker implements TypeChecker {
    private static final String VALID_VALUE_REGEX = "-?\\\\d+(\\\\.\\\\d+)?";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private static final String DOUBLE_TYPE = "double";
    private final int scopeLevel;
    private final boolean isFinal;

    private HashMap<String, String> varsToCheck;
    private Matcher matcher;

    /**
     * constructor
     *
     * @param line line to be checked
     */
    public DoubleTypeChecker(String line,int scopeLevel,boolean isFinal) {

        // split line into names and values
        varsToCheck = splitLine(line,scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal = isFinal;

    }

    /**
     * checks that the name and value of parameter if valid
     * value must an integer or a floating-point number (positive, 0 or negative)
     */
    @Override
    public void checkValidity() throws InvalidTypeException {
        // iterate over hashmap
        for (Map.Entry<String, String> entry : varsToCheck.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            // check name
            checkName(name);

            // check value
            if (value != null) {
                Matcher matcher = valuePattern.matcher(value);
                if (!matcher.matches()) {
                    throw new InvalidTypeException();
                }
            }

            Variable newVar = new Variable(name, DOUBLE_TYPE,value,scopeLevel,isFinal);
            Parser.variables.get(scopeLevel).put(name,newVar);
        }
    }
}
