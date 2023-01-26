package oop.ex6.type_checker;

import com.sun.jdi.InvalidTypeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnChecker implements TypeChecker {
    private final String line;

    public ReturnChecker(String line) {
        this.line = line;

    }

    @Override
    public void checkValidity() throws InvalidTypeException {
        Pattern typePattern = Pattern.compile("^\\s*return\\s*;\\s*$");
        Matcher matcher = typePattern.matcher(line);
        if(!matcher.find()){
            throw new InvalidTypeException();
        }

    }
}
