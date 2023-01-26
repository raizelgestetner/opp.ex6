package type_checker;

import Sjavac.Parser;
import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface TypeChecker {

    String SEPARATE_LINE_REGEX = "(^\\s*\\w+\\s+([a-zA-Z_][a-zA-Z\\d_]*)\\s*=" +
            "\\s*([a-zA-Z_+\\-]?[a-zA-Z\\d_]*)\\s*;\\s*$)|" +
            "(^\\s*\\w+\\s+([a-zA-Z_][a-zA-Z\\d_]*)\\s*;\\s*$)";
//    String SEPARATE_LINE_REGEX ="([a-zA-Z]+)\\s+((_?[a-zA-Z]+[\\w]*)\\s*(=\\s*([^,;]+))?\\s*(,\\s*" +
//        "(_?[a-zA-Z]+[\\w]*)\\s*(=\\s*([^,;]+))?\\s*)*)\\s*;";
    Pattern separateLinePattern = Pattern.compile(SEPARATE_LINE_REGEX);
    String VALID_NAME_REGEX = "^(?!\\d)[a-zA-Z_][a-zA-Z\\d_]*$";
    Pattern namePattern = Pattern.compile(VALID_NAME_REGEX);
    String VALID_INT_VALUE_REGEX = "^\\s*[-+]?(\\d+)*\\s*$";
    String VALID_DOUBLE_VALUE_REGEX = "-?\\\\d+(\\\\.\\\\d+)?";
    String VALID_CHAR_VALUE_REGEX = "^\\s*'.'\\s*$";
    String VALID_BOOLEAN_VALUE_REGEX = "(true|false|-?\\d+(\\.\\d+)?)";
    String VALID_STRING_VALUE_REGEX = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"";



    void checkValidity() throws InvalidTypeException, VarNameAlreadyUsed, InvalidMethodName;

    /**
     * this function splits the given line into names and values or variables. and checks that these names weren't
     * already used for other variables
     *
     * @param line line to be split
     * @return hashmap with names of variables as keys and their value (if value was initialized, else value is null)
     */
    default HashMap<String, String> splitLine(String line, int scopeLevel) throws InvalidVariableException {
        HashMap<String, String> varList = new HashMap<>();
        line = line.replaceFirst("\\b\\w+\\b","");
        line = line.replaceAll(";$","");
        String[] splitLine = line.split(",");
        for (String line1 :splitLine){
//        Matcher matcher = separateLinePattern.matcher(line1);

        Pattern pattern2 = Pattern.compile("\\s*(.*)\\s+=\\s+(.*)|\\s*(.*)");
        Matcher matcher =pattern2.matcher(line1);

            if (matcher.find()) {

            if (!Parser.variables.containsKey(scopeLevel) ||
                    !Parser.variables.get(scopeLevel).containsKey(matcher.group(3).trim())) {
                if (line1.contains(" = ")) {
                    varList.put(matcher.group(1).trim(), matcher.group(2).trim());
                } else {
                    varList.put(matcher.group(3).trim(), null);
                }
            }

        }
        }
        if(varList.size() == 0){
            throw new InvalidVariableException();
        }

        return varList;
    }

    default void checkName(String name) throws InvalidTypeException {
        Matcher matcher = namePattern.matcher(name);
        if (!matcher.matches()) {
            throw new InvalidTypeException();
        }
    }


    default boolean checkScope(int scopeLevel,String val){
        for (int i=scopeLevel;i>=0;i--){
            HashMap<String, Variable > scopeLevelVarMap = Parser.variables.get(i);

            if(scopeLevelVarMap!=null){
            if(scopeLevelVarMap.containsKey(val)){
                return true;}
            }

        }
        return false;
    }

    default String trimLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.replaceAll("\\s+", " ");
        return line.trim();
    }
}
