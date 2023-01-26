package oop.ex6.type_checker;


import oop.ex6.main.Parser;
import oop.ex6.main.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntTypeChecker implements TypeChecker {

    private static final String VALID_VALUE_REGEX = "^\\s*[-]?(\\d+)*\\s*\\;?$";

    private static final String SEPARATE_LINE_REGEX = "^(.*?[^\\s])\\s*=\\s*(.*)$";

    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    public static final String INT_TYPE = "int";
    private final int scopeLevel;
    private final boolean isFinal;

    private HashMap<String, String> varsToCheck;
    private Pattern pattern;
    private Matcher matcher;
    private ArrayList<String[]> arr;
    private HashMap<String, String> varsToFindLater;

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
        varsToFindLater = new HashMap<>();

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

                Matcher nameMatcher = namePattern.matcher(value);
                if(!nameMatcher.matches()){
                    throw new InvalidTypeException();
                }

                //check if already declared in earlier scope
                boolean inPrevScope = checkScope(scopeLevel, value);

//                if (!inPrevScope && scopeLevel == 0) {
//                    varsToFindLater.put(value, INT_TYPE);
//                }
                if(!inPrevScope){
                    throw new InvalidTypeException();
                }
            }
        }
    }

    /**
     * getter for array of variables to find later in file
     *
     * @return hashmap with key - name , value - type
     */
    public HashMap<String, String> getVarsToFindLater() {
        return varsToFindLater;
    }

    public ArrayList<String[]> getArr() {
        return arr;
    }
}
