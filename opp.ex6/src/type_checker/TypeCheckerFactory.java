package type_checker;

import com.sun.jdi.InvalidTypeException;

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


    public TypeChecker getType(String variableType, String line,int scopeLevel,String name )
            throws InvalidTypeException {
         switch (variableType) {
             case INT_TYPE:
                 return new IntTypeChecker(line);
             case DOUBLE_TYPE:
                 return new DoubleTypeChecker(line);
             case STRING_TYPE :
                 return new StringTypeChecker(line);
             case BOOLEAN_TYPE:

                 return new BooleanTypeChecker(line);
             case CHAR_TYPE:
                 return new CharTypeChecker(line);
             case VOID_TYPE:
                 return new MethodChecker(line,scopeLevel,name);
             case "final":
                return checkNextWord(line);
             case"if":
             case "while":
                 return new IfWhileTypeChecker(line);
             case "return":
                 return new ReturnChecker(line);
             default:
                 throw new InvalidTypeException();
        }

    }

    private TypeChecker checkNextWord(String line) {
    }
}
