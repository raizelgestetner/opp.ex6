package Sjavac;

/**
 * this class is for variables and holds each variable's name,type and value (if initialized)
 */
public class Variable {
    private final boolean isFinal;
    private String name;
    private String type;
    private String value=null;
    private int scopeNum;


    /**
     * constructor
     * @param name
     * @param type
     * @param value
     */
    public Variable(String name, String type, String value,int scopeNum,boolean isFinal) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.scopeNum = scopeNum;
        this.isFinal = isFinal;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public int getScopeNum() {
        return scopeNum;
    }

    /**
     * constructor
     *
     * @param isFinal
     * @param name    name of variable
     * @param type    type of variable
     */
    public Variable( String name, String type, int scopeNum,boolean isFinal) {
        this.isFinal = isFinal;
        this.name = name;
        this.type = type;
        this.scopeNum=scopeNum;
    }

    /**
     * getter
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * getter
     * @return String type
     */
    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
