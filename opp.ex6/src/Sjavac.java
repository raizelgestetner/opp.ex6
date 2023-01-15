import java.io.*;

public class Sjavac  {
    public Sjavac(String fileName) throws FileNotFoundException {
        try (InputStream inputFile = new FileInputStream(fileName)){
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputFile));
            String line;
            while ((line = reader.readLine())!=null){

            }
        }catch (IOException e){
            System.err.println("Invalid file name");

        }
    }

    public static void main(String[] args) {
        System.out.println("test");
    }
}