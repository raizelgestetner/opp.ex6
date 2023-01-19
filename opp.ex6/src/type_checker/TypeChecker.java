package type_checker;

import Sjavac.Parser;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TypeChecker {

    String SEPARATE_LINE_REGEX = "/^([a-zA-Z_][a-zA-Z\\d_]*)\\s{1}(?:=\\s*([^,;]+))?\\s*,?\\s*;/gm";
    Pattern separateLinePattern = Pattern.compile(SEPARATE_LINE_REGEX);
    String VALID_NAME_REGEX = "^(?!\\d)[a-zA-Z_][a-zA-Z\\d_]*$";
    Pattern namePattern = Pattern.compile(VALID_NAME_REGEX);


    void checkValidity() throws InvalidTypeException;

    /**
     * this function splits the given line into names and values or variables. and checks that these names weren't
     * already used for other variables
     * @param line line to be split
     * @return hashmap with names of variables as keys and their value (if value was initialized, else value is null)
     */
    default HashMap<String, String> splitLine(String line,int scopeLevel) {
        HashMap<String, String> varList = new HashMap<>();

        Matcher matcher = separateLinePattern.matcher(line);

        while (matcher.find()) {
            if (!Parser.variables.get(scopeLevel).containsKey(matcher.group(1))) {
                varList.put(matcher.group(1), matcher.group(2));
            }
        }

        return varList;
    }

    default void checkName(String name) throws InvalidTypeException {
        Matcher matcher = namePattern.matcher(name);
        if (!matcher.matches()) {
            throw new InvalidTypeException();
        }
    }
    default String trimLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.replaceAll("\\s+", " ");
        return line.trim();
    }
}
