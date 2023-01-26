package type_checker;

import Sjavac.Variable;
import com.sun.jdi.InvalidTypeException;

import java.util.HashMap;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * this class is a factory which creates VariableChecker classes
 */
public class TypeCheckerFactory {

    public static final String STRING_TYPE = "String";
    private static final String INT_TYPE = "int";
    private static final String DOUBLE_TYPE = "double";
    public static final String BOOLEAN_TYPE = "boolean";
    public static final String CHAR_TYPE = "char";
    public static final String VOID_TYPE = "void";
    public static final String FINAL_TYPE = "final";
//    private final HashMap<Integer, HashMap<String, Variable>> variables;

//    public TypeCheckerFactory( HashMap<Integer, HashMap<String, Variable>> variables){
//        this.variables = variables;
//    }


    public TypeChecker getType(String variableType, String line, int scopeLevel, String name,
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

    private TypeChecker checkNextWord(String line ,int scopeLevel) throws InvalidTypeException
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
