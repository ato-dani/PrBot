import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

//handles all JSON GET/POST requests
public class Requester {

    public static String makeGETRequest(String url, Map<String, String> parameters){
        try{
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            for(Map.Entry<String, String> entry : parameters.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            out.flush();
            out.close();
            connection.setRequestProperty("Content-Type", "application/json");
            int status = connection.getResponseCode();
            //TODO: actually pay attention to this
            System.out.println("STATUS CODE: " + status);
            connection.disconnect();
            return inputStreamToString(connection.getInputStream());
        }
        catch(Exception e){
            e.printStackTrace();
            //TODO: should probably do more error checking than just this...
            return null;
        }
    }

    public static String makePOSTRequest(String url, Map<String, String> parameters, String data){
        try{
            HttpURLConnection connection = (HttpURLConnection) (new URL(url)).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            for(Map.Entry<String, String> entry : parameters.entrySet()){
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            if(data != null) {
                OutputStream out = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                osw.write(data);
                osw.flush();
                osw.close();
                out.close();
            }
            connection.connect();
            int status = connection.getResponseCode();
            //todo: actually pay attention to this
            System.out.println("STATUS CODE: " + status);
            return inputStreamToString(connection.getInputStream());
        }
        catch(Exception e){
            e.printStackTrace();
            //TODO: should probably do more error checking than just this...
            return null;
        }
    }

    public static String buildParameterString(Map<String, String> parameters) throws UnsupportedEncodingException{
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry : parameters.entrySet()){
            sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            sb.append("=");
            sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            sb.append("&");
        }
        String toReturn = sb.toString();
        if(toReturn.length() > 0){
            toReturn = toReturn.substring(0, toReturn.length() - 1);
        }
        return toReturn;
    }

    public static Map<String, String> decodeParameterString(String parameterString){
        TreeMap<String, String> parameters = new TreeMap<>();
        String[] params = parameterString.split("&");
        for(String s : params){
            String[] split = s.split("=", 2);
            parameters.put(split[0], split[1]);
        }
        return parameters;
    }

    public static String inputStreamToString(InputStream inputStream) throws IOException{
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int length;
        while((length = inputStream.read(buffer)) != -1){
            out.write(buffer, 0, length);
        }
        return out.toString(StandardCharsets.UTF_8.name());
    }
}
