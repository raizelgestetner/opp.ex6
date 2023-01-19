package type_checker;

import Sjavac.Parser;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanTypeChecker implements TypeChecker {
    public static final String BOOLEAN_TYPE = "boolean";
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "(true|false|-?\\d+(\\.\\d+)?)";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private final boolean isFinal;
    private final int scopeLevel;

    ;
    public BooleanTypeChecker(String line,int scopeLevel,boolean isFinal) {
        varsToCheck=splitLine(line,scopeLevel);
        this.scopeLevel=scopeLevel;
        this.isFinal = isFinal;
    }

    @Override
    public void checkValidity() throws InvalidTypeException {
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
            // add to variable map in Parser class
            Variable newVar = new Variable(name, BOOLEAN_TYPE,value,scopeLevel,isFinal);
            Parser.variables.get(scopeLevel).put(name,newVar);
        }

    }
}
