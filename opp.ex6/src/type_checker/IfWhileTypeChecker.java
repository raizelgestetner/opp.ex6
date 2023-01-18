package type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfWhileTypeChecker implements TypeChecker {
    private final int scopeLevel;
    private String line;
    private static final String AND_CONDITION = "\\s*&&\\s*";


    public IfWhileTypeChecker(String line,int scopeLevel) {
        this.line = line;
        this.scopeLevel = scopeLevel;
    }

    @Override
    public void checkValidity() throws InvalidTypeException {
        String conditions = line.replaceAll("\\s*\\|\\|\\s*","&&");
        String [] splitConditions = conditions.split(AND_CONDITION);
        if(splitConditions.length==1){
            if(!(splitConditions[0].equals("false")||splitConditions[0].equals("true"))){
                // check if word is an int char or double parameter that has already been initialized

            }
        }


    }
}
