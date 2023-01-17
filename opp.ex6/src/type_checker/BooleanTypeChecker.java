package type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static type_checker.IntTypeChecker.namePattern;

public class BooleanTypeChecker implements VariableTypeChecker {
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "(true|false|-?\\d+(\\.\\d+)?)";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);

    ;
    public BooleanTypeChecker(String line) {
        varsToCheck=splitLine(line);
    }

    @Override
    public void checkValidity() throws InvalidTypeException {
        for (Map.Entry<String, String> entry : varsToCheck.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            // check name
            Matcher matcher = namePattern.matcher(name);
            if (!matcher.matches()) {
                throw new InvalidTypeException();
            }


            // check value
            if (value != null) {
                matcher = valuePattern.matcher(value);
                if (!matcher.matches()) {
                    throw new InvalidTypeException();
                }
            }
            variableMap.put(name,value);
        }

    }
}
