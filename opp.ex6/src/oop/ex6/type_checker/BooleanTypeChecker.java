package oop.ex6.type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *Checks if a given word is of type boolean
 */
public class BooleanTypeChecker extends TypeChecker {
    private static final String BOOLEAN_TYPE = "boolean";
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "(true|false|-?\\d+(\\.\\d+)?)";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private final boolean isFinal;
    private final int scopeLevel;
    private ArrayList<String[]> arr;
    private HashMap<String,String > varsToFindLater;


    /**
     * constructor
     * @param line holds the vars to check
     * @param scopeLevel the scope that the line came from
     * @param isFinal true if the given word to check is final
     * @throws InvalidVariableException the var format is invalid
     * @throws VarNameAlreadyUsed the var name exist in this scope
     */
    public BooleanTypeChecker(String line,int scopeLevel,boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {
        varsToCheck=splitLine(line,scopeLevel);
        this.scopeLevel=scopeLevel;
        this.isFinal = isFinal;
        this.arr= new ArrayList<>();
        this.varsToFindLater=new HashMap<>();
    }


    /**
     * checks validity of the word
     * @throws InvalidTypeException the type if invalid
     */
    @Override
    public void checkValidity() throws InvalidTypeException {
        for (Map.Entry<String, String> entry : varsToCheck.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            // check name
            checkName(name);

            // check value
            checkValue(value);
            arr.add(new String[]{name, BOOLEAN_TYPE, value});
        }

    }

    /**
     * checks that the value given to the var is valid
     * @param value given to the var
     * @throws InvalidTypeException thrown if the value given is invalid for bool type
     */
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
//                    varsToFindLater.put(value, BOOLEAN_TYPE);
//                }
                if(!inPrevScope){
                    throw new InvalidTypeException();
                }
            }
        }
    }
//
//    public HashMap<String, String> getVarsToFindLater() {
//        return varsToFindLater;
//    }
//
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
