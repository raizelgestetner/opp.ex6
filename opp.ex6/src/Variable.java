/**
 * this class is for variables and holds each variable's name,type and value (if initialized)
 */
public class Variable {
    private String name;
    private String type;
    private String value=null;


    /**
     * constructor
     * @param name
     * @param type
     * @param value
     */
    public Variable(String name, String type, String value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    /**
     * constructor
     * @param name name of variable
     * @param type type of variable
     */
    public Variable(String name, String type) {
        this.name = name;
        this.type = type;
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
