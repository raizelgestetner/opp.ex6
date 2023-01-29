package oop.ex6.type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class is a factory which creates VariableChecker oop.ex6.classes
 */
public class TypeCheckerFactory {

    public static final String STRING_TYPE = "String";
    private static final String INT_TYPE = "int";
    private static final String DOUBLE_TYPE = "double";
    public static final String BOOLEAN_TYPE = "boolean";
    public static final String CHAR_TYPE = "char";
    public static final String VOID_TYPE = "void";
    public static final String FINAL_TYPE = "final";

    /**
     * the factory that builds the type checker needed
     * @param variableType type of checks we want
     * @param line the line to check the var on
     * @param scopeLevel the scope of this line
     * @param name name of the variable
     * @param isFinal if var is final
     * @return the wanted type checker
     * @throws InvalidTypeException the type given in line isn't valid for this typy
     * @throws InvalidVariableException the variable assigned is invalid
     * @throws VarNameAlreadyUsed the name of the var was already used in this scope
     */
    public static TypeChecker getType(String variableType, String line, int scopeLevel, String name,
                               boolean isFinal )
            throws InvalidTypeException, InvalidVariableException, VarNameAlreadyUsed {
         switch (variableType) {
             case INT_TYPE:
                 return new IntTypeChecker(line,scopeLevel,isFinal);
             case DOUBLE_TYPE:
                 return new DoubleTypeChecker(line,scopeLevel,isFinal);
             case STRING_TYPE :
                 return new StringTypeChecker(line,scopeLevel,isFinal);
             case BOOLEAN_TYPE:

                 return new BooleanTypeChecker(line,scopeLevel,isFinal);
             case CHAR_TYPE:
                 return new CharTypeChecker(line,scopeLevel,isFinal);
             case VOID_TYPE:
                 return new MethodChecker(line,scopeLevel,name);
             case FINAL_TYPE:
                return checkNextWord(line, scopeLevel);
             case"if":
             case "while":
                 return new IfWhileTypeChecker(line);
             case "return":
                 return new ReturnChecker(line);
             default:
                 throw new InvalidTypeException();
        }

    }

    /**
     * is for final type , after knowing the line is of type final we need to check if the rest of the
     * line can be transformed to a checker
     * @param line same as above
     * @param scopeLevel same as above
     * @return the type checker
     * @throws InvalidTypeException same as above
     * @throws InvalidVariableException same as above
     * @throws VarNameAlreadyUsed same as above
     */
    private static TypeChecker checkNextWord(String line ,int scopeLevel) throws InvalidTypeException
            , InvalidVariableException, VarNameAlreadyUsed {
        Pattern pattern = Pattern.compile("final\\s+(\\w+)");
        Matcher match = pattern.matcher(line);
        int start = match.start();

        String next_word ="";
        if (match.find()){
            next_word = match.group(1);
        }
        else{
            throw new InvalidTypeException();
        }
        return getType(next_word,line.substring(start+next_word.length()),scopeLevel,null,true);
    }
}
