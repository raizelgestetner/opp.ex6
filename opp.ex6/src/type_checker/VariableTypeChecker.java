package type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface VariableTypeChecker {
    HashMap<String,String>variableMap = new HashMap<>();
    String SEPARATE_LINE_REGEX = "/^([a-zA-Z_][a-zA-Z\\d_]*)\\s{1}(?:=\\s*([^,;]+))?\\s*,?\\s*;/gm";
    Pattern separateLinePattern = Pattern.compile(SEPARATE_LINE_REGEX);

    void checkValidity() throws InvalidTypeException;

    /**
     * this function splits the given line into names and values or variables. and checks that these names weren't
     * already used for other variables
     * @param line line to be split
     * @return hashmap with names of variables as keys and their value (if value was initialized, else value is null)
     */
    default HashMap<String, String> splitLine(String line) {
        HashMap<String, String> varList = new HashMap<>();

        Matcher matcher = separateLinePattern.matcher(line);

        while (matcher.find()) {
            if (!variableMap.containsKey(matcher.group(1))) {
                varList.put(matcher.group(1), matcher.group(2));
            }
        }

        return varList;
    }
}
