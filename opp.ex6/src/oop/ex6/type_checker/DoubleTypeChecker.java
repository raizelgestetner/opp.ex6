package oop.ex6.type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoubleTypeChecker extends TypeChecker {
    private static final String VALID_VALUE_REGEX = "-?\\d+(\\.\\d+)?";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private static final String DOUBLE_TYPE = "double";
    private final int scopeLevel;
    private final boolean isFinal;

    private HashMap<String, String> varsToCheck;
    private Matcher matcher;
    private ArrayList<String[]> arr;
    private HashMap<String,String> varsToFindLater;

    /**
     * constructor
     *
     * @param line line to be checked
     */
    public DoubleTypeChecker(String line, int scopeLevel, boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {

        // split line into names and values
        varsToCheck = splitLine(line, scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal = isFinal;
        arr = new ArrayList<>();
        varsToFindLater=new HashMap<>();


    }

//    public HashMap<String, String> getVarsToFindLater() {
//        return varsToFindLater;
//    }

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

            checkValue(value);

            arr.add(new String[]{name, DOUBLE_TYPE, value});

        }
    }

    /**
     * checks that the value given to the var is valid
     * @param value given to the var
     * @throws InvalidTypeException thrown if the value given is invalid for double type
     */
    private void checkValue(String value) throws InvalidTypeException {
        if (value != null) {
            Matcher matcher = valuePattern.matcher(value);
            if (!matcher.matches()) {
                //check if already declared in earlier scope
                boolean inPrevScope = checkScope(scopeLevel, value);

                if(!inPrevScope){
                    throw new InvalidTypeException();
                }
            }
        }
    }

//    public boolean isFinal() {
//        return isFinal;
//    }

    /**
     * getter of arr of values
     * @return the array
     */
    public ArrayList<String[]> getArr() {
        return arr;
    }

}
