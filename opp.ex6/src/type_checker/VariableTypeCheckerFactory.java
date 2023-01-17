package type_checker;

import com.sun.jdi.InvalidTypeException;

/**
 * this class is a factory which creates VariableChecker classes
 */
public class VariableTypeCheckerFactory {

    public VariableTypeChecker getType(VariableType variableType,String line)
            throws InvalidTypeException {
         switch (variableType) {
             case INT:
                 return new IntTypeChecker(line);
             case DOUBLE :
                 return new DoubleTypeChecker(line);
             case STRING :
                 return new StringTypeChecker(line);
             case BOOLEAN:
                 return new BooleanTypeChecker(line);
             case CHAR:
                 return new CharTypeChecker(line);
             default:
                 throw new InvalidTypeException(line);
        }

    }
}
