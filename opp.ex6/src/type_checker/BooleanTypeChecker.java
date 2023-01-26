package type_checker;

import Sjavac.Parser;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BooleanTypeChecker implements TypeChecker {
    private static final String BOOLEAN_TYPE = "boolean";
    private final HashMap<String, String> varsToCheck;
    private static final String VALID_VALUE_REGEX = "(true|false|-?\\d+(\\.\\d+)?)";
    private static final Pattern valuePattern = Pattern.compile(VALID_VALUE_REGEX);
    private final boolean isFinal;
    private final int scopeLevel;
    private ArrayList<String[]> arr;
    private HashMap<String,String > varsToFindLater;

    ;
    public BooleanTypeChecker(String line,int scopeLevel,boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {
        varsToCheck=splitLine(line,scopeLevel);
        this.scopeLevel=scopeLevel;
        this.isFinal = isFinal;
        this.arr= new ArrayList<>();
        this.varsToFindLater=new HashMap<>();
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
            arr.add(new String[]{name, BOOLEAN_TYPE, value});
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

                if (!inPrevScope && scopeLevel == 0) {
                    varsToFindLater.put(value, BOOLEAN_TYPE);
                }
            }
        }
    }

    public HashMap<String, String> getVarsToFindLater() {
        return varsToFindLater;
    }

    public boolean isFinal() {
        return isFinal;
    }
    public ArrayList<String[]> getArr() {
        return arr;
    }
}
