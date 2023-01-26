package type_checker;

import Sjavac.Parser;
import Sjavac.Variable;
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
    private boolean isFinal;
    private final int scopeLevel;

    public CharTypeChecker(String line,int scopeLevel,boolean isFinal) throws InvalidVariableException, VarNameAlreadyUsed {
        varsToCheck =splitLine(line,scopeLevel);
        this.scopeLevel = scopeLevel;
        this.isFinal=isFinal;
        this.arr=new ArrayList<String[]>();

    }

    @Override
    public void checkValidity() throws InvalidTypeException {
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
            arr.add(new String[]{name, CHAR_TYPE, value});
        }

    }

    public ArrayList<String[]> getArr() {
        return arr;
    }

    public boolean isFinal() {
        return isFinal;
    }
}
