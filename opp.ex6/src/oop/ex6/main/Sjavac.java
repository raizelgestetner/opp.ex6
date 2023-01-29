package oop.ex6.main;

import com.sun.jdi.InvalidTypeException;
import oop.ex6.type_checker.InvalidMethodNameException;
import oop.ex6.type_checker.InvalidVariableException;
import oop.ex6.type_checker.VarNameAlreadyUsed;

import java.io.*;

public class Sjavac {
    private static final String INVALID_FILE_NAME = "Invalid file name";
    private static final int LEGAL_CODE = 0;
    private static final int ILLEGAL_CODE = 1;
    private static final int IO_ERROR = 2;
    private static final String INVALID_TYPE_EXCEPTION_MSG = "Invalid type exception";
    private static final String ILLEGAL_START_OF_LINE = "Illegal start of line";
    private static final String ILLEGAL_METHOD_FORMAT_MSG = "Illegal method format";
    private static final String ILLEGAL_IF_WHILE_BLOCK_MSG = "Illegal if while block";
    private static final String RETURN_ERROR_MSG = "Method must have return at end of the method";
    private static final String ILLEGAL_VARIABLE_DECLARATION_MSG = "Illegal variable declaration";
    private static final String ILLEGAL_NESTED_METHOD_MSG = "Nested methods are illegal";
    private static final String GLOBAL_VARIABLE_EXCEPTION_MSG = "global variable is not initialized";
    private static final String ILLEGAL_NUMBER_OF_SCOPES_MSG = "Illegal number of scopes";
    private static final String ILLEGAL_METHOD_CALL_MSG = "method call is illegal";
    public static final String UNDECLARED_METHOD_ERROR_MSG = "undeclared method error";
    public static final String CAN_T_CHANGE_FINAL_VALUE_MSG = "can't change final value";
    public static final String SAME_NAME_VAR_MSG = "Vars can't have same name as other declared before";
    public static final String INVALID_METHOD_MSG = "Method name is not valid";
    public static final String END_OF_LINE_MSG = "Illegal end of line";
    public static final String RETURN_VALUE_IS_ILLEGAL_MSG = "return value is illegal";

    public static void main(String[] fileName) {

        try (InputStream inputFile = new FileInputStream(fileName[0])) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile));

            // create parser to read file
            Parser parser = new Parser(reader);
            parser.readFile();

            // print 0 if code is legal
            System.out.println(LEGAL_CODE);

        } catch (IOException e) {
            System.out.println(IO_ERROR);
            System.err.println(INVALID_FILE_NAME);

        }
        catch (EndOfLineException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(END_OF_LINE_MSG);
        }
        catch (InvalidTypeException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(INVALID_TYPE_EXCEPTION_MSG);
        }

        catch(StartOfLineException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_START_OF_LINE);
        }
        catch(IllegalMethodFormatException e ){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_METHOD_FORMAT_MSG);
        }
        catch(InvalidIfWhileBlockException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_IF_WHILE_BLOCK_MSG);
        }

        catch (InvalidVariableException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_VARIABLE_DECLARATION_MSG);
        }
        catch (MethodHasNoReturnException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(RETURN_ERROR_MSG);
        } catch (IllegalNestedMethodException e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_NESTED_METHOD_MSG);

        } catch (IllegalMethodCallException e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_METHOD_CALL_MSG);

        } catch (VarNameAlreadyUsed e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println(SAME_NAME_VAR_MSG);
        } catch (InvalidMethodNameException e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println(INVALID_METHOD_MSG);
        }
        catch (AlreadyDeclaredFinalException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(CAN_T_CHANGE_FINAL_VALUE_MSG);
        }
        catch(UndeclaredMethodException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(UNDECLARED_METHOD_ERROR_MSG);
        }

        catch(IllegalNumOfScopesException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_NUMBER_OF_SCOPES_MSG);
        }
        catch (GlobalVariableException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(GLOBAL_VARIABLE_EXCEPTION_MSG);
        } catch (InvalidReturnException e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println(RETURN_VALUE_IS_ILLEGAL_MSG);
        }
    }


}

