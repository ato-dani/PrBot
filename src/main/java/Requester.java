import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;

//handles all JSON GET/POST requests
public class Requester {
    private HttpTransport transport;
    private HttpRequestFactory reqFactory;

    private HttpTransport initTransport() {
        if (transport == null) {
            transport = new NetHttpTransport();
        }
        return transport;
    }

    private HttpRequestFactory initReqFactory() {
        if (reqFactory == null) {
            reqFactory = initTransport().createRequestFactory();
        }
        return reqFactory;
    }

    public void setHttpTransport(HttpTransport customHttpTransport) {
        this.transport = customHttpTransport;
    }

    public void setReqFactory(HttpRequestFactory customReqFactory) {
        this.reqFactory = customReqFactory;
    }

    /**
     * Makes a GET request and parses the response to the response type(if it is
     * given one)
     * 
     * @param requestUrl   The request url.
     * @param parameters   A hashmap containing the request parameters /
     *                     queries(null if it doesn't have one).
     * @param headers      A hashmap containing the request headers(null if it
     *                     doesn't have one).
     * @param responseType The type declaring how the response should be
     *                     formatted(see example from RedditMessagePoster class).
     * @return response object with the response parsed based on the type given
     *         above.
     */
    // I am creating this because the makeGETRequest doesn't work(have some bugs)
    // and I couldn't fix them
    public Object makeGETRequest(String requestUrl, Map<String, String> parameters, HttpHeaders headers,
                                 Type responseType) {
        Object respObject = null;
        try {
            GenericUrl url = new GenericUrl(requestUrl);
            if (parameters != null) {
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    url.put(entry.getKey(), entry.getValue());
                }
            }
            HttpRequest req = initReqFactory().buildGetRequest(url);
            req.setParser(new JsonObjectParser(new JacksonFactory()));
            if (headers != null) {
                req.setHeaders(headers);
            }

            HttpResponse resp = req.execute();
            if (responseType != null) {
                respObject = resp.parseAs(responseType);
            }
            System.out.println("message: " + resp.getStatusMessage());
            System.out.println("Code: " + resp.getStatusCode());
            resp.disconnect();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        return respObject;
    }

    /**
     * Makes a POST request and parses the response to the response type(if it is
     * given one)
     * 
     * @param requestUrl   The request url.
     * @param data         A hashmap containing the post data(null if it doesn't
     *                     have one).
     * @param parameters   A hashmap containing the request parameters /
     *                     queries(null if it doesn't have one).
     * @param headers      A hashmap containing the request headers(null if it
     *                     doesn't have one).
     * @param responseType The type declaring how the response should be
     *                     formatted(see example from RedditMessagePoster class).
     * @return response object with the response parsed based on the type given
     *         above.
     */
    public Object makePOSTRequest(String requestUrl, Map<String, String> data, Map<String, String> parameters,
                                  HttpHeaders headers, Type responseType) {
        Object respObject = null;
        try {
            GenericUrl url = new GenericUrl(requestUrl);
            if (parameters != null) {
                for (Map.Entry<String, String> entry : parameters.entrySet()) {
                    url.put(entry.getKey(), entry.getValue());
                }
            }
            HttpContent content = data == null ? new JsonHttpContent(new JacksonFactory(), data) : null;
            HttpRequest req = initReqFactory().buildPostRequest(url, content);
            req.setParser(new JsonObjectParser(new JacksonFactory()));
            if (headers != null) {
                // set headers if any is given
                req.setHeaders(headers);
            }
            HttpResponse resp = req.execute();
            if (responseType != null) {
                // parse response if type of response is specifiec
                respObject = resp.parseAs(responseType);
            }
            System.out.println("message: " + resp.getStatusMessage());
            System.out.println("Code: " + resp.getStatusCode());
            // System.out.println("Data: " + resp.get)
            resp.disconnect();
        } catch (Exception e) {
            System.out.println("Exception: " + e);
            System.out.println("Stack trace: ");
            e.printStackTrace();
        }

        return respObject;
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

    /**
     * Given the query string and the query/paramter key whose value we are looking
     * for, it returns the value assigned to that key
     * 
     * @param query     string of parameters separated by &
     * @param queryName the key whose value we are looking for
     * @return The value of the query or null if the key was not found
     */
    public static String getQueryValue(String query, String queryName) {
        String queriesDelimeter = "&";
        String queryValueDelimeter = "=";
        if (query == null || query.indexOf(queryName) == -1) {
            return null;
        }
        return (query.substring(query.indexOf(queryName)).split(queriesDelimeter)[0]).split(queryValueDelimeter)[1];
    }

    /**
     * Convert an InputStream to a String of its contents.
     * @param inputStream   InputStream to convert.
     * @return              String contents.
     * @throws IOException  Incapable of reading InputStream.
     */
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
