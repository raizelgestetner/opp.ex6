package oop.ex6.main;

import com.sun.jdi.InvalidTypeException;
import oop.ex6.type_checker.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class reads and parses the file
 */
public class Parser {
    public static final String VALID_INT_VALUE_REGEX = "^\\s*[-+]?(\\d+)*\\s*$";
    public static final String VALID_DOUBLE_VALUE_REGEX = "-?\\\\d+(\\\\.\\\\d+)?";
    public static final String VALID_CHAR_VALUE_REGEX = "^\\s*'.'\\s*$";
    public static final String VALID_BOOLEAN_VALUE_REGEX = "(true|false|-?\\d+(\\.\\d+)?)";
    public static final String VALID_STRING_VALUE_REGEX = "\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"";

    private static final String METHOD_SUFFIX = "\\s*\\([^\\)]*\\)\\s*(\\{|;)$";
    private static final String REGEX_OPEN_PARENTHESIS = "\\(";
    private static final String VALID_LINE_PREFIX = "^\\s*(if|while|void|char|String|boolean|double|return" +
            "|final|int|})\\s*";
    private static final String METHOD_CALL_PATTERN = "^\\s*[a-zA-z\\d]+\\s*\\(.*\\);\\s*$";
    private static final String SPLIT_BY_PARENTHESES = "\\s*\\(\\s*|\\s*\\)\\s*";
    private static final String SPLIT_BY_COMMA = "\\s*,\\s*";
    private static final String INT_REGEX = "^\\s*[+\\-]?\\d+\\s*$";
    private static final String DOUBLE_REGEX = "^[+\\-]?\\s*\\d*\\.\\d+\\s*$|^[+\\-]?\\s*\\d+\\.\\d*\\s*$";
    private static final String BOOL_REGEX = "^\\s*true\\s*$|^\\s*false\\s*$";
    private static final String STRING_REGEX = "^\\s*[a-zA-Z_\\d]*\\s*$";
    private static final String CHAR_REGEX = "^\\s*'.'\\s*$";


    private static final String BOOLEAN_TYPE = "boolean";
    private static final String STRING_TYPE = "String";
    private static final String DOUBLE_TYPE = "double";
    private static final String CHAR_TYPE = "char";
    private static final String INT_TYPE = "int";
    private static final String RETURN_TYPE = "return";
    private static final String FINAL_TYPE = "final";
    public static final String ASSIGN_VAR_REGEX =
            "\\s*[a-zA-Z_\\d]+\\s*=\\s*(\"[^\"]*\"|'[^']*'|\\w+[a-zA-Z_\\d]*)\\s*;\\s*$";
    public static final String END_OF_METHOD_SIGN = "}";
    public static final String VOID_FIRST_WORD = "void";
    public static final String IF_FIRST_WORD = "if";
    public static final String WHILE_FIRST_WORD = "while";

    private final Pattern intPattern = Pattern.compile(INT_REGEX);
    private final Pattern doublePattern = Pattern.compile(DOUBLE_REGEX);
    private final Pattern boolPattern = Pattern.compile(BOOL_REGEX);
    private final Pattern stringPattern = Pattern.compile(STRING_REGEX);

    private final Pattern charPattern = Pattern.compile(CHAR_REGEX);
    private final BufferedReader reader;
    private ArrayList< Variable> curMethodParams;
    private HashMap<String, Method> methodsList;
    public static HashMap<Integer, HashMap<String, Variable>> variables;


    private int scopeNum = 0;
    private static final String REGEX_COMMENT = "^(\\/\\/).*$";

    private static final String VALID_OUTLINE_REGEX_IF_WHILE = "^(if|while)\\s*\\((.*)\\)\\s*\\{$";
    private static final String INVALID_PREFIX_REGEX = "^[\\s]*[;{][\\s]*$";
    private static final String VALID_END_OF_LINE_REGEX = ".*[;{}]$";
    private static final String TYPE_PREFIX = "^\b(int|double|boolean|char|String|void|final|if|return)\b";
    private static final String METHOD_PREFIX = "void";
    private Method curMethod; //is null if not in method and otherwise holds the current method of the scope
    private ArrayList<String> methodsToFind;

    private HashMap<String, String> globalVarsToFind;

    private static final String SPLIT_LINE_METHOD =
           "^\\w+\\s+\\w+\\s*\\(([\\w\\s]+\\s+\\w+\\s*(,\\s*[\\w\\s]+\\s+\\w+\\s*)*)?\\)\\s*\\{\\s*$";


    /**
     * constructor
     */
    public Parser(BufferedReader reader) {
        this.reader = reader;
        this.curMethod = null;
        this.methodsToFind = new ArrayList<>();
        this.globalVarsToFind = new HashMap<>();
        variables = new HashMap<>();
        methodsList = new HashMap<>();
        this.curMethodParams = new ArrayList<>();

    }


    /**
     * this function reads the file and throws exceptions if file has incorrect syntax
     */
    public void readFile() throws IOException, EndOfLineException, StartOfLineException,
            IllegalMethodFormatException, InvalidTypeException, InvalidIfWhileBlockException, MethodHasNoReturnException, IllegalNestedMethodException, IllegalMethodCallException, VarNameAlreadyUsed, InvalidMethodNameException, InvalidVariableException, UndeclaredMethodException, AlreadyDeclaredFinalException, IllegalNumOfScopesException, GlobalVariableException, InvalidReturnException {
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
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    continue;
                }

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

            line = reader.readLine();
            line = trimLine(line);
        }

        if (methodsToFind.size() > 0) {
            throw new UndeclaredMethodException();
        }

        if (globalVarsToFind.size() > 0) {
            throw new GlobalVariableException();
        }

        if (scopeNum != 0) {
            throw new IllegalNumOfScopesException();
        }

        // close stream
        reader.close();
    }


    /**
     * getter for variables map
     *
     * @return variables map
     */
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
            InvalidIfWhileBlockException, MethodHasNoReturnException, IllegalNestedMethodException,
            IllegalMethodCallException, VarNameAlreadyUsed, InvalidMethodNameException,
            InvalidVariableException, AlreadyDeclaredFinalException, InvalidReturnException {
        Pattern pattern = Pattern.compile(VALID_LINE_PREFIX);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {

            String first_word = matcher.group(1);
            if(!first_word.equals(END_OF_METHOD_SIGN) && curMethod!=null){
                curMethod.hasNoReturn();
            }
            switch (first_word) {
                case END_OF_METHOD_SIGN:
                    if (curMethod != null && !(curMethod.GetHasReturn())) {
                        throw new MethodHasNoReturnException();
                    }
                    variables.remove(scopeNum);
                    curMethodParams.clear();
                    scopeNum--;
                    break;
                case VOID_FIRST_WORD:
                    checkMethodLine(line);

                    break;
                case IF_FIRST_WORD:
                case WHILE_FIRST_WORD:
                    checkIfWhile(line);

                    break;

                case CHAR_TYPE:
                case INT_TYPE:
                case BOOLEAN_TYPE:
                case DOUBLE_TYPE:
                case STRING_TYPE:
                    checkNewVariable(first_word, line, scopeNum, false);
                    break;

                case FINAL_TYPE:
                    String[] splitLine = line.split(" ");
                    String type = splitLine[1];
                    line = line.replaceFirst("final ", "");
                    checkNewVariable(type, line, scopeNum, true);
                    break;
                case RETURN_TYPE:
                    if (scopeNum == 0) {
                        throw new InvalidReturnException();
                    } else {

                        ReturnChecker returnChecker = new ReturnChecker(line);
                        returnChecker.checkValidity();
                        //If it doesn't throw exception means the cur method has a return value
                        curMethod.hasReturn();
//                        curMethod = null;
                    }
                    break;
                default:

            }

        } else {
            curMethod.hasNoReturn();
            CheckMethodCall(line);
            if (!CheckVar(line)) {
                throw new InvalidTypeException();
            }
        }
    }


    /**
     * this function checks new variables in the file
     *
     * @param vartype  type of variable
     * @param line     line in file
     * @param scopeNum current scope number
     * @param isFinal  boolean value if is final or not
     * @throws InvalidTypeException
     * @throws InvalidVariableException
     * @throws VarNameAlreadyUsed
     */
    private void checkNewVariable(String vartype, String line, int scopeNum, boolean isFinal)
            throws InvalidTypeException, InvalidVariableException, VarNameAlreadyUsed {
        switch (vartype) {
            case CHAR_TYPE:
                CharTypeChecker charChecker = new CharTypeChecker(line, scopeNum, isFinal);
                charChecker.checkValidity();
                ArrayList<String[]> charVals = charChecker.getArr();
                for (String[] val : charVals) {
                    Variable var = new Variable(val[0], val[1], val[2], scopeNum, isFinal);
                    AddVarToVars(scopeNum, val[0], var);
                }
                break;
            case INT_TYPE:
                IntTypeChecker intTypeChecker = new IntTypeChecker(line, scopeNum, isFinal);
                intTypeChecker.checkValidity();
                ArrayList<String[]> values = intTypeChecker.getArr();
                for (String[] val : values) {
                    Variable var = new Variable(val[0], val[1], val[2], scopeNum, isFinal);
                    AddVarToVars(scopeNum, val[0], var);
                }
                break;
            case DOUBLE_TYPE:
                DoubleTypeChecker doubleTypeChecker = new DoubleTypeChecker(line, scopeNum, isFinal);
                doubleTypeChecker.checkValidity();
                ArrayList<String[]> dValues = doubleTypeChecker.getArr();
                for (String[] val : dValues) {
                    Variable var = new Variable(val[0], val[1], val[2], scopeNum, isFinal);
                    AddVarToVars(scopeNum, val[0], var);
                }
                break;
            case STRING_TYPE:
                StringTypeChecker stringTypeChecker = new StringTypeChecker(line, scopeNum, isFinal);
                stringTypeChecker.checkValidity();
                ArrayList<String[]> sValues = stringTypeChecker.getArr();
                for (String[] val : sValues) {
                    Variable var = new Variable(val[0], val[1], val[2], scopeNum, isFinal);
                    AddVarToVars(scopeNum, val[0], var);
                }
                break;
            case BOOLEAN_TYPE:
                BooleanTypeChecker booleanChecker = new BooleanTypeChecker(line, scopeNum, isFinal);
                booleanChecker.checkValidity();
                ArrayList<String[]> booleanVars = booleanChecker.getArr();
                for (String[] val : booleanVars) {
                    Variable var = new Variable(val[0], val[1], val[2], scopeNum, isFinal);
                    AddVarToVars(scopeNum, val[0], var);
                }
                break;
        }
    }


    /**
     * this function checks an already existing variable's assignment
     *
     * @param line line to be checked
     * @return true or false
     * @throws InvalidTypeException
     */
    private boolean CheckVar(String line) throws InvalidTypeException, AlreadyDeclaredFinalException {
        // if line is a method call return true
        Pattern methodCallPattern = Pattern.compile(METHOD_CALL_PATTERN);
        Matcher matcher = methodCallPattern.matcher(line);
        if (matcher.find()) {
            return true;
        }

        Pattern assignVar = Pattern.
                compile(ASSIGN_VAR_REGEX);

        matcher = assignVar.matcher(line);
        boolean foundType = false;
        if (matcher.find()) {
            line = line.replaceAll("\\s*;\\s*", "");
            String[] params = line.split("\\s*=\\s*");
            int LHSScope = -1;

            String[] s = new String[]{params[0]};
            ParamsFromMethodCall(s, curMethodParams);
            if(s[0] == null){
                foundType = true;
            }

            else {
                Variable LHS = null;

                for (int i = 0; i < variables.size(); i++) {
                    if (variables.get(i).containsKey(params[0])) {
                        if (variables.get(i).get(params[0]).isFinal()) {
                            throw new AlreadyDeclaredFinalException();
                        }
                        if (variables.get(i).get(params[0]).getValue() == null) {
                            throw new InvalidTypeException();
                        }
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
                                if (!((RHS.getType().equals(INT_TYPE) &&
                                        (LHS.getType().equals(DOUBLE_TYPE) ||
                                                LHS.getType().equals(BOOLEAN_TYPE)))
                                        || (RHS.getType().equals(DOUBLE_TYPE) &&
                                        LHS.getType().equals(BOOLEAN_TYPE)))) {
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

                        String LHS_type = LHS.getType();
                        // check if rhs is the same type as lhs
                        Pattern p = null;
                        switch (LHS_type) {
                            case INT_TYPE:
                                p = Pattern.compile(VALID_INT_VALUE_REGEX);
                                break;
                            case DOUBLE_TYPE:
                                p = Pattern.compile(VALID_DOUBLE_VALUE_REGEX);
                                break;
                            case STRING_TYPE:
                                p = Pattern.compile(VALID_STRING_VALUE_REGEX);
                                break;
                            case BOOLEAN_TYPE:
                                p = Pattern.compile(VALID_BOOLEAN_VALUE_REGEX);
                                break;
                            case CHAR_TYPE:
                                p = Pattern.compile(VALID_CHAR_VALUE_REGEX);
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

                }
            }
        }
        return foundType;
    }

    /**
     * this function checks if method name is valid
     *
     * @param methodName method name
     * @return true or false
     */
    private boolean isMethodNameValid(String methodName) {
        return methodsList.containsKey(methodName);
    }


    /**
     * checks if number of parameters in the method is valid
     *
     * @param givenParams the given parameters in method call
     * @param method      method
     * @return true or false
     */
    private boolean isNumOfParamsValid(String[] givenParams, Method method) {
        return givenParams.length == method.getMethodParameters().size();
    }

    /**
     * this function checks if the method call is valid
     * @param line current line in file
     * @throws IllegalMethodCallException if method call is illegal
     */
    private void CheckMethodCall(String line) throws IllegalMethodCallException {
        Pattern methodCallPattern = Pattern.compile(METHOD_CALL_PATTERN);
        Matcher matcher = methodCallPattern.matcher(line);
        if (matcher.find()) {
            String[] splitLine = line.split(SPLIT_BY_PARENTHESES);
            String methodName = splitLine[0];
            if (isMethodNameValid(methodName)) { //check if method name is valid
                Method method = methodsList.get(methodName);
                String[] givenParams = splitLine[1].split(SPLIT_BY_COMMA);
                ArrayList< Variable> methodParameters = method.getMethodParameters();
                if (!isNumOfParamsValid(givenParams, method)) { //check num of params given is valid
                    throw new IllegalMethodCallException();
                }

                if(curMethodParams != null) {
                    ParamsFromMethodCall(givenParams, methodParameters);
                }
                    checkNameIsInOuterScope(givenParams, methodParameters);

            } else {
                methodsToFind.add(methodName);
            }
        }

    }

    private void ParamsFromMethodCall(String[] givenParams, ArrayList<Variable> methodParameters) {
        for (int i = 0; i < givenParams.length; i++) {
            ArrayList<String> curMethodNames = getVarNames(curMethodParams);
            if (curMethodNames.contains(givenParams[i])) {
                int j = curMethodNames.indexOf(givenParams[i]);
                if (methodParameters.get(i).getType().equals(curMethodParams.get(j).getType())) {
                    givenParams[i] = null;
                }

            }
        }
    }

    private static String[] makeArrFromArrList(ArrayList<String> sendParamsToCheckOuterScope) {
        String[] send = new String[sendParamsToCheckOuterScope.size()];
        int idx = 0 ;
        for (String s : sendParamsToCheckOuterScope){
            send[idx] = s;
            idx++;
        }
        return send;
    }

    private void checkNameIsInOuterScope(String[] givenParams, ArrayList< Variable> methodParameters) throws IllegalMethodCallException {
        int curGivenParamIdx = 0;
        int curScopeIdx = scopeNum;
        for (Variable methodVar : methodParameters) {
            if(givenParams[curGivenParamIdx] == null){
                curGivenParamIdx++;
                continue;
            }
            String methodType = methodVar.getType();
            boolean nameExists = false;
            while (curScopeIdx >= 0 && !nameExists) {
                if (!variables.containsKey(curScopeIdx) || !variables.get(curScopeIdx).containsKey(givenParams[curGivenParamIdx])) { //the
                    // cur scope doesn't contain the varName
                    curScopeIdx--;//check next scope
                } else { //found variable with same name
                    Variable foundVar =
                            variables.get(curScopeIdx).get(givenParams[curGivenParamIdx]);
                    String varType = foundVar.getType();

                    //check if types are valid, if it isn't will throw exception
                    if (!isTypesValid(methodType, varType)) {
                        throw new IllegalMethodCallException();
                    }
                    nameExists = true;
                }
            }//we checked all scopes for this var
            if (!nameExists) {
                nameExists = ParamTypeChecker(methodType, givenParams[curGivenParamIdx]);
                if (!nameExists) {
                    throw new IllegalMethodCallException();
                }
            }
        }
    }

    /**
     * this function checks if types are valid
     * @param methodType method type
     * @param varType variable type
     * @return true or false
     */
    private boolean isTypesValid(String methodType, String varType) {
        return methodType.equals(varType) ||
                (varType.equals(INT_TYPE) && (methodType.equals(DOUBLE_TYPE) ||
                        methodType.equals(BOOLEAN_TYPE))) ||
                (varType.equals(DOUBLE_TYPE) && methodType.equals(BOOLEAN_TYPE));
    }

    /**
     * this function checks the parameters based on their type
     *
     * @param methodType  method type
     * @param givenParams the given parameters
     * @return true or false
     */
    private boolean ParamTypeChecker(String methodType, String givenParams) {
        boolean goodParam = true;
        Matcher intMatcher = intPattern.matcher(givenParams);
        Matcher doubleMatcher = doublePattern.matcher(givenParams);
        Matcher boolMatcher = boolPattern.matcher(givenParams);
        Matcher stringMatcher = stringPattern.matcher(givenParams);
        Matcher charMatcher = charPattern.matcher(givenParams);


        if (!((methodType.equals(BOOLEAN_TYPE) && (intMatcher.find() || doubleMatcher.find() ||
                boolMatcher.find()))
                || (methodType.equals(DOUBLE_TYPE) && (doubleMatcher.find() || boolMatcher.find()))
                || (methodType.equals(INT_TYPE) && intMatcher.find())
                || (methodType.equals(STRING_TYPE) && stringMatcher.find())
                || (methodType.equals(CHAR_TYPE) && charMatcher.find()))) {
            goodParam = false;
        }
        return goodParam;
    }

    /**
     * this function checks if/while blocks
     *
     * @param line current line in file
     * @throws InvalidIfWhileBlockException if while is invalid
     * @throws InvalidTypeException         wrong type
     */
    private void checkIfWhile(String line) throws InvalidIfWhileBlockException, InvalidTypeException {
        Pattern ifWhilePattern = Pattern.compile(VALID_OUTLINE_REGEX_IF_WHILE);
        Matcher matcher = ifWhilePattern.matcher(line);
        if (matcher.find()) {
            String condition = matcher.group(2);
            scopeNum++;
            IfWhileTypeChecker checker = new IfWhileTypeChecker(condition);
            checker.checkValidity();
            ArrayList<String> params = checker.getParamNames();
            for (String param : params) {
                checkIfWhileParameter(param);
            }
        } else {
            throw new InvalidIfWhileBlockException();
        }
    }

    /**
     * this function checks if / while block's parameters
     *
     * @param param parameter to check
     * @throws InvalidTypeException invalid type
     */
    private void checkIfWhileParameter(String param) throws InvalidTypeException {
        boolean foundGoodParam = false;
        for (int i = 0; i < scopeNum; i++) {
            HashMap<String, Variable> varsInScope = variables.get(i);
            for (Variable var : varsInScope.values()) {
                boolean goodType =
                        var.getType().equals(INT_TYPE) ||
                                var.getType().equals(DOUBLE_TYPE) ||
                                var.getType().equals(BOOLEAN_TYPE);
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

    /**
     * this function checks a method line in file
     *
     * @param line current line in file
     * @throws InvalidTypeException
     * @throws IllegalMethodFormatException
     * @throws IllegalNestedMethodException
     * @throws InvalidMethodNameException
     */
    private void checkMethodLine(String line) throws
            InvalidTypeException, IllegalMethodFormatException, IllegalNestedMethodException,
            InvalidMethodNameException {
        if (curMethod != null) {
            throw new IllegalNestedMethodException();
        }
        Pattern methodSuffix = Pattern.compile(METHOD_SUFFIX);
        Matcher matcher = methodSuffix.matcher(line);
        if (line.startsWith(METHOD_PREFIX) && matcher.find()) {

            Pattern pattern = Pattern.compile(SPLIT_LINE_METHOD);
            matcher = pattern.matcher(line);
            boolean throwException = true;
            if (matcher.find()) {
                String method_name = matcher.group(0).split(REGEX_OPEN_PARENTHESIS)[0].strip();
                method_name = method_name.replaceAll("\\s*void\\s*", "");
                if (!methodsList.containsKey(method_name)) {

                    MethodChecker methodChecker = new MethodChecker(matcher.group(1), scopeNum,
                            method_name);
                    methodChecker.checkValidity();
                    throwException = methodChecker.getThrowException();
                    methodsList.put(method_name, methodChecker.getMethod());
                    curMethod = methodChecker.getMethod();
                    this.curMethodParams = new ArrayList<>();
                    curMethodParams.addAll(methodChecker.getMethod().getMethodParams());

                    // if method is in list of undeclared methods remove it
                    methodsToFind.remove(method_name);


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
        if (curMethod != null) {
            checkMethodVariables(scopeNum, name, var);
        }
        if (variables.size() == 0) {
            HashMap<String, Variable> newHash = new HashMap<>();
            newHash.put(name, var);
            variables.put(scopeNum, newHash);
        } else {

            // check if variables map ha a sub-map for the current scope level
            if (!variables.containsKey(scopeNum)) {
                HashMap<String, Variable> newHash = new HashMap<>();
                variables.put(scopeNum, newHash);

                // check if it already contains a var with this name
                if (variables.get(scopeNum).containsKey(name)) {
                    throw new VarNameAlreadyUsed();
                }
            } else {
                variables.get(scopeNum).put(name, var);

                // check if this (global) variable has been used earlier but not yet initialized
                if (globalVarsToFind.containsKey(name)) {
                    if (!globalVarsToFind.get(name).equals(var.getType())) {
                        // if there is another variable with same name but different type
                        throw new VarNameAlreadyUsed();
                    }
                    if (globalVarsToFind.get(name).equals(var.getType())) {
                        // if the previously used variable has been found - remove from map of vars to find
                        globalVarsToFind.remove(name);
                    }
                }
            }
        }
    }

    /**
     * this function checks that the new variables in the method are valid
     *
     * @param scopeNum scope number
     * @param name     name of variable
     * @param var      variable
     */
    private void checkMethodVariables(int scopeNum, String name, Variable var) throws VarNameAlreadyUsed {
        Method method = methodsList.get(curMethod.getMethodName());
        ArrayList<Variable> mVars = method.getMethodParameters();

        ArrayList<String> varNames = getVarNames(mVars);
        if (varNames.contains(name)) {
            int idx =varNames.indexOf(name);
            if (!var.getType().equals(mVars.get(idx).getType())) {
                throw new VarNameAlreadyUsed();
            }
        } else {
            mVars.add( var);
        }
    }

    private ArrayList<String> getVarNames(ArrayList<Variable> variables){
        ArrayList<String> arr = new ArrayList<>();
        for(Variable variable : variables){
            arr.add(variable.getName());
        }
        return arr;
    }

}
