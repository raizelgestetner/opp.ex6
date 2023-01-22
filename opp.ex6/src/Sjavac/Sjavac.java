package Sjavac;

import com.sun.jdi.InvalidTypeException;
import type_checker.InvalidMethodName;
import type_checker.VarNameAlreadyUsed;

import java.io.*;

public class Sjavac {

    private static final String INVALID_FILE_NAME = "Invalid file name";
    private static final int LEGAL_CODE = 0;
    private static final int ILLEGAL_CODE = 1;
    private static final int IO_ERROR = 2;

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
        catch (EndOfLineException | InvalidTypeException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println(e.getMessage());
        }

        catch(StartOfLineException e){
            System.out.println(ILLEGAL_CODE);
            System.err.println("Illegal start of line");
        }
        catch(IllegalMethodFormatException e ){
            System.out.println(ILLEGAL_CODE);
            System.err.println("Illegal method format");
        }
        catch(InvalidIfWhileBlock e){
            System.out.println(ILLEGAL_CODE);
            System.err.println("Illegal if while block");
        }
        catch (MethodHasNoReturn e){
            System.out.println(ILLEGAL_CODE);
            System.err.println("Method must have return at end of the method");
        } catch (IllegalNestedMethod e) {
            System.out.println(ILLEGAL_CODE);
            System.err.println("Nested methods are illegal");

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
    }


}

