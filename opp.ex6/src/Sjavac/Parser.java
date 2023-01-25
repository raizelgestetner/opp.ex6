package Sjavac;

import com.sun.jdi.InvalidTypeException;
import type_checker.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SimpleTimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class reads and parses the file
 */
public class Parser {
    public static final String REGEX_OPEN_PARENTHESIS = "\\(";
    public static final String VALID_LINE_PREFIX = "^\\s*(if|while|void|char|String|boolean|double|return" +
            "|int|})\\s*";
    public static final String MATHOS_CALL_PATTERN = "^\\s*[a-zA-z\\d]+\\s*\\(.*\\);\\s*$";
    public static final String SPLIT_BY_PARENTHESES = "\\s*\\(\\s*|\\s*\\)\\s*";
    public static final String SPLIT_BY_COMMA = "\\s*,\\s*";
    public static final String INT_REGEX = "^\\s*[+\\-]?\\d+\\s*$";
    public static final String DOUBLE_REGEX = "^[+\\-]?\\s*\\d*\\.\\d+\\s*$|^[+\\-]?\\s*\\d+\\.\\d*\\s*$";
    public static final String BOOL_REGEX = "^\\s*true\\s*$|^\\s*false\\s*$";
    public static final String STRING_REGEX = "^\\s*[a-zA-Z_\\d]*\\s*$";
    public static final String CHAR_REGEX = "^\\s*'.'\\s*$";
    private final Pattern intPattern = Pattern.compile(INT_REGEX);
    private final Pattern doublePattern = Pattern.compile(DOUBLE_REGEX);
    private final Pattern boolPattern = Pattern.compile(BOOL_REGEX);
    private final Pattern stringPattern = Pattern.compile(STRING_REGEX);

    private final Pattern charPattern = Pattern.compile(CHAR_REGEX);
    private final BufferedReader reader;
    private HashMap<String, Method> methodsList = new HashMap<>();
    public static HashMap<Integer, HashMap<String, Variable>> variables = new HashMap<>();


    private int scopeNum = 0; // todo count number of brackets and make sure is legal
    private static final String REGEX_COMMENT = "^//.*$";
    private static final String VALID_OUTLINE_REGEX_IF_WHILE = "^(if|while)\\s*\\((.*)\\)\\s*\\{\\s+$";
    private static final String INVALID_PREFIX_REGEX = "^[\\s]*[;{][\\s]*$";
    private static final String VALID_END_OF_LINE_REGEX = ".*[;{}]$";
    private static final String TYPE_PREFIX = "^\b(int|double|boolean|char|String|void|final|if|return)\b";
    private static final String METHOD_PREFIX = "void";
    private Method curMethod; //is null if not in method and otherwise holds the current method of the scope
    private Matcher matcher;

    private static final String SPLIT_LINE_METHOD =
            "^\\w+\\s+\\w+\\s*\\(([\\w\\s]+\\s+\\w+(,\\s*[\\w\\s]+\\s+\\w+)*)?\\)\\s*\\{\\s*$";//todo make sure 2
    // todo backslashes work
//

    /**
     * constructor
     */
    public Parser(BufferedReader reader) {
        this.reader = reader;
        this.curMethod = null;
    }


    public void readFile() throws IOException, EndOfLineException, StartOfLineException,
            IllegalMethodFormatException, InvalidTypeException, InvalidIfWhileBlock, MethodHasNoReturn, IllegalNestedMethod, IllegalMethodCall, VarNameAlreadyUsed, InvalidMethodName, InvalidVariableException {
        String line = reader.readLine();
        line = trimLine(line);
        while (line != null) {

            //empty line and line starting with comment should be ignored
            Pattern pattern = Pattern.compile(REGEX_COMMENT);
            Matcher matcher = pattern.matcher(line);
            if (line.isEmpty() || matcher.find()) {
                line = reader.readLine();
                if (line == null) {
                    break;
                }
                line = trimLine(line);
            }


            // check that line doesn't start with ; {
            pattern = Pattern.compile(INVALID_PREFIX_REGEX);
            matcher = pattern.matcher(line);
            if (matcher.find()) {
                reader.close();
                throw new StartOfLineException();
            }

            // check suffix of code line
            pattern = Pattern.compile(VALID_END_OF_LINE_REGEX);
            matcher = pattern.matcher(line);
            if (!matcher.find()) {
                reader.close();
                throw new EndOfLineException();
            }

            // check that content of line is valid
            checkLine(line);


            line = reader.readLine(); // todo: this ignors empty lines and then when return is not the last
            // todo: line before the end of method but an empty line
            line = trimLine(line);
        }


        // close stream
        reader.close();
    }


    public HashMap<Integer, HashMap<String, Variable>> getVariables() {
        return variables;
    }


    /**
     * after checking that prefix and suffix of the line is valid this function checks that the content of the line is
     * valid. There are 2 options - either there is a method or just regular scope.
     * this function know to check if both options are valid
     *
     * @param line line to check
     */
    private void checkLine(String line) throws IllegalMethodFormatException, InvalidTypeException,
            InvalidIfWhileBlock, MethodHasNoReturn, IllegalNestedMethod, IllegalMethodCall, VarNameAlreadyUsed, InvalidMethodName, InvalidVariableException {
        Pattern pattern = Pattern.compile(VALID_LINE_PREFIX);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {

            String first_word = matcher.group(1);
            switch (first_word) {
                case "}":
                    if (curMethod != null && !(curMethod.GetHasReturn())) {
                        throw new MethodHasNoReturn();
                    }
                    variables.remove(scopeNum);
                    scopeNum--;
                    curMethod = null;
                    break;
                case "void":
                    checkMethodLine(line);
//                    scopeNum++;

                    break;
                case "if":
                case "while":
                    curMethod = null;
                    checkIfWhile(line);
                    break;
                case "char":

                    break;
                case "int":
                    IntTypeChecker intTypeChecker = new IntTypeChecker(line, scopeNum, false);
                    intTypeChecker.checkValidity();
                    ArrayList<String[]> values = intTypeChecker.getArr();
                    for (String[] val : values) {
                        Variable var = new Variable(val[0], val[1], val[2], scopeNum, false);
                        AddVarToVars(scopeNum, val[0], var);
                    }
                    break;
                case "double":
                    break;
                case "String":

                    break;
                case "boolean":

                    break;
                case "final":

                    break;
                case "return":
                    if (scopeNum == 0) {
                        throw new InvalidReturn();
                    } else {
                        ReturnChecker returnChecker = new ReturnChecker(line);
                        returnChecker.checkValidity();//If it doesn't throw exception means the cur method hase a return value
                        curMethod.hasReturn();
                    }
                    break;
                default:
                    //todo :what is defualt

            }

        } else {
//            boolean isVar = CheckVar(line);
            CheckMethodCall(line);
            if (!CheckVar(line)) {
                throw new InvalidTypeException();
            }
        }
    }

    private boolean CheckVar(String line) throws InvalidTypeException {
        Pattern assignVar = Pattern.compile("^\\s*[a-zA-Z_\\d]+\\s*=\\s*\\w+[a-zA-Z_\\d]*\\s*;\\s*$");
        Matcher matcher = assignVar.matcher(line);
        boolean foundType = false;
        if (matcher.find()) {
            line = line.replaceAll("\\s*;\\s*", "");
            String[] params = line.split("\\s*=\\s*");
            int LHSScope = -1;

            Variable LHS = null;
            for (int i = 0; i < variables.size(); i++) {
                if (variables.get(i).containsKey(params[0])) {
                    LHSScope = i;
                    LHS = variables.get(i).get(params[0]);
                    break;
                }
            }
            if (LHSScope != -1) {
                for (int i = 0; i <= LHSScope; i++) {//check all scopes smaller or equal to lhs scope
                    if (variables.get(i).containsKey(params[1])) {//has the RHS name
                        Variable RHS = variables.get(i).get(params[1]);
                        if (!RHS.getType().equals(LHS.getType())) {//check if types are legal
                            if (!((RHS.getType().equals("int") &&
                                    (LHS.getType().equals("double") || LHS.getType().equals("boolean")))
                                    || (RHS.getType().equals("double") && LHS.getType().equals("boolean")))) {
                                foundType = false;
                                break;
                            } else {
                                foundType = true;
                                break;
                            }
                        } else {
                            foundType = true;
                            break;
                        }
                    }

                    // I added new code
                    String LHS_type = LHS.getType();
                    // check if rhs is the same type as lhs
                    Pattern p = null;
                    switch (LHS_type) {
                        case "int":
                            p = Pattern.compile(TypeChecker.VALID_INT_VALUE_REGEX);
                            break;
                        case "double":
                            p = Pattern.compile(TypeChecker.VALID_DOUBLE_VALUE_REGEX);
                            break;
                        case "String":
                            p = Pattern.compile(TypeChecker.VALID_STRING_VALUE_REGEX);
                            break;
                        case "boolean":
                            p = Pattern.compile(TypeChecker.VALID_BOOLEAN_VALUE_REGEX);
                            break;
                        case "char":
                            p = Pattern.compile(TypeChecker.VALID_CHAR_VALUE_REGEX);
                            break;
                    }
                    if (p != null) {
                        // check if value is correct type
                        matcher = p.matcher(params[1]);
                        if (matcher.matches()) {
                            foundType = true;
                            break;
                        }
                    }
                }

            } else {//LHS was never declared
                foundType = false;

            }

        }
//        else {
//            foundType = false;
//        }
        return foundType;
    }


    private void CheckMethodCall(String line) throws IllegalMethodCall {
        Pattern methodCallPattern = Pattern.compile(MATHOS_CALL_PATTERN);
        Matcher matcher = methodCallPattern.matcher(line);
        if (matcher.find()) {
            String[] splittedLine = line.split(SPLIT_BY_PARENTHESES);
            if (methodsList.containsKey(splittedLine[0])) { //check if method name is valid
                Method method = methodsList.get(splittedLine[0]);
                String[] givenParams = splittedLine[1].split(SPLIT_BY_COMMA);
                HashMap<String, Variable> methodParameters = method.getMethodParameters();
                if (givenParams.length != methodParameters.size()) { //check num of params given is valid
                    throw new IllegalMethodCall();
                }
                int curGivenParamIdx = 0;
                int curScopeIdx = 0;
                for (Variable methodVar : methodParameters.values()) {
                    String methodType = methodVar.getType();
                    boolean nameExists = false;
//                    boolean varsIsEmpty = variables.isEmpty();
                    int numOfVars = variables.size();
                    while (curScopeIdx < variables.size() && !nameExists) {
                        if (!variables.get(curScopeIdx).containsKey(givenParams[curGivenParamIdx])) { //the
                            // cur scope doesn't contain the varName
                            curScopeIdx++;//check next scope
                        } else { //found variable with same name

                            Variable foundVar =
                                    variables.get(curScopeIdx).get(givenParams[curGivenParamIdx]);

                            String varType = foundVar.getType();

                            //check if types are valid, if it isn't will throw exception
                            if (!methodType.equals(varType)) {
                                if (!((varType.equals("int") && (methodType.equals("double") || methodType.equals(
                                        "boolean"))) || (varType.equals("double") && methodType.equals("boolean")))) {
                                    throw new IllegalMethodCall();
                                }
                            }
                            nameExists = true;
                        }
                    }//we checked all scopes for this var
                    if (!nameExists) {
                        nameExists = ParamTypeChecker(methodType, givenParams[curGivenParamIdx]);
                        if (!nameExists) {
                            throw new IllegalMethodCall();
                        }
                    }
                }


            }
        }

    }

    private boolean ParamTypeChecker(String methodType, String givenParams) {
        boolean goodParam = true;
        Matcher intMatcher = intPattern.matcher(givenParams);
        Matcher doubleMatcher = doublePattern.matcher(givenParams);
        Matcher boolMatcher = boolPattern.matcher(givenParams);
        Matcher stringMatcher = stringPattern.matcher(givenParams);
        Matcher charMatcher = charPattern.matcher(givenParams);


        if (!((methodType.equals("boolean") && (intMatcher.find() || doubleMatcher.find() || boolMatcher.find()))
                || (methodType.equals("double") && (doubleMatcher.find() || boolMatcher.find()))
                || (methodType.equals("int") && intMatcher.find())
                || (methodType.equals("String") && stringMatcher.find())
                || (methodType.equals("char") && charMatcher.find()))) {
            goodParam = false;
        }
        return goodParam;
    }

    private void checkIfWhile(String line) throws InvalidIfWhileBlock, InvalidTypeException {
        Pattern ifWhilePattern = Pattern.compile(VALID_OUTLINE_REGEX_IF_WHILE);
        Matcher matcher = ifWhilePattern.matcher(line);
        if (matcher.find()) {
            String condition = matcher.group(2);
            scopeNum++;
            IfWhileTypeChecker checker = new IfWhileTypeChecker(condition);
            checker.checkValidity();
            ArrayList<String> params = checker.getParamNames();
            for (String param : params) {
                boolean foundGoodParam = false;
                for (int i = 0; i < scopeNum; i++) {
                    HashMap<String, Variable> varsInScope = variables.get(i);
                    for (Variable var : varsInScope.values()) {
                        boolean goodType =
                                var.getType().equals("int") ||
                                        var.getType().equals("double") ||
                                        var.getType().equals("boolean");
                        if (param.equals(var.getName()) && (goodType)) {
                            foundGoodParam = true;
                            break;
                        }

                    }
                }
                if (!foundGoodParam) {
                    throw new InvalidTypeException();
                }
            }
        } else {
            throw new InvalidIfWhileBlock();
        }
    }

    private void checkMethodLine(String line) throws
            InvalidTypeException, IllegalMethodFormatException, IllegalNestedMethod, InvalidMethodName {
        if (curMethod != null) {
            throw new IllegalNestedMethod();
        }
        if (line.startsWith(METHOD_PREFIX)) {

//                    line = line.replaceAll(METHOD_PREFIX, VALID_LINE_PREFIX);
            // split line to find method name and parameters
            Pattern pattern = Pattern.compile(SPLIT_LINE_METHOD);
            Matcher matcher = pattern.matcher(line);
            boolean throwException = true;
            if (matcher.find()) {
                String method_name = matcher.group(0).split(REGEX_OPEN_PARENTHESIS)[0].strip();
                method_name = method_name.replaceAll("\\s*void\\s*", "");
                if (!methodsList.containsKey(method_name)) { //todo check that this is the correct method name

                    MethodChecker methodChecker = new MethodChecker(matcher.group(1), scopeNum,
                            method_name);
                    methodChecker.checkValidity();
                    throwException = methodChecker.getThrowException();
                    methodsList.put(method_name, methodChecker.getMethod());
                    curMethod = methodChecker.getMethod();
                    scopeNum++;
                }

                if (throwException) {
                    throw new IllegalMethodFormatException();
                }

            }
        } else {
            throw new IllegalMethodFormatException();
        }
    }


    /**
     * this function removes extra white spaces from line
     *
     * @param line to remove spaces from
     * @return line after spaces have been removed
     */
    private String trimLine(String line) {
        if (line == null) {
            return null;
        }
        line = line.replaceAll("\\s+", " ");
        return line.trim();
    }

    private void AddVarToVars(int scopeNum, String name, Variable var) throws VarNameAlreadyUsed {
        if (variables.size() == 0) {
            HashMap<String, Variable> newHash = new HashMap<>();
            newHash.put(name, var);
            variables.put(scopeNum, newHash);
        } else {

            // check if variables map ha a submap for the current scope level
            if (!variables.containsKey(scopeNum)) {
                HashMap<String, Variable> newHash = new HashMap<>();
                variables.put(scopeNum, newHash);

                // check if it already contains a var with this name
                if (variables.get(scopeNum).containsKey(name)) {
                    throw new VarNameAlreadyUsed();
                }
            } else {
                variables.get(scopeNum).put(name, var);
            }
        }
    }

}
