import java.io.*;

public class FileIO {

    public static String readFile(String filepath){
        try {
            File file = new File(filepath);
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            br.close();
            return sb.toString();
        }
        catch(FileNotFoundException e){
            System.out.println("Could not find file " + filepath);
            return null;
        }
        catch(Exception f){
            f.printStackTrace();
            return null;
        }
    }

    public static boolean writeFile(String filepath, String contents){
        try{
            File file = new File(filepath);
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            bw.write(contents);
            bw.close();
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
}
