package Sjavac;

import com.sun.jdi.InvalidTypeException;
import type_checker.InvalidMethodName;
import type_checker.InvalidVariableException;
import type_checker.VarNameAlreadyUsed;

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
            System.err.println("Illegal end of line");
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
        catch(InvalidIfWhileBlock e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_IF_WHILE_BLOCK_MSG);
        }

        catch (InvalidVariableException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_VARIABLE_DECLARATION_MSG);
        }
        catch (MethodHasNoReturn e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(RETURN_ERROR_MSG);
        } catch (IllegalNestedMethod e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println(ILLEGAL_NESTED_METHOD_MSG);

        } catch (IllegalMethodCall e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println("method call is illegal");


        } catch (VarNameAlreadyUsed e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println("Vars can't have same name as other declared before");
        } catch (InvalidMethodName e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println("Method name is not valid");

        }
        catch(UndeclaredMethodException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println("undeclared method error");
        }
    }


}

