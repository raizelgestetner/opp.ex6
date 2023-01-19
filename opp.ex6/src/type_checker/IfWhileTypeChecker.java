package type_checker;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IfWhileTypeChecker implements TypeChecker {
    public static final String A_INT_OR_FLOAT_REGEX = "^-?\\d*(.\\d+)?$";

    private String line;
    private static final String AND_CONDITION = "\\s*&&\\s*";

    private ArrayList<String> paramNames;

    public IfWhileTypeChecker(String line) {
        this.line = line;
        paramNames = new ArrayList<>();
    }

    public ArrayList<String> getParamNames() {
        return paramNames;
    }

    @Override
    public void checkValidity() {
        //todo:  I didn't do this
        // todo.. if/while blocks can be nested to a practically unlimited depth (i.e. you should support a
        //todo .. depth of at least java.lang.Integer.MAX VALUE): if inside while and vise versa.

        String conditions = line.replaceAll("\\s*\\|\\|\\s*","&&");
        String [] splitConditions = conditions.split(AND_CONDITION);
        for(String condition : splitConditions){ //todo: need to check if support empty condition
            Pattern intOrDoubleRegex = Pattern.compile(A_INT_OR_FLOAT_REGEX);
            Matcher matcherIntDouble = intOrDoubleRegex.matcher(condition);
            Pattern EmptyConditionRegex = Pattern.compile("^\\s*$");
            Matcher matcherEmptyCondition = EmptyConditionRegex.matcher(condition);
//            if(splitConditions.length==1){
                //check if is an integer or float or boolean true or false and isn't white space
                if(!(condition.equals("false")||condition.equals("true")||
                        matcherIntDouble.find()||!matcherEmptyCondition.find())){
                    // parser will check this list to check if they are valid int char or double parameter
                    // that has already been initialized
                    paramNames.add(splitConditions[0]);
                }
//            }
        }
    }

}
