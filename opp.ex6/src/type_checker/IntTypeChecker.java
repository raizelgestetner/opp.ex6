package type_checker;


import Sjavac.Parser;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntTypeChecker implements TypeChecker {

    private static final String VALID_VALUE_REGEX = "^\\s*[-+]?(\\d+)*\\s*\\;?$";

    private static final String SEPARATE_LINE_REGEX = "^(.*?[^\\s])\\s*=\\s*(.*)$";

    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    public static final String INT_TYPE = "int";
    private final int scopeLevel;
    private final boolean isFinal;

    private HashMap<String, String> varsToCheck;
    private Pattern pattern;
    private Matcher matcher;
    private ArrayList<String[]> arr;


    /**
     * constructor
     *
     * @param line line to be checked
     */
    public IntTypeChecker(String line, int scopeLevel, boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {

        // split line into names and values
        varsToCheck = splitLine(line, scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal = isFinal;
        arr = new ArrayList<>();

    }

    /**
     * can be an int number positive, 0 or negative
     */
    @Override
    public void checkValidity() throws InvalidTypeException, VarNameAlreadyUsed {

        HashMap<String, Variable> scopeLevelVarMap = Parser.variables.get(scopeLevel);

        // iterate over hashmap
        for (Map.Entry<String, String> entry : varsToCheck.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            // check name
            checkName(name);

            // check value
            checkValue(value);

            arr.add(new String[]{name, INT_TYPE, value});

        }

    }

    public boolean isFinal() {
        return isFinal;
    }

    public IntTypeChecker() {
        scopeLevel = 0;
        isFinal = false;
    }

    private void checkValue(String value) throws InvalidTypeException {
        if (value != null) {
            Matcher matcher = valuePattern.matcher(value);
            if (!matcher.matches()) {
                //check if already declared in earlier scope
                boolean inPrevScope = checkScope(scopeLevel, value);

                if (!inPrevScope) {


                    throw new InvalidTypeException();
                }
            }
        }
    }

    public ArrayList<String[]> getArr() {
        return arr;
    }
}
