package oop.ex6.type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTypeChecker extends TypeChecker {
    private static final String STRING_TYPE = "String";
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private final int scopeLevel;
    private final boolean isFinal;
    private ArrayList<String[]> arr;
    private HashMap<String,String> varsToFindLater;

    /**
     * constructor
     *
     * @param line line to be checked
     */
    public StringTypeChecker(String line,int scopeLevel,boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {

        // split line into names and values
        varsToCheck = splitLine(line,scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal = isFinal ;
        this.arr =new ArrayList<>();
        this.varsToFindLater = new HashMap<>();

    }

    /**
     * getter
     * @return arraylist</String[]>
     */
    public ArrayList<String[]> getArr() {
        return arr;
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
            checkValue(value);

            arr.add(new String[]{name, STRING_TYPE, value});

        }

    }

    public HashMap<String, String> getVarsToFindLater() {
        return varsToFindLater;
    }

    /**
     * checks value of variable
     * @param value value of variable to be checked
     * @throws InvalidTypeException
     */
    private void checkValue(String value) throws InvalidTypeException {
        if (value != null) {
            Matcher matcher = valuePattern.matcher(value);
            Matcher nameMatcher = namePattern.matcher(value);

            if (!matcher.matches()) {
                if(!nameMatcher.matches()){
                    throw new InvalidTypeException();
                }
                //check if already declared in earlier scope
                boolean inPrevScope = checkScope(scopeLevel, value);
//
//                if (!inPrevScope && scopeLevel == 0) {
//                    varsToFindLater.put(value, STRING_TYPE);
//                }
                if(!inPrevScope){
                    throw new InvalidTypeException();
                }
            }
        }
    }

    /**
     * getter for boolean isFinal
     * @return true or false
     */
    public boolean isFinal() {
        return isFinal;
    }
}
