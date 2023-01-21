package Sjavac;

import com.sun.jdi.InvalidTypeException;
import type_checker.IfWhileTypeChecker;
import type_checker.MethodChecker;
import type_checker.ReturnChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class reads and parses the file
 */
public class Parser {
    public static final String REGEX_OPEN_PARENTHESIS = "\\(";
    public static final String VALID_LINE_PREFIX = "^\\s*(if|while|void|char|String|boolean|double|return" +
            "|int|})\\s*";
    private final BufferedReader reader;
    private HashMap<String, Method> methodsList = new HashMap<>();
    public static HashMap<Integer, HashMap<String , Variable>> variables = new HashMap<>();


    private int scopeNum = 0; // todo count number of brackets and make sure is legal
    private static final String REGEX_COMMENT = "^//.*$";
    private static final String VALID_OUTLINE_REGEX_IF_WHILE = "^(if|while)\\s*\\((.*)\\)\\s*\\{\\s+$";
    private static final String INVALID_PREFIX_REGEX = "^[\\s]*[;{][\\s]*$";
    private static final String VALID_END_OF_LINE_REGEX = ".*[;{}]$";
    private static final String TYPE_PREFIX = "^\b(int|double|boolean|char|String|void|final|if|return)\b";
    private static final String METHOD_PREFIX = "void";
    private Method curMethod; //is null if not in method and otherwise holds the current method of the scope

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
            IllegalMethodFormatException, InvalidTypeException, InvalidIfWhileBlock {
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


            line = reader.readLine();
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
    private void checkLine(String line) throws IllegalMethodFormatException, InvalidTypeException, InvalidIfWhileBlock {
        Pattern pattern = Pattern.compile(VALID_LINE_PREFIX);
        Matcher matcher = pattern.matcher(line);
        if(matcher.find()){
            String first_word = matcher.group(1);
            if(first_word.equals("void")){
                checkMethodLine(line);
            }
            else if(first_word.equals("if")||first_word.equals("while")){
                checkIfElse(line);
            }

            else if (first_word.equals("char")){

            }
            else if(first_word.equals("int")){

            }
            else if (first_word.equals("double")){

            }
            else if(first_word.equals("String")){

            }
            else if(first_word.equals("boolean")){

            }
            else if(first_word.equals("}")){
                //todo need to make curMethod change and scope number change too
                //todo if there are methods that didnt have returen - need to throw exceotions?

            }
            else if (first_word.equals("return")){
                if(scopeNum == 0){
                    throw new InvalidReturn();
                }
                else {
                    ReturnChecker returnChecker = new ReturnChecker(line); //if doesn't throw excetion
                    // means the cur method hase a return value
                    curMethod.hasReturn();
                }

            }
            else if(first_word.equals("}")){
                variables.remove(scopeNum);
                scopeNum--;
//                curMethod = //todo need to update curMethod
            }
        }



        // todo check that regular scope is valid

        //todo check if/else
//        checkIfElse(line);
    }

    private void checkIfElse(String line) throws InvalidIfWhileBlock, InvalidTypeException {
        Pattern ifWhilePattern = Pattern.compile(VALID_OUTLINE_REGEX_IF_WHILE);
        Matcher matcher = ifWhilePattern.matcher(line);
        if(matcher.find()){
            String condition = matcher.group(2);
            scopeNum++;
            IfWhileTypeChecker checker = new IfWhileTypeChecker(condition);
            checker.checkValidity();
            ArrayList<String> params = checker.getParamNames();
            for(String param : params){
                boolean foundGoodParam = false;
                for (int i = 0; i < scopeNum; i++) {
                    HashMap<String, Variable> varsInScope= variables.get(i);
                    for(Variable var : varsInScope.values()){
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
                if(!foundGoodParam){
                    throw new InvalidTypeException();
                }
            }
        }
        else{
            throw new InvalidIfWhileBlock();
        }
    }

    private void checkMethodLine(String line) throws InvalidTypeException, IllegalMethodFormatException {
        if (line.startsWith(METHOD_PREFIX)) {

            line = line.replaceAll(METHOD_PREFIX, VALID_LINE_PREFIX);
            // split line to find method name and parameters
            Pattern pattern = Pattern.compile(SPLIT_LINE_METHOD);
            Matcher matcher = pattern.matcher(line);
            boolean throwException = true;
            if (matcher.find()) {
                String method_name = matcher.group(0).split(REGEX_OPEN_PARENTHESIS)[0].strip();
                if (!methodsList.containsKey(method_name)) { //todo check that this is the correct method name

                    MethodChecker methodChecker = new MethodChecker(matcher.group(1), scopeNum,
                            method_name);
                    methodChecker.checkValidity();
                    throwException = methodChecker.getThrowException();
                    methodsList.put(method_name, methodChecker.getMethod());
                    scopeNum++;
                    curMethod = methodsList.get(method_name);
                }
            }
            if(throwException){
                throw new IllegalMethodFormatException();
            }

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
}
