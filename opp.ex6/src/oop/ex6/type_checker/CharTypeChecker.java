package oop.ex6.type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CharTypeChecker implements TypeChecker {
    public static final String CHAR_TYPE = "char";
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "^\\s*'.'\\s*$";

    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private final ArrayList<String[]> arr;
    private final HashMap<String, String> varsToFindLater;
    private boolean isFinal;
    private final int scopeLevel;

    public CharTypeChecker(String line,int scopeLevel,boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {
        varsToCheck =splitLine(line,scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal=isFinal;
        this.arr=new ArrayList<String[]>();
        this.varsToFindLater = new HashMap<>();

    }

    public HashMap<String, String> getVarsToFindLater() {
        return varsToFindLater;
    }

    @Override
    public void checkValidity() throws InvalidTypeException {
        for (Map.Entry<String, String> entry : varsToCheck.entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();

            // check name
            checkName(name);


            // check value
            checkValue(value);
            arr.add(new String[]{name, CHAR_TYPE, value});
        }

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
//                    varsToFindLater.put(value, CHAR_TYPE);
//                }
                if(!inPrevScope){
                    throw new InvalidTypeException();
                }
            }
        }
    }

    public ArrayList<String[]> getArr() {
        return arr;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
