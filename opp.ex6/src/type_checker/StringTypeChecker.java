package type_checker;

import Sjavac.Parser;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTypeChecker implements TypeChecker {
    public static final String STRING_TYPE = "String";
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private final int scopeLevel;
    private final boolean isFinal;

    /**
     * constructor
     *
     * @param line line to be checked
     */
    public StringTypeChecker(String line,int scopeLevel,boolean isFinal) throws InvalidVariableException {

        // split line into names and values
        varsToCheck = splitLine(line,scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal = isFinal ;

    }
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
            // add to variable map in Parser class
            Variable newVar = new Variable(name, STRING_TYPE,value,scopeLevel,isFinal);
            Parser.variables.get(scopeLevel).put(name,newVar);
        }

    }
}
