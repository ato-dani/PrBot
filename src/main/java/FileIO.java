import java.io.*;

/**
 * Basic text file input/output handler.
 */
public class FileIO {

    /**
     * Reads in a text file as a string
     * @param filepath  Filepath of the text file.
     * @return          Contents of the text file.
     */
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

    /**
     * Writes a string out to a text file. Will overwrite if file already exists.
     * @param filepath  Filepath of the text file.
     * @param contents  Text to save.
     * @return          true on success.
     */
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
