package Sjavac;

import com.sun.jdi.InvalidTypeException;
import type_checker.MethodChecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class reads and parses the file
 */
public class Parser {
    public static final String REGEX_OPEN_PARENTHESIS = "\\(";
    private final BufferedReader reader;
    private HashMap<String, Method> methodsList = new HashMap<>();
    private HashMap<String, Variable> globalVariables = new HashMap<>();


    private int numOfBrackets = 0; // todo count number of brackets and make sure is legal
    private static final String REGEX_COMMENT = "^//.*$";
    private static final String INVALID_PREFIX_REGEX = "^[\\s]*[;{}][\\s]*$";
    private static final String VALID_END_OF_LINE_REGEX = ".*[;{}]$";
    private static final String TYPE_PREFIX = "^\b(int|double|boolean|char|String|void|final|if|return)\b";
    private static final String METHOD_PREFIX = "void";

    private static final String SPLIT_LINE_METHOD =
            "^\\w+\\s+\\w+\\s*\\(([\\w\\s]+\\s+\\w+(,\\s*[\\w\\s]+\\s+\\w+)*)?\\)\\s*\\{\\s*$";//todo make sure 2
    // todo backslashes work
//

    /**
     * constructor
     */
    public Parser(BufferedReader reader) {
        this.reader = reader;
    }


    public void readFile() throws IOException, EndOfLineException, StartOfLineException, IllegalMethodFormatException, InvalidTypeException {
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


            // check that line doesn't start with ; { or }
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



    /**
     * after checking that prefix and suffix of the line is valid this function checks that the content of the line is
     * valid. There are 2 options - either there is a method or just regular scope.
     * this function know to check if both options are valid
     *
     * @param line line to check
     */
    private void checkLine(String line) throws IllegalMethodFormatException, InvalidTypeException {
        // todo check for method - if there is check that it is valid
        checkMethodLine(line);

        // todo check that regular scope is valid

        //todo check if/else
    }

    private void checkMethodLine(String line) throws InvalidTypeException, IllegalMethodFormatException {
        if (line.startsWith(METHOD_PREFIX)) {

            line.replaceAll(METHOD_PREFIX, "");
            // split line to find method name and parameters
            Pattern pattern = Pattern.compile(SPLIT_LINE_METHOD);
            Matcher matcher = pattern.matcher(line);
            boolean throwException = true;
            while (matcher.find()) {
                String method_name = matcher.group(0).split(REGEX_OPEN_PARENTHESIS)[0].strip();
                if (!methodsList.containsKey(method_name)) { //todo check that this is the correct method name

                    MethodChecker methodChecker = new MethodChecker(matcher.group(1),numOfBrackets,
                            method_name);
                    methodChecker.checkValidity();
                    throwException = methodChecker.getThrowException();
                    methodsList.put(method_name, methodChecker.getMethod());


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
