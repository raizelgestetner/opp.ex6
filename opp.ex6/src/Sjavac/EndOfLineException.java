package Sjavac;

/**
 * wrong end of line format
 */
public class EndOfLineException extends Exception {
    public EndOfLineException() {
    }

    public EndOfLineException(String message) {
        super(message);
        System.err.println("Invalid end of line");
    }
}
