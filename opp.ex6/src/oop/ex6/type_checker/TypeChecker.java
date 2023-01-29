package oop.ex6.type_checker;

import oop.ex6.main.Parser;
import oop.ex6.main.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * interface for a type checker, it also has default functionality that is used by all checks
 */
public abstract class TypeChecker {

    public static final String SEPARATE_LINE_REGEX = "(^\\s*\\w+\\s+([a-zA-Z_][a-zA-Z\\d_]*)\\s*=" +
            "\\s*([a-zA-Z_+\\-]?[a-zA-Z\\d_]*)\\s*;\\s*$)|" +
            "(^\\s*\\w+\\s+([a-zA-Z_][a-zA-Z\\d_]*)\\s*;\\s*$)";

    public static final String VALID_NAME_REGEX = "^(?!\\d)[a-zA-Z_][a-zA-Z\\d_]*$";
    Pattern namePattern = Pattern.compile(VALID_NAME_REGEX);

    /**
     * checks validity of the word
     * @throws InvalidTypeException the type if invalid
     */
    abstract public void checkValidity() throws InvalidTypeException, VarNameAlreadyUsed,
            InvalidMethodNameException;


    /**
     * this function splits the given line into names and values or variables. and checks that these names weren't
     * already used for other variables
     *
     * @param line line to be split
     * @return hashmap with names of variables as keys and their value (if value was initialized, else value is null)
     */
    protected HashMap<String, String> splitLine(String line, int scopeLevel) throws InvalidVariableException,
            VarNameAlreadyUsed {
        HashMap<String, String> varList = new HashMap<>();
        line = line.replaceFirst("\\b\\w+\\b\\s*", "");
        line = line.replaceAll(";$", "");
        String[] splitLine = line.split(",");
        for (String line1 : splitLine) {

            Pattern pattern2 = Pattern.compile("\\s*(.*)\\s*=\\s*(.*)|\\s*(.*)");
            Matcher matcher = pattern2.matcher(line1);

            if (matcher.find()) {
                if (line1.contains("=")) {
                    if (Parser.variables.containsKey(scopeLevel)) {
                        if (Parser.variables.get(scopeLevel).containsKey(matcher.group(1).trim())) {
                            // check if variable is final
                            Variable oldVar = Parser.variables.get(scopeLevel).get(matcher.group(1).trim());
                            if (oldVar.isFinal()) {
                                throw new VarNameAlreadyUsed();
                            }
                        }

                    }
                    varList.put(matcher.group(1).trim(), matcher.group(2).trim());
                } else {
                    varList.put(matcher.group(3).trim(), null);
                }


            }
        }
        if (varList.size() == 0) {
            throw new InvalidVariableException();
        }

        return varList;
    }

    protected void checkName(String name) throws InvalidTypeException {
        Matcher matcher = namePattern.matcher(name);
        if (!matcher.matches()) {
            throw new InvalidTypeException();
        }
    }


    protected boolean checkScope(int scopeLevel, String val) {
        for (int i = scopeLevel; i >= 0; i--) {
            HashMap<String, Variable> scopeLevelVarMap = Parser.variables.get(i);

            if (scopeLevelVarMap != null) {
                if (scopeLevelVarMap.containsKey(val)) {
                    return true;
                }
            }

        }
        return false;
    }

    protected String trimLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.replaceAll("\\s+", " ");
        return line.trim();
    }
}
