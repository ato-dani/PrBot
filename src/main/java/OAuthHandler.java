import org.json.JSONObject;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class OAuthHandler {

    //based on this: https://twitter4j.org/en/code-examples.html
    public static boolean authorizeTwitter(String apiKey, String apiSecretKey) throws Exception {
        RedirectServer server = new RedirectServer();
        server.startUp();
        Twitter twitter = TwitterFactory.getSingleton();
        twitter.setOAuthConsumer(apiKey, apiSecretKey);
        RequestToken requestToken = twitter.getOAuthRequestToken();
        try{
            Desktop desktop = Desktop.getDesktop();
            desktop.browse(new URI(requestToken.getAuthorizationURL()));
        }
        catch(Exception e){
            //todo: print url so user can copy+paste
        }
        while(server.getTwitterResponse() == null){
            //do nothing and wait while the user finishes up in their browser
            Thread.sleep(1);
        }
        //this is hideous, but works
        //this essentially saves the value of the second key-value pair in the URL
        String oAuthVerifier = server.getTwitterResponse().split("\\?")[1].split("\\&")[1].split("=")[1];
        server.shutDown();
        AccessToken accessToken = twitter.getOAuthAccessToken(oAuthVerifier);
        System.out.println("Access token: " + accessToken.getToken());
        System.out.println("Access token secret: " + accessToken.getTokenSecret());
        //todo: actually save these values
        return true;
    }

    //generate random base64 string of [len] bytes
    private static String generateNonce(int len){
        byte[] nonce = new byte[len];
        new SecureRandom().nextBytes(nonce);
        return Base64.getEncoder().encodeToString(nonce);
    }

    //generate the needlessly complex OAuth signature
    private static String generateSignature(String url, Map<String, String> parameters, String requestType, String secretKey, String secretToken){
        try{
            StringBuilder sb = new StringBuilder();
            sb.append(requestType.toUpperCase());
            sb.append("&");
            sb.append(URLEncoder.encode(url, "UTF-8"));
            sb.append("&");
            sb.append(URLEncoder.encode(Requester.buildParameterString(parameters), "UTF-8"));
            String sigBaseString = sb.toString();
            String signingKey = URLEncoder.encode(secretKey, "UTF-8") + "&";
            if(secretToken != null){
                signingKey += URLEncoder.encode(secretToken, "UTF-8");
            }
            byte[] byteHMAC;
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            mac.init(spec);
            byteHMAC = mac.doFinal(sigBaseString.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(byteHMAC);
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /*private static JSONObject formatResponse(String response){
        JSONObject json = new JSONObject();
        //remove the remains of the regular URL, and split on the &
        String[] responsePairs = response.split("\\?")[1].split("\\&");
        for(String pair : responsePairs){
            //add to the json, by splitting key=value into key : value
            String[] pairSplit = pair.split("=");
            json.put(pairSplit[0], pairSplit[1]);
        }
        return json;
    }*/
}
