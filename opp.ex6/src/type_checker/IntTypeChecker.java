package type_checker;


import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntTypeChecker implements TypeChecker {

    private static final String VALID_VALUE_REGEX = "-?(0|[1-9]\\d+)";

    private static final String SEPARATE_LINE_REGEX = "^(.*?[^\\s])\\s*=\\s*(.*)$";

    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);

    private HashMap<String, String> varsToCheck;
    private Pattern pattern;
    private Matcher matcher;


    /**
     * constructor
     *
     * @param line line to be checked
     */
    public IntTypeChecker(String line) {

        // split line into names and values
        varsToCheck = splitLine(line);

    }

    /**
     * can be a number positive, 0 or negative
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
                matcher = valuePattern.matcher(value);
                if (!matcher.matches()) {
                    throw new InvalidTypeException();
                }
            }
            variableMap.put(name,value);
        }
    }
}
