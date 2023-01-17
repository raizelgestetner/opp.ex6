package type_checker;

import com.sun.jdi.InvalidTypeException;

/**
 * this class is a factory which creates VariableChecker classes
 */
public class VariableTypeCheckerFactory {

    public static final String STRING = "String";
    private static final String INT = "int";
    private static final String DOUBLE = "double";
    public static final String BOOLEAN = "boolean";
    public static final String CHAR = "char";
    public static final String VOID_TYPE = "void";


    public TypeChecker getType(String variableType, String line,int scopeLevel,String name )
            throws InvalidTypeException {
         switch (variableType) {
             case INT:
                 return new IntTypeChecker(line);
             case DOUBLE:
                 return new DoubleTypeChecker(line);
             case STRING :
                 return new StringTypeChecker(line);
             case BOOLEAN:

                 return new BooleanTypeChecker(line);
             case CHAR:
                 return new CharTypeChecker(line);
             case VOID_TYPE:
                 return new MethodChecker(line,scopeLevel,name);
             case "final":
                return checkNextWord(line);
             case"if":
                 return new IfWhileTypeChecker(line);
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
